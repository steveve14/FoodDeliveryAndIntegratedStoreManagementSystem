// middleware/auth.global.ts
export default defineNuxtRouteMiddleware((to, from) => {
  if (to.path === '/login') return

  const user = useCookie('auth_token') 

  if (!user.value) {
    return navigateTo('/login')
  }
})