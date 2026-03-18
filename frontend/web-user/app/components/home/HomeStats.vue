<script setup lang="ts">
import { useHomeUserSource } from '~/composables/useHomeUserSource';
import type { Period, Range } from '~/types';

const props = defineProps<{
  period: Period;
  range: Range;
}>();

function formatCurrency (value: number): string {
  return new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0,
  }).format(value);
}

function formatNumber (value: number): string {
  return new Intl.NumberFormat('ko-KR').format(value);
}

const { data: source } = await useHomeUserSource();

const rangeMs = computed(() => {
  return Math.max(0, props.range.end.getTime() - props.range.start.getTime());
});

const previousRange = computed(() => {
  const prevEnd = new Date(props.range.start.getTime() - 1);
  const prevStart = new Date(prevEnd.getTime() - rangeMs.value);
  return { start: prevStart, end: prevEnd };
});

const currentOrders = computed(() => {
  return (source.value?.orders ?? []).filter((order) => {
    const createdAt = new Date(order.createdAt).getTime();
    return createdAt >= props.range.start.getTime() && createdAt <= props.range.end.getTime();
  });
});

const prevOrders = computed(() => {
  return (source.value?.orders ?? []).filter((order) => {
    const createdAt = new Date(order.createdAt).getTime();
    return createdAt >= previousRange.value.start.getTime() && createdAt <= previousRange.value.end.getTime();
  });
});

const currentRevenue = computed(() => {
  return currentOrders.value.reduce((sum, order) => sum + order.totalAmount, 0);
});

const prevRevenue = computed(() => {
  return prevOrders.value.reduce((sum, order) => sum + order.totalAmount, 0);
});

const currentDelivering = computed(() => {
  return currentOrders.value.filter(order => order.status === 'DELIVERING').length;
});

const prevDelivering = computed(() => {
  return prevOrders.value.filter(order => order.status === 'DELIVERING').length;
});

const currentDone = computed(() => {
  return currentOrders.value.filter(order => order.status === 'DONE').length;
});

const prevDone = computed(() => {
  return prevOrders.value.filter(order => order.status === 'DONE').length;
});

function variationPercent (current: number, previous: number): number {
  if (previous <= 0) {
    return current > 0 ? 100 : 0;
  }

  return Math.round(((current - previous) / previous) * 100);
}

const stats = computed(() => {
  return [
    {
      title: '기간 내 주문수',
      icon: 'i-lucide-shopping-bag',
      value: `${formatNumber(currentOrders.value.length)}건`,
      variation: variationPercent(currentOrders.value.length, prevOrders.value.length),
      link: '/orders',
    },
    {
      title: '기간 내 매출',
      icon: 'i-lucide-banknote',
      value: formatCurrency(currentRevenue.value),
      variation: variationPercent(currentRevenue.value, prevRevenue.value),
      link: '/orders',
    },
    {
      title: '현재 배달 중',
      icon: 'i-lucide-bike',
      value: `${currentDelivering.value}건`,
      variation: variationPercent(currentDelivering.value, prevDelivering.value),
      link: '/orders',
    },
    {
      title: '완료 주문',
      icon: 'i-lucide-circle-check',
      value: `${currentDone.value}건`,
      variation: variationPercent(currentDone.value, prevDone.value),
      link: '/orders',
    },
  ];
});
</script>

<template>
  <UPageGrid class="grid grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 lg:gap-px">
    <UPageCard
      v-for="(stat, index) in stats"
      :key="index"
      :icon="stat.icon"
      :title="stat.title"
      :to="stat.link"
      variant="subtle"
      :ui="{
        title: 'uppercase',
      }"
      class="lg:rounded-none first:rounded-l-lg last:rounded-r-lg transition-colors"
    >
      <template #description>
        <div class="flex items-center gap-2 mt-1">
          <span class="text-2xl font-bold text-highlighted">
            {{ stat.value }}
          </span>

          <UBadge
            :color="stat.variation > 0 ? 'success' : 'error'"
            variant="subtle"
            class="text-xs font-medium px-1.5"
          >
            {{ stat.variation > 0 ? "+" : "" }}{{ stat.variation }}%
          </UBadge>
        </div>
      </template>
    </UPageCard>
  </UPageGrid>
</template>
