export default defineNuxtPlugin(() => {
  const auth = useAuth();
  const userSession = useCookie('user-session');

  if (!userSession.value) {
    return;
  }

  auth
    .refresh()
    .then((ok) => {
      if (!ok) {
        userSession.value = null;
        navigateTo('/login');
      }
    })
    .catch(() => {
      userSession.value = null;
      navigateTo('/login');
    });
});
