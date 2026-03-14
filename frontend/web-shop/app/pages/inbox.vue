<script setup lang="ts">
import { formatTimeAgo } from '@vueuse/core';
import type { UseTimeAgoMessages } from '@vueuse/core';
import type { Mail } from '~/types';

const { data: threads } = await useFetch<Mail[]>('/api/mails', {
  default: () => [],
});

const koMessages: UseTimeAgoMessages = {
  justNow: '방금 전',
  past: n => (n.match(/\d/) ? `${n} 전` : n),
  future: n => (n.match(/\d/) ? `${n} 후` : n),
  month: n => `${n}개월`,
  year: n => `${n}년`,
  day: n => `${n}일`,
  week: n => `${n}주`,
  hour: n => `${n}시간`,
  minute: n => `${n}분`,
  second: n => `${n}초`,
  invalid: '유효하지 않은 날짜',
};

function formatUpdatedAt (dateString: string) {
  return formatTimeAgo(new Date(dateString), { messages: koMessages });
}
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="메시지함" :badge="threads.length" />

    <div class="p-4 space-y-3">
      <UPageCard
        v-for="thread in threads"
        :key="thread.id"
        :title="thread.from.name"
        :description="formatUpdatedAt(thread.date)"
        variant="subtle"
      >
        <div class="flex items-center justify-between gap-3">
          <p class="text-sm text-muted">{{ thread.subject }}</p>
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
