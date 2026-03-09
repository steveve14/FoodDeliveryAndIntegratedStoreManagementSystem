<script setup lang="ts">
interface DailySales {
  date: string;
  orders: number;
  gross: number;
}

const reports = ref<DailySales[]>([
  { date: '03/05', orders: 58, gross: 1260000 },
  { date: '03/06', orders: 63, gross: 1390000 },
  { date: '03/07', orders: 49, gross: 1115000 },
]);

const totalGross = computed(() =>
  reports.value.reduce((acc, cur) => acc + cur.gross, 0),
);

const formatPrice = (price: number) =>
  new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0,
  }).format(price);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="판매 리포트">
      <template #right>
        <UBadge color="primary" variant="subtle"
          >
합계 {{ formatPrice(totalGross) }}
</UBadge
        >
      </template>
    </UDashboardNavbar>

    <div class="p-4">
      <UTable :data="reports" />
    </div>
  </UDashboardPanel>
</template>
