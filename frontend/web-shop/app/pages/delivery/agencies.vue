<script setup lang="ts">
interface Agency {
  name: string;
  status: 'normal' | 'delay' | 'error';
  feeRate: string;
  lastSync: string;
}

const agencies = ref<Agency[]>([
  { name: '생생배달', status: 'normal', feeRate: '12%', lastSync: '방금 전' },
  { name: '바로고', status: 'delay', feeRate: '11.5%', lastSync: '5분 전' },
  {
    name: '우리동네퀵',
    status: 'error',
    feeRate: '10.8%',
    lastSync: '18분 전',
  },
]);

const badgeColor = (status: Agency['status']) => {
  if (status === 'normal') {
    return 'success';
  }
  if (status === 'delay') {
    return 'warning';
  }
  return 'error';
};

const statusLabel = (status: Agency['status']) => {
  if (status === 'normal') {
    return '정상';
  }
  if (status === 'delay') {
    return '지연';
  }
  return '장애';
};
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="배달 대행사 설정" :badge="agencies.length" />

    <div class="p-4 grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
      <UPageCard
        v-for="agency in agencies"
        :key="agency.name"
        :title="agency.name"
        :description="`수수료율 ${agency.feeRate}`"
        variant="subtle"
      >
        <div class="flex items-center justify-between text-sm">
          <span class="text-muted">연동 상태</span>
          <UBadge :color="badgeColor(agency.status)" variant="subtle">
            {{ statusLabel(agency.status) }}
          </UBadge>
        </div>
        <div class="mt-2 text-sm text-muted">
          마지막 동기화: {{ agency.lastSync }}
        </div>
      </UPageCard>
    </div>
  </UDashboardPanel>
</template>
