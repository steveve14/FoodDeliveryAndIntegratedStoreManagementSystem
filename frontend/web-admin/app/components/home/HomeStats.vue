<script setup lang="ts">
import { useAdminHomeSource } from '~/composables/useAdminHomeSource';
import type { Period, Range, Stat } from '~/types';

const props = defineProps<{
  period: Period;
  range: Range;
}>();

function formatCurrency (value: number): string {
  return value.toLocaleString('ko-kr', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0,
  });
}

const { data: source } = await useAdminHomeSource();

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

const previousOrders = computed(() => {
  return (source.value?.orders ?? []).filter((order) => {
    const createdAt = new Date(order.createdAt).getTime();
    return createdAt >= previousRange.value.start.getTime() && createdAt <= previousRange.value.end.getTime();
  });
});

function variationPercent (current: number, previous: number): number {
  if (previous <= 0) {
    return current > 0 ? 100 : 0;
  }
  return Math.round(((current - previous) / previous) * 100);
}

const stats = computed<Stat[]>(() => {
  const revenue = currentOrders.value.reduce((sum, order) => sum + order.totalAmount, 0);
  const previousRevenue = previousOrders.value.reduce((sum, order) => sum + order.totalAmount, 0);
  const pendingOrders = currentOrders.value.filter(order => order.status !== 'DONE' && order.status !== 'CANCELLED').length;
  const previousPendingOrders = previousOrders.value.filter(order => order.status !== 'DONE' && order.status !== 'CANCELLED').length;

  return [
    {
      title: '고객',
      icon: 'i-lucide-users',
      value: source.value?.customers.length ?? 0,
      variation: variationPercent(source.value?.customers.length ?? 0, source.value?.customers.length ?? 0),
      link: '/customer',
    },
    {
      title: '등록 가게',
      icon: 'i-lucide-store',
      value: source.value?.stores.length ?? 0,
      variation: variationPercent(source.value?.stores.length ?? 0, source.value?.stores.length ?? 0),
      link: '/stores',
    },
    {
      title: '총 수익',
      icon: 'i-lucide-coins',
      value: formatCurrency(revenue),
      variation: variationPercent(revenue, previousRevenue),
      link: '/finance/transactions',
    },
    {
      title: '진행 중 주문',
      icon: 'i-lucide-clipboard-list',
      value: pendingOrders,
      variation: variationPercent(pendingOrders, previousPendingOrders),
      link: '/operation/orders',
    },
  ];
});
</script>

<template>
  <UPageGrid class="lg:grid-cols-4 gap-4 sm:gap-6 lg:gap-px">
    <UPageCard
      v-for="(stat, index) in stats"
      :key="index"
      :icon="stat.icon"
      :title="stat.title"
      :to="stat.link"
      variant="subtle"
      :ui="{
        container: 'gap-y-1.5',
        wrapper: 'items-start',
        leading:
          'p-2.5 rounded-full bg-primary/10 ring ring-inset ring-primary/25 flex-col',
        title: 'font-normal text-muted text-xs uppercase',
      }"
      class="lg:rounded-none first:rounded-l-lg last:rounded-r-lg hover:z-1"
    >
      <div class="flex items-center gap-2">
        <span class="text-2xl font-semibold text-highlighted">
          {{ stat.value }}
        </span>

        <UBadge
          :color="stat.variation > 0 ? 'success' : 'error'"
          variant="subtle"
          class="text-xs"
        >
          {{ stat.variation > 0 ? "+" : "" }}{{ stat.variation }}%
        </UBadge>
      </div>
    </UPageCard>
  </UPageGrid>
</template>
