<script setup lang="ts">
import type { NuxtError } from '#app'

defineProps<{
  error: NuxtError
}>()

useSeoMeta({
  title: '페이지를 찾을 수 없습니다',
  description: '죄송하지만 요청하신 페이지를 찾을 수 없습니다.'
})

useHead({
  htmlAttrs: {
    lang: 'ko'
  }
})
</script>

<template>
  <UApp>
    <UError :error="error" />
  </UApp>
</template>
