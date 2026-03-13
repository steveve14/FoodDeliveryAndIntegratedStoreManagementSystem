// app/composables/useAuth.ts
import type { UserRole } from '~/types/api';

export type { UserRole } from '~/types/api';

export interface AuthUser {
  id: string;
  email: string;
  name: string;
  role: UserRole;
}

export const useAuth = () => {
  const { $api } = useApi();
  const user = useCookie<AuthUser | null>('user-session', {
    maxAge: 60 * 60 * 24 * 7, // 7일
  });

  /** 이메일/비밀번호 로그인 */
  const login = async (email: string, password: string) => {
    const res = await $api<AuthUser>('/api/v1/auth/login', {
      method: 'POST',
      body: { email, password },
    });
    if (!res.success) {
      throw new Error(res.error || '로그인에 실패했습니다.');
    }

    user.value = res.data;
  };

  /** 로그아웃 */
  const logout = async () => {
    try {
      await $api('/api/v1/auth/logout', {
        method: 'POST',
      });
    } catch (error) {
    // 필요하다면 에러 로그를 남깁니다.
      console.error('Logout failed:', error);
    } finally {
    // 상태 초기화와 같은 부수 효과(Side Effect)만 수행
      user.value = null;
    }

    // return은 finally 밖에서 안전하게 처리
    return navigateTo('/login');
  };

  /** 토큰 갱신 */
  const refresh = async (): Promise<boolean> => {
    try {
      const res = await $api<AuthUser>('/api/v1/auth/refresh', {
        method: 'POST',
      });
      if (res.success && res.data) {
        user.value = res.data;
        return true;
      }
    } catch {
      // refresh 실패 시 무시
    }
    return false;
  };

  const isLoggedIn = computed(() => !!user.value);
  const isAdmin = computed(() => user.value?.role === 'ADMIN');
  const isStore = computed(() => user.value?.role === 'STORE');
  const isGeneral = computed(() => user.value?.role === 'USER');

  return {
    user,
    login,
    logout,
    refresh,
    isLoggedIn,
    isAdmin,
    isStore,
    isGeneral,
  };
};
