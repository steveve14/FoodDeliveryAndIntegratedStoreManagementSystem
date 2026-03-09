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

const title = '배달의 맛 - 음식 배달 서비스';
const description =
  '맛있는 음식을 빠르고 편리하게 주문하세요. 다양한 맛집과 메뉴를 한곳에서 만나보세요.';

useSeoMeta({
  title,
  description,
  ogTitle: title,
  ogDescription: description,
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
