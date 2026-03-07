export default defineNuxtPlugin(() => {
  // 앱 시작 시 리프레시 토큰으로 세션 복원 시도
  const auth = useAuth()
  const accessToken = useCookie('access-token')
  const refreshToken = useCookie('refresh-token')
  const userSession = useCookie('user-session')

  // 리프레시 토큰이 없으면 세션 정리만 수행 (미들웨어가 리다이렉트 처리)
  if (!refreshToken.value) {
    accessToken.value = null
    userSession.value = null
    return
  }

  // 비동기적으로 시도하고 실패 시 세션 정리
  auth
    .refresh()
    .then((ok) => {
      if (!ok) {
        // 리프레시 실패 -> 로컬 세션 정리 (미들웨어가 로그인 페이지로 리다이렉트)
        accessToken.value = null
        refreshToken.value = null
        userSession.value = null
        navigateTo('/login')
      }
    })
    .catch(() => {
      // 네트워크/백엔드 오류 시에도 안전하게 세션 정리
      accessToken.value = null
      refreshToken.value = null
      userSession.value = null
      navigateTo('/login')
    })
})
