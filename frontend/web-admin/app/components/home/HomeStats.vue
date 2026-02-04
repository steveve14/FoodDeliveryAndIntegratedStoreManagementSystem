<script setup lang="ts">
import type { Period, Range, Stat } from '~/types'

const props = defineProps<{
  period: Period
  range: Range
}>()

function formatCurrency(value: number): string {
  return value.toLocaleString('ko-kr', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0
  })
}

// 1. 각 항목별로 이동할 링크(link)를 정의합니다.
const baseStats = [{
  title: '사용자',
  icon: 'i-lucide-users',
  minValue: 400,
  maxValue: 1000,
  minVariation: -15,
  maxVariation: 25,
  link: '/customers' // 사용자 목록 페이지
}, {
  title: '등록 가게',
  icon: 'i-lucide-store',
  minValue: 1000,
  maxValue: 2000,
  minVariation: -10,
  maxVariation: 20,
  link: '/stores' // (예시) 가게 목록 페이지
}, {
  title: '총 수익',
  icon: 'i-lucide-coins',
  minValue: 200000,
  maxValue: 500000,
  minVariation: -20,
  maxVariation: 30,
  formatter: formatCurrency,
  link: '/finance' // (예시) 수익 관련 페이지
}, {
  title: '오류 내역',
  icon: 'i-lucide-alert-triangle',
  minValue: 100,
  maxValue: 300,
  minVariation: -5,
  maxVariation: 15,
  link: '/error'
}]

const { data: stats } = await useAsyncData<Stat[]>('stats', async () => {
  return baseStats.map((stat) => {
    const value = randomInt(stat.minValue, stat.maxValue)
    const variation = randomInt(stat.minVariation, stat.maxVariation)

    return {
      title: stat.title,
      icon: stat.icon,
      value: stat.formatter ? stat.formatter(value) : value,
      variation,
      link: stat.link // 2. 링크 정보를 반환 객체에 포함시킵니다.
    }
  })
}, {
  watch: [() => props.period, () => props.range],
  default: () => []
})
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
        leading: 'p-2.5 rounded-full bg-primary/10 ring ring-inset ring-primary/25 flex-col',
        title: 'font-normal text-muted text-xs uppercase'
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
          {{ stat.variation > 0 ? '+' : '' }}{{ stat.variation }}%
        </UBadge>
      </div>
    </UPageCard>
  </UPageGrid>
</template>