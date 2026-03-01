export type UserRole = 'USER' | 'STORE' | 'ADMIN'

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

    const payload = parseJwt(res.data.accessToken)
    if (payload) {
      user.value = {
        id: payload.sub,
        email,
        name: email.split('@')[0]!,
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

  const isLoggedIn = computed(() => !!user.value)

  return {
    user,
    login,
    logout,
    isLoggedIn
  }
}
