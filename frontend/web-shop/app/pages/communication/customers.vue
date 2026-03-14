<script setup lang="ts">
import { formatTimeAgo } from '@vueuse/core';
import type { UseTimeAgoMessages } from '@vueuse/core';

interface Customer {
  id: string;
  name: string;
  orders: number;
  lastOrderAt: string | null;
  grade: 'vip' | 'regular';
}

const { data: customers } = await useFetch<Customer[]>('/api/customers', {
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

function formatLastOrderAt (value: string | null) {
  if (!value) {
    return '주문 없음';
  }

  return formatTimeAgo(new Date(value), { messages: koMessages });
}
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="단골 관리" :badge="customers.length" />

    <div class="p-4">
      <UTable :data="customers">
        <template #name-cell="{ row }">
          <span class="font-medium">{{ row.original.name }}</span>
        </template>

        <template #lastOrderAt-cell="{ row }">
          <span class="text-sm text-muted">{{ formatLastOrderAt(row.original.lastOrderAt) }}</span>
        </template>

        <template #grade-cell="{ row }">
          <UBadge
            :color="row.original.grade === 'vip' ? 'warning' : 'neutral'"
            variant="subtle"
          >
            {{ row.original.grade === "vip" ? "VIP" : "일반" }}
          </UBadge>
        </template>
      </UTable>
    </div>
  </UDashboardPanel>
</template>
