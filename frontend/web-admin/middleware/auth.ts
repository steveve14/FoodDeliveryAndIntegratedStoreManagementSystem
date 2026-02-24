// middleware/auth.global.ts
export default defineNuxtRouteMiddleware((to, from) => {
  const { user } = useAuth()

  // 1. 로그인이 안 된 상태에서 로그인 페이지가 아닌 곳으로 갈 때 -> 로그인 페이지로 리다이렉트
  if (!user.value && to.path !== '/login') {
    return navigateTo('/login')
  }

  // 2. 이미 로그인 된 상태에서 로그인 페이지로 가려고 할 때 -> 메인 페이지로 리다이렉트
  if (user.value && to.path === '/login') {
    return navigateTo('/')
  }

  const userRole = user.value.role

  // 3. 시스템 경로는 ADMIN만 접근 가능
  if (to.path.startsWith('/system') && userRole !== 'ADMIN') {
    return navigateTo('/unauthorized')
  }

  // 4. 가게 경로는 ADMIN/STORE만 접근 가능
  if (to.path.startsWith('/stores') && !['ADMIN', 'STORE'].includes(userRole)) {
    return navigateTo('/unauthorized')
  }
})