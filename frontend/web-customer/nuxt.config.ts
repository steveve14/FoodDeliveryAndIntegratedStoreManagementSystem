// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: [
    '@nuxt/ui',     // Nuxt UI 추가 (Tailwind CSS 포함됨)
    '@pinia/nuxt'   // Pinia (기존에 있었다면 유지)
  ],

  // Backend Gateway 프록시 — /api/v1/** 요청을 Spring Cloud Gateway로 전달
  nitro: {
    routeRules: {
      '/api/v1/**': { proxy: 'http://localhost:8000/api/v1/**' }
    }
  },

  devtools: { enabled: true }
})