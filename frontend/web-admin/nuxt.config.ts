// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: [
    '@nuxt/eslint',
    '@nuxt/ui',
    '@vueuse/nuxt',
  ],

  devtools: {
    enabled: true,
  },

  css: ['~/assets/css/main.css'],

  // 런타임 환경 변수 — .env 파일에서 오버라이드 가능
  runtimeConfig: {
    public: {
      apiBaseUrl: process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8000',
    },
  },

  compatibilityDate: '2024-07-11',

  // Backend Gateway 프록시 — /api/v1/** 요청을 Spring Cloud Gateway로 전달
  nitro: {
    routeRules: {
      '/api/v1/**': { proxy: `${process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8000'}/api/v1/**` },
    },
  },

  eslint: {
    config: {
      stylistic: {
        commaDangle: 'never',
        braceStyle: '1tbs',
      },
    },
  },
});
