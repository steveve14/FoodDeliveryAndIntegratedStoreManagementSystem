<script setup lang="ts">
const colorMode = useColorMode();

const color = computed(() =>
  colorMode.value === 'dark' ? '#1b1718' : 'white',
);

useHead({
  meta: [
    { charset: 'utf-8' },
    { name: 'viewport', content: 'width=device-width, initial-scale=1' },
    { key: 'theme-color', name: 'theme-color', content: color },
  ],
  link: [{ rel: 'icon', href: '/favicon.ico' }],
  htmlAttrs: {
    lang: 'ko',
  },
});

const title = '푸드딜리버리 통합 관리자';
const description =
  '음식 배달 및 통합 매장 관리 시스템의 관리자 대시보드입니다. 매장, 고객, 주문, 결제, 마케팅 등을 한 곳에서 관리할 수 있습니다.';

useSeoMeta({
  title,
  description,
  ogTitle: title,
  ogDescription: description,
  ogImage: 'https://ui.nuxt.com/assets/templates/nuxt/dashboard-light.png',
  twitterImage: 'https://ui.nuxt.com/assets/templates/nuxt/dashboard-light.png',
  twitterCard: 'summary_large_image',
});
</script>

<template>
  <UApp>
    <NuxtLoadingIndicator />

    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </UApp>
</template>
