export default defineNuxtRouteMiddleware((to, from) => {
  const { user } = useAuth()

  // 1. 이미 로그인 상태인데 로그인 페이지로 가려고 할 때 -> 메인으로 보냄
  if (user.value && to.path === '/login') {
    return navigateTo('/')
  }

  // 2. 로그인 상태가 아닌데 로그인 페이지가 아닌 곳을 가려고 할 때 -> 로그인으로 보냄
  if (!user.value && to.path !== '/login') {
    return navigateTo('/login')
  }
})