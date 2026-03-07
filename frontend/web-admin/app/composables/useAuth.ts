// app/composables/useAuth.ts
import type { TokenResponse, UserRole } from '~/types/api'

export type { UserRole } from '~/types/api'

export interface AuthUser {
  id: string
  email: string
  name: string
  role: UserRole
}

/** JWT payload에서 사용자 정보 추출 */
function parseJwt(token: string): { sub: string, role: string, exp: number } | null {
  try {
    const base64Url = token.split('.')[1]!
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const json = decodeURIComponent(
      atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
    )
    return JSON.parse(json)
  } catch {
    return null
  }
}

export const useAuth = () => {
  const { $api, accessToken } = useApi()
  const refreshToken = useCookie('refresh-token')
  const user = useCookie<AuthUser | null>('user-session', {
    maxAge: 60 * 60 * 24 * 7 // 7일
  })

  /** 이메일/비밀번호 로그인 */
  const login = async (email: string, password: string) => {
    const res = await $api<{ accessToken: string, refreshToken: string }>('/api/v1/auth/login', {
      method: 'POST',
      body: { email, password }
    })
    if (!res.success) throw new Error(res.error || '로그인에 실패했습니다.')

    accessToken.value = res.data.accessToken
    refreshToken.value = res.data.refreshToken

    // JWT 디코드 → 사용자 정보 저장
    const payload = parseJwt(res.data.accessToken)
    if (payload) {
      user.value = {
        id: payload.sub,
        email,
        name: email.split('@')[0]!, // 프로필 API 연동 전 임시 이름
        role: payload.role as UserRole
      }
    }
  }

  /** 로그아웃 */
  const logout = async () => {
    try {
      if (refreshToken.value) {
        await $api('/api/v1/auth/logout', {
          method: 'POST',
          body: { refreshToken: refreshToken.value }
        })
      }
    } finally {
      accessToken.value = null
      refreshToken.value = null
      user.value = null
      return navigateTo('/login')
    }
  }

  /** 토큰 갱신 */
  const refresh = async (): Promise<boolean> => {
    if (!refreshToken.value) return false
    try {
      const res = await $api<{ accessToken: string, refreshToken: string }>('/api/v1/auth/refresh', {
        method: 'POST',
        body: { refreshToken: refreshToken.value }
      })
      if (res.success) {
        accessToken.value = res.data.accessToken
        refreshToken.value = res.data.refreshToken
        return true
      }
    } catch {
      // refresh 실패 시 무시
    }
    return false
  }

  const isLoggedIn = computed(() => !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isStore = computed(() => user.value?.role === 'STORE')
  const isGeneral = computed(() => user.value?.role === 'USER')

  return {
    user,
    login,
    logout,
    refresh,
    isLoggedIn,
    isAdmin,
    isStore,
    isGeneral
  }
}