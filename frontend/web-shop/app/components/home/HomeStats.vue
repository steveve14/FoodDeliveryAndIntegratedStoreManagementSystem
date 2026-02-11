<script setup lang="ts">
import type { Period, Range } from "~/types";

const props = defineProps<{
  period: Period;
  range: Range;
}>();

function formatCurrency(value: number): string {
  return new Intl.NumberFormat("ko-KR", {
    style: "currency",
    currency: "KRW",
    maximumFractionDigits: 0,
  }).format(value);
}

function formatNumber(value: number): string {
  return new Intl.NumberFormat("ko-KR").format(value);
}

const baseStats = [
  {
    title: "기간 내 주문수",
    icon: "i-lucide-shopping-bag",
    minValue: 50,
    maxValue: 150,
    minVariation: -10,
    maxVariation: 20,
    formatter: (v: number) => `${formatNumber(v)}건`,
    link: "/orders",
  },
  {
    title: "기간 내 매출",
    icon: "i-lucide-banknote",
    minValue: 1500000,
    maxValue: 3000000,
    minVariation: -5,
    maxVariation: 15,
    formatter: formatCurrency,
    link: "/finance/reports",
  },
  {
    title: "현재 배달 중",
    icon: "i-lucide-bike",
    minValue: 3,
    maxValue: 12,
    minVariation: 0,
    maxVariation: 5,
    formatter: (v: number) => `${v}건`,
    link: "/delivery/tracking",
  },
  {
    title: "신규 리뷰",
    icon: "i-lucide-star",
    minValue: 2,
    maxValue: 10,
    minVariation: -2,
    maxVariation: 5,
    formatter: (v: number) => `${v}건`,
    link: "/communication/reviews",
  },
];

const { data: stats } = await useAsyncData(
  "home-stats",
  async () => {
    return baseStats.map((stat) => {
      const value =
        Math.floor(Math.random() * (stat.maxValue - stat.minValue + 1)) +
        stat.minValue;
      const variation =
        Math.floor(
          Math.random() * (stat.maxVariation - stat.minVariation + 1),
        ) + stat.minVariation;

      return {
        title: stat.title,
        icon: stat.icon,
        value: stat.formatter ? stat.formatter(value) : value,
        variation,
        link: stat.link,
      };
    });
  },
  {
    watch: [() => props.period, () => props.range],
    default: () => [],
  },
);
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
