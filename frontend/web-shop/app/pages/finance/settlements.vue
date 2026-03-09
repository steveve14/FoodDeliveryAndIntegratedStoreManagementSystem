<script setup lang="ts">
interface Settlement {
  id: string;
  period: string;
  gross: number;
  fee: number;
  net: number;
  status: 'scheduled' | 'paid';
}

const settlements = ref<Settlement[]>([
  {
    id: 'SET-2026-03-01',
    period: '2026-03-01 ~ 2026-03-03',
    gross: 3120000,
    fee: 374400,
    net: 2745600,
    status: 'paid',
  },
  {
    id: 'SET-2026-03-02',
    period: '2026-03-04 ~ 2026-03-06',
    gross: 3560000,
    fee: 427200,
    net: 3132800,
    status: 'scheduled',
  },
]);

const formatPrice = (price: number) =>
  new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0,
  }).format(price);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="정산 내역" :badge="settlements.length" />

    <div class="p-4 space-y-3">
      <UPageCard
        v-for="item in settlements"
        :key="item.id"
        :title="item.id"
        :description="item.period"
        variant="subtle"
      >
        <div class="grid gap-2 text-sm sm:grid-cols-2">
          <p>총매출: {{ formatPrice(item.gross) }}</p>
          <p>수수료: {{ formatPrice(item.fee) }}</p>
          <p class="font-semibold">실정산금: {{ formatPrice(item.net) }}</p>
          <div>
            <UBadge
              :color="item.status === 'paid' ? 'success' : 'warning'"
              variant="subtle"
            >
              {{ item.status === "paid" ? "지급 완료" : "지급 예정" }}
            </UBadge>
          </div>
        </div>
      </UPageCard>
    </div>
  </UDashboardPanel>
</template>
