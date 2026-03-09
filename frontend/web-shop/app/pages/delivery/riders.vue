<script setup lang="ts">
interface Rider {
  id: number;
  name: string;
  phone: string;
  vehicle: string;
  agency: string;
  rating: number;
  status: 'active' | 'offline';
}

const riders = ref<Rider[]>([
  {
    id: 1,
    name: '김배달',
    phone: '010-1234-5678',
    vehicle: '오토바이',
    agency: '생생배달',
    rating: 4.9,
    status: 'active',
  },
  {
    id: 2,
    name: '이라이더',
    phone: '010-9876-5432',
    vehicle: '전기자전거',
    agency: '바로고',
    rating: 4.7,
    status: 'offline',
  },
]);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="배달원/기사 관리" :badge="riders.length">
      <template #right>
        <UButton label="기사 추가" icon="i-lucide-plus" color="primary" />
      </template>
    </UDashboardNavbar>

    <div class="p-4">
      <UTable :data="riders">
        <template #name-cell="{ row }">
          <div class="font-medium">{{ row.original.name }}</div>
        </template>

        <template #status-cell="{ row }">
          <UBadge
            :color="row.original.status === 'active' ? 'success' : 'neutral'"
            variant="subtle"
          >
            {{ row.original.status === "active" ? "운행 가능" : "오프라인" }}
          </UBadge>
        </template>

        <template #rating-cell="{ row }"
          >
★ {{ row.original.rating.toFixed(1) }}
</template
        >
      </UTable>
    </div>
  </UDashboardPanel>
</template>
