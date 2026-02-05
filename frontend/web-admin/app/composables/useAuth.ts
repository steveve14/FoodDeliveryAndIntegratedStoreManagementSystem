export const useAuth = () => {
  // 로그인된 사용자 정보를 담는 상태
  const user = useState<{ email: string; name: string } | null>('user', () => null)

  // 로그인 함수 (실제 API 연동 필요)
  const login = async (email: string) => {
    // 예: await $fetch('/api/login', ...)
    
    // 임시로 유저 정보 세팅
    user.value = { email, name: '사용자' }
    
    // 로그인 후 메인 페이지로 이동
    return navigateTo('/')
  }

  // 로그아웃 함수
  const logout = async () => {
    user.value = null
    return navigateTo('/login')
  }

  return {
    user,
    login,
    logout
  }
}