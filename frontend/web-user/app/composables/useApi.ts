/**
 * Backend API 호출 유틸리티
 * Nitro 프록시를 통해 Gateway(localhost:8000)의 /api/v1/** 엔드포인트를 호출합니다.
 * 인증 토큰은 httpOnly 쿠키로 브라우저가 자동 전송합니다.
 */

/** Backend 공통 응답 래퍼 */
export interface ApiResponse<T = unknown> {
  success: boolean;
  data: T;
  error: string | null;
}

interface AuthSession {
  id: string;
  email: string;
  name: string;
  role: 'USER' | 'STORE' | 'ADMIN';
}

let refreshPromise: Promise<boolean> | null = null;

export const useApi = () => {
  const userSession = useCookie<AuthSession | null>('user-session', {
    maxAge: 60 * 60 * 24 * 7,
  });

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
   * $fetch 래퍼 — httpOnly 쿠키가 자동으로 포함되며 401 시 refresh를 시도합니다.
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
    } catch (err) {
      const error = err as { response?: { status: number } };
      if (error?.response?.status === 401) {
        const refreshed = await tryRefreshToken();
        if (refreshed) {
          return await doFetch();
        }

        // SSR에서는 쿠키를 지우지 않습니다. (레이아웃 SSR 데이터 로드 실패 시 즉시 로그아웃되는 문제 방지)
        if (import.meta.client) {
          userSession.value = null;
        }

        // SSR 컨텍스트에서 navigateTo를 호출하면 런타임 에러가 나므로 클라이언트에서만 이동합니다.
        if (import.meta.client) {
          await navigateTo('/login');
        }

        return {
          success: false,
          data: null as T,
          error: 'UNAUTHORIZED',
        };
      }
      throw err;
    }
  }

  return { $api };
};
