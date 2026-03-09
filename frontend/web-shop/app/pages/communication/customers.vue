<script setup lang="ts">
interface Customer {
  id: number;
  name: string;
  orders: number;
  lastOrderAt: string;
  grade: 'vip' | 'regular';
}

const customers = ref<Customer[]>([
  { id: 1, name: '김단골', orders: 42, lastOrderAt: '오늘', grade: 'vip' },
  { id: 2, name: '이손님', orders: 7, lastOrderAt: '2일 전', grade: 'regular' },
  { id: 3, name: '박고객', orders: 19, lastOrderAt: '어제', grade: 'regular' },
]);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="단골 관리" :badge="customers.length" />

    <div class="p-4">
      <UTable :data="customers">
        <template #name-cell="{ row }">
          <span class="font-medium">{{ row.original.name }}</span>
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
