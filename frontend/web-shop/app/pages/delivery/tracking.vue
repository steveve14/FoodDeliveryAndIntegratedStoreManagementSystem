<script setup lang="ts">
interface RiderTracking {
  id: number;
  name: string;
  phone: string;
  area: string;
  activeOrder: string;
  etaMinutes: number;
  status: 'driving' | 'waiting';
}

const riders = ref<RiderTracking[]>([
  {
    id: 1,
    name: '김배달',
    phone: '010-1234-5678',
    area: '강남역 2km 반경',
    activeOrder: '#2026030704',
    etaMinutes: 12,
    status: 'driving',
  },
  {
    id: 2,
    name: '이라이더',
    phone: '010-9876-5432',
    area: '역삼/논현',
    activeOrder: '-',
    etaMinutes: 0,
    status: 'waiting',
  },
]);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="라이더 실시간 위치" :badge="riders.length" />

    <div class="p-4 grid gap-4 xl:grid-cols-2">
      <UPageCard
        title="실시간 운행 현황"
        description="지도 API 연동 전까지는 요약 카드 형태로 상태를 제공합니다."
        variant="subtle"
      >
        <div class="space-y-3">
          <div
            v-for="rider in riders"
            :key="rider.id"
            class="rounded-lg border border-default p-3"
          >
            <div class="flex items-center justify-between gap-3">
              <div>
                <p class="font-semibold">
                  {{ rider.name }} ({{ rider.phone }})
                </p>
                <p class="text-sm text-muted">권역: {{ rider.area }}</p>
                <p class="text-sm text-muted">
                  현재 배정: {{ rider.activeOrder }}
                </p>
              </div>
              <UBadge
                :color="rider.status === 'driving' ? 'success' : 'neutral'"
                variant="subtle"
              >
                {{
                  rider.status === "driving"
                    ? `운행중 · ${rider.etaMinutes}분`
                    : "대기중"
                }}
              </UBadge>
            </div>
          </div>
        </div>
      </UPageCard>

      <UPageCard
        title="배차 참고 지표"
        description="최근 30분 기준 운영 수치"
        variant="subtle"
      >
        <div class="grid grid-cols-2 gap-3 text-sm">
          <div class="rounded-lg bg-elevated p-3">
            <p class="text-muted">평균 픽업 시간</p>
            <p class="text-lg font-semibold">6분</p>
          </div>
          <div class="rounded-lg bg-elevated p-3">
            <p class="text-muted">평균 배달 시간</p>
            <p class="text-lg font-semibold">18분</p>
          </div>
          <div class="rounded-lg bg-elevated p-3">
            <p class="text-muted">지연 주문</p>
            <p class="text-lg font-semibold">2건</p>
          </div>
          <div class="rounded-lg bg-elevated p-3">
            <p class="text-muted">현재 가용 라이더</p>
            <p class="text-lg font-semibold">1명</p>
          </div>
        </div>
      </UPageCard>
    </div>
  </UDashboardPanel>
</template>
