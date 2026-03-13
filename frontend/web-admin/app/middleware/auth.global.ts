export default defineNuxtRouteMiddleware((to) => {
  const { user } = useAuth();

  const isAuthenticated = !!user.value;

  // 인증이 필요 없는 페이지 목록
  const publicPages = ['/login', '/unauthorized'];
  const isPublicPage = publicPages.includes(to.path);

  // 1. 로그인이 안 되어 있는데 보호된 페이지로 가려고 할 때 → 로그인 페이지로 리다이렉트
  if (!isAuthenticated && !isPublicPage) {
    return navigateTo('/login');
  }

  // 2. 이미 로그인되어 있는데 로그인 페이지로 가려고 할 때 → 대시보드로 리다이렉트
  if (isAuthenticated && to.path === '/login') {
    return navigateTo('/');
  }

  // 3. 관리자 페이지는 ADMIN 역할만 접근 가능
  if (isAuthenticated && !isPublicPage) {
    if (user.value?.role !== 'ADMIN') {
      return navigateTo('/unauthorized');
    }
  }
});
