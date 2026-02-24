// app/composables/useAuth.ts
export type UserRole = 'USER' | 'STORE' | 'ADMIN'

export interface AuthUser {
  email: string
  name: string
  role: UserRole
  avatar?: string
}

export const useAuth = () => {
  const user = useCookie<AuthUser | null>('user-session', {
    maxAge: 60 * 60 * 24 * 7 // 7일간 유지
  })

  const loginWithGoogle = () => {
    window.location.href = '/api/auth/google'
  }

  const logout = async () => {
    user.value = null
    return navigateTo('/login')
  }

  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isStore = computed(() => user.value?.role === 'STORE')
  const isGeneral = computed(() => user.value?.role === 'USER')

  return {
    user,
    loginWithGoogle,
    logout,
    isAdmin,
    isStore,
    isGeneral
  }
}