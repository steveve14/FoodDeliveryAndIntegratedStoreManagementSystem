<script setup lang="ts">
const { user } = useAuth();

definePageMeta({
  layout: 'auth',
});
</script>

<template>
  <div
    class="flex flex-col items-center justify-center min-h-[60vh] gap-6 text-center p-4"
  >
    <UIcon name="i-lucide-shield-alert" class="w-20 h-20 text-red-500" />

    <div>
      <h1 class="text-2xl font-bold mb-2">권한이 없습니다</h1>
      <p class="text-gray-500 dark:text-gray-400">
        현재 계정({{ user?.email }})으로는 이 페이지에 접근할 수 없습니다.<br>
        올바른 권한이 있는 페이지로 이동하시겠습니까?
      </p>
    </div>

    <UButton
      variant="ghost"
      color="success"
      label="다른 계정으로 로그인"
      icon="i-lucide-logout"
      @click="useAuth().logout()"
    />
  </div>
</template>
