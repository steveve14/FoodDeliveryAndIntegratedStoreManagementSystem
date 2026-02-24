<script setup lang="ts">
const { user, isAdmin, isStore } = useAuth();

definePageMeta({
  layout: "auth",
});

type ButtonColor =
  | "primary"
  | "warning"
  | "secondary"
  | "success"
  | "info"
  | "error"
  | "neutral"
  | "gray";

interface NavLink {
  label: string;
  icon: string;
  to: string;
  color: ButtonColor;
}

const links = computed<NavLink[]>(() => {
  const items: NavLink[] = [];

  if (isAdmin.value) {
    items.push({
      label: "시스템 관리자로 이동",
      icon: "i-lucide-shield-check",
      to: "/system/audit",
      color: "primary",
    });
  }
  if (isStore.value || isAdmin.value) {
    items.push({
      label: "가게 관리 페이지로 이동",
      icon: "i-lucide-store",
      to: "/stores",
      color: "warning",
    });
  }
  items.push({
    label: "홈으로 이동",
    icon: "i-lucide-home",
    to: "/",
    color: "gray",
  });

  return items;
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
        현재 계정({{ user?.email }})으로는 이 페이지에 접근할 수 없습니다.<br />
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
