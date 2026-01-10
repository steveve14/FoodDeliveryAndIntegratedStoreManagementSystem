// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: [
    '@nuxt/ui',     // Nuxt UI 추가 (Tailwind CSS 포함됨)
    '@pinia/nuxt'   // Pinia (기존에 있었다면 유지)
  ],

  // 개발 서버 프록시 설정 (기존 설정 유지)
  nitro: {
    routeRules: {
      '/api/**': { proxy: 'http://localhost:8080/api/**' }
    }
  },

  devtools: { enabled: true }
})