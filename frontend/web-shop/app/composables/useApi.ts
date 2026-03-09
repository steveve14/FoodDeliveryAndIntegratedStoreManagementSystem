/**
 * Backend API 호출 유틸리티
 * Nitro 프록시를 통해 Gateway(localhost:8000)의 /api/v1/** 엔드포인트를 호출합니다.
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
   * $fetch 래퍼 — httpOnly 쿠키를 자동 전송하고 401 시 refresh를 시도합니다.
   */
  async function $api<T> (url: string, options?: Parameters<typeof $fetch>[1]): Promise<ApiResponse<T>> {
    const doFetch = () => {
      return $fetch<ApiResponse<T>>(url, {
        ...options,
        credentials: 'include',
        headers: {
          ...(options?.headers as Record<string, string> | undefined),
        },
      });
    };

    try {
      return await doFetch();
    } catch (err: any) {
      if (err?.response?.status === 401) {
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
