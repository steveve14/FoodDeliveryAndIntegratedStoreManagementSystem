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

  runtimeConfig: {
    public: {
      apiBaseUrl: process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8000',
    },
  },

  devServer: {
    port: 3200,
  },

  compatibilityDate: '2025-05-05',

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
