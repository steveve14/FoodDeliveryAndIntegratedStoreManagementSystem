<script setup lang="ts">
const { user } = useAuth();

const roleLabelMap: Record<string, string> = {
  USER: '일반 사용자',
  STORE: '가게 관리자',
  ADMIN: '시스템 관리자',
};

const currentRoleLabel = computed(() => {
  const role = user.value?.role;
  if (!role) {
    return '알 수 없음';
  }
  return roleLabelMap[role] ?? role;
});

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
        현재 계정({{ user?.email }})의 권한은 {{ currentRoleLabel }}입니다.<br>
        이 서비스(web-user)는 일반 사용자(USER) 계정만 이용할 수 있습니다.
      </p>
    </div>

    <UButton
      variant="ghost"
      color="primary"
      label="다른 계정으로 로그인"
      icon="i-lucide-log-out"
      @click="useAuth().logout()"
    />
  </div>
</template>
