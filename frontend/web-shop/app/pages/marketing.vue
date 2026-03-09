<script setup lang="ts">
const couponEnabled = ref(true);
const campaignName = ref('주말 재주문 쿠폰');
const campaignMessage = ref('2만원 이상 주문 시 2,000원 즉시 할인 쿠폰 지급');
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="마케팅 도구" />

    <div class="p-4 grid gap-4 xl:grid-cols-2">
      <UPageCard title="쿠폰 캠페인" variant="subtle">
        <div class="space-y-4">
          <UFormField label="캠페인명">
            <UInput v-model="campaignName" />
          </UFormField>
          <UFormField label="노출 문구">
            <UTextarea v-model="campaignMessage" :rows="4" />
          </UFormField>
          <UFormField label="활성화">
            <USwitch v-model="couponEnabled" />
          </UFormField>
          <UButton label="캠페인 저장" color="primary" />
        </div>
      </UPageCard>

      <UPageCard
        title="간단 통계"
        description="최근 7일 기준 쿠폰/재주문 지표"
        variant="subtle"
      >
        <div class="grid grid-cols-2 gap-3 text-sm">
          <div class="rounded-lg bg-elevated p-3">
            <p class="text-muted">쿠폰 사용률</p>
            <p class="text-lg font-semibold">18.2%</p>
          </div>
          <div class="rounded-lg bg-elevated p-3">
            <p class="text-muted">재주문율</p>
            <p class="text-lg font-semibold">34.5%</p>
          </div>
          <div class="rounded-lg bg-elevated p-3">
            <p class="text-muted">신규 고객</p>
            <p class="text-lg font-semibold">21명</p>
          </div>
          <div class="rounded-lg bg-elevated p-3">
            <p class="text-muted">단골 전환</p>
            <p class="text-lg font-semibold">7명</p>
          </div>
        </div>
      </UPageCard>
    </div>
  </UDashboardPanel>
</template>
