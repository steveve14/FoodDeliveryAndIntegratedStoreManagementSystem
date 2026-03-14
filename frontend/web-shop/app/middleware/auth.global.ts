export default defineNuxtRouteMiddleware((to) => {
  const { user } = useAuth();

  const publicPages = ['/login', '/unauthorized'];
  const isPublicPage = publicPages.includes(to.path);

  // 1. 이미 로그인 상태인데 로그인 페이지로 가려고 할 때 -> 메인으로 보냄
  if (user.value && to.path === '/login') {
    return navigateTo('/');
  }

  // 2. 로그인 상태가 아닌데 보호된 페이지를 가려고 할 때 -> 로그인으로 보냄
  if (!user.value && !isPublicPage) {
    return navigateTo('/login');
  }

  // 3. 가게 관리 페이지는 STORE 역할만 접근 가능
  if (user.value && !isPublicPage && user.value.role !== 'STORE') {
    return navigateTo('/unauthorized');
  }
});
