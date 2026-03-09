<script setup lang="ts">
interface MessageThread {
  id: number;
  customer: string;
  preview: string;
  updatedAt: string;
  unread: boolean;
}

const threads = ref<MessageThread[]>([
  {
    id: 1,
    customer: '김고객',
    preview: '사장님, 소스 하나 더 부탁드려요!',
    updatedAt: '방금 전',
    unread: true,
  },
  {
    id: 2,
    customer: '이단골',
    preview: '리뷰 이벤트 아직 진행 중인가요?',
    updatedAt: '12분 전',
    unread: false,
  },
]);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="메시지함" :badge="threads.length" />

    <div class="p-4 space-y-3">
      <UPageCard
        v-for="thread in threads"
        :key="thread.id"
        :title="thread.customer"
        :description="thread.updatedAt"
        variant="subtle"
      >
        <div class="flex items-center justify-between gap-3">
          <p class="text-sm text-muted">{{ thread.preview }}</p>
          <UBadge v-if="thread.unread" color="primary" variant="soft"
            >
NEW
</UBadge
          >
        </div>
      </UPageCard>
    </div>
  </UDashboardPanel>
</template>
