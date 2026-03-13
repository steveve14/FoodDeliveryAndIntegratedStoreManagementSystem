/**
 * Backend API 호출 유틸리티
 * Nitro 프록시를 통해 Gateway(localhost:8000)의 /api/v1/** 엔드포인트를 호출합니다.
 *
 * 주요 기능:
 * - Authorization 헤더 자동 첨부
 * - 401 응답 시 refreshToken으로 자동 갱신 후 원래 요청 재시도
 * - 갱신 실패 시 로그인 페이지로 리다이렉트
 */

import type { ApiResponse } from '~/types/api';

interface AuthSession {
  id: string;
  email: string;
  name: string;
  role: 'USER' | 'STORE' | 'ADMIN';
}

/** 토큰 갱신 중복 방지를 위한 싱글턴 프로미스 */
let refreshPromise: Promise<boolean> | null = null;

export const useApi = () => {
  const userSession = useCookie<AuthSession | null>('user-session', {
    maxAge: 60 * 60 * 24 * 7,
  });

  /**
   * refreshToken으로 accessToken 갱신 (중복 방지)
   */
  async function tryRefreshToken (): Promise<boolean> {
    if (refreshPromise) {
      return refreshPromise;
    }

    refreshPromise = (async () => {
      try {
        const res = await $fetch<ApiResponse<AuthSession>>('/api/v1/auth/refresh', {
          method: 'POST',
          credentials: 'include',
        });
        if (res.success && res.data) {
          userSession.value = res.data;
          return true;
        }
        return false;
      } catch {
        return false;
      } finally {
        refreshPromise = null;
      }
    })();

    return refreshPromise;
  }

  /**
   * $fetch 래퍼 — Authorization 헤더 자동 첨부 + 401 자동 갱신 재시도
   * @param url - API 경로 (예: '/api/v1/stores')
   * @param options - $fetch 옵션
   * @returns ApiResponse<T> 형태의 응답
   */
  async function $api<T> (url: string, options?: Parameters<typeof $fetch>[1]): Promise<ApiResponse<T>> {
    const doFetch = () => {
      return $fetch<ApiResponse<T>>(url, {
        ...options,
        credentials: 'include',
        headers: {
          ...(userSession.value?.id ? { 'X-User-Id': userSession.value.id } : {}),
          ...(userSession.value?.role ? { 'X-User-Role': userSession.value.role } : {}),
          ...(options?.headers as Record<string, string> | undefined),
        },
      });
    };

    try {
      return await doFetch();
    } catch (err: unknown) {
      if (
        typeof err === 'object' &&
        err !== null &&
        'response' in err &&
        typeof (err as { response?: { status?: number } }).response?.status === 'number' &&
        (err as { response?: { status?: number } }).response?.status === 401
      ) {
        const refreshed = await tryRefreshToken();
        if (refreshed) {
          return await doFetch();
        }

        userSession.value = null;
        navigateTo('/login');
      }
      throw err;
    }
  }

  return { $api };
};
