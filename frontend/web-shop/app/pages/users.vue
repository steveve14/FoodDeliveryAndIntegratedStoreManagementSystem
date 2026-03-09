<script setup lang="ts">
interface UserItem {
  id: number;
  name: string;
  role: string;
  phone: string;
  joinedAt: string;
}

const users = ref<UserItem[]>([
  {
    id: 1,
    name: '홍길동',
    role: '매장 관리자',
    phone: '010-1111-2222',
    joinedAt: '2025-09-12',
  },
  {
    id: 2,
    name: '김아르바',
    role: '주방',
    phone: '010-3333-4444',
    joinedAt: '2026-01-03',
  },
]);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="사용자 관리" :badge="users.length">
      <template #right>
        <UButton
          label="사용자 추가"
          icon="i-lucide-user-plus"
          color="primary"
        />
      </template>
    </UDashboardNavbar>

    <div class="p-4">
      <UTable :data="users" />
    </div>
  </UDashboardPanel>
</template>
