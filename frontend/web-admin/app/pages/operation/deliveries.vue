<script setup lang="ts">
import * as z from 'zod';
import type { FormSubmitEvent } from '@nuxt/ui';
import type { DeliveryDto, DeliveryStatus } from '~/types/api';
import { useDeliveryApi } from '~/composables/api/useDeliveryApi';

const UBadge = resolveComponent('UBadge');
const UButton = resolveComponent('UButton');

const toast = useToast();
const { getDelivery, updateDeliveryStatus } = useDeliveryApi();

// ── 상태 ──────────────────────────────────────────────────
const searchId = ref('');
const loading = ref(false);
const updating = ref(false);
const delivery = ref<DeliveryDto | null>(null);
const error = ref<string | null>(null);

// ── 배달 상태 메타 ─────────────────────────────────────────
const STATUS_META: Record<
  DeliveryStatus,
  {
    label: string;
    color: 'success' | 'warning' | 'primary' | 'error' | 'neutral' | 'info';
  }
> = {
  SCHEDULED: { label: '배달 예약됨', color: 'info' },
  PENDING: { label: '대기중', color: 'neutral' },
  PICKED_UP: { label: '픽업 완료', color: 'primary' },
  IN_TRANSIT: { label: '이동중', color: 'warning' },
  DELIVERED: { label: '배달 완료', color: 'success' },
  CANCELLED: { label: '취소됨', color: 'error' },
};

const NEXT_STATUS: Partial<Record<DeliveryStatus, DeliveryStatus[]>> = {
  SCHEDULED: ['PENDING', 'CANCELLED'],
  PENDING: ['PICKED_UP', 'CANCELLED'],
  PICKED_UP: ['IN_TRANSIT'],
  IN_TRANSIT: ['DELIVERED', 'CANCELLED'],
};

// ── 검색 ──────────────────────────────────────────────────
const searchSchema = z.object({
  deliveryId: z.string().min(1, '배달 ID를 입력해주세요'),
});

async function onSearch (e: FormSubmitEvent<{ deliveryId: string }>) {
  error.value = null;
  delivery.value = null;
  loading.value = true;
  try {
    const res = await getDelivery(e.data.deliveryId);
    if (res.success) {
      delivery.value = res.data;
    } else {
      error.value = '배달 정보를 찾을 수 없습니다.';
    }
  } catch {
    error.value = '배달 조회 중 오류가 발생했습니다.';
  } finally {
    loading.value = false;
  }
}

// ── 상태 변경 ──────────────────────────────────────────────
async function changeStatus (status: DeliveryStatus) {
  if (!delivery.value) {
    return;
  }
  updating.value = true;
  try {
    const res = await updateDeliveryStatus(delivery.value.id, status);
    if (res.success) {
      delivery.value = res.data;
      toast.add({
        title: '상태 변경 완료',
        description: `배달 상태가 "${STATUS_META[status].label}"으로 변경되었습니다.`,
        color: 'success',
      });
    }
  } catch {
    toast.add({
      title: '오류',
      description: '상태 변경에 실패했습니다.',
      color: 'error',
    });
  } finally {
    updating.value = false;
  }
}

const currencyFmt = new Intl.NumberFormat('ko-KR', {
  style: 'currency',
  currency: 'KRW',
});
</script>

<template>
  <UDashboardPanel id="deliveries">
    <template #header>
      <UDashboardNavbar title="배달 관리">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="max-w-2xl mx-auto space-y-6 p-4">
        <!-- 검색 폼 -->
        <UForm
          :schema="searchSchema"
          :state="{ deliveryId: searchId }"
          class="flex gap-2"
          @submit="onSearch"
        >
          <UFormField name="deliveryId" class="flex-1">
            <UInput
              v-model="searchId"
              placeholder="배달 ID를 입력하세요 (UUID)"
              icon="i-lucide-search"
              class="w-full"
            />
          </UFormField>
          <UButton
            type="submit"
            label="조회"
            :loading="loading"
            icon="i-lucide-search"
          />
        </UForm>

        <!-- 오류 -->
        <UAlert
          v-if="error"
          color="error"
          variant="subtle"
          icon="i-lucide-alert-circle"
          :title="error"
        />

        <!-- 배달 상세 카드 -->
        <UCard v-if="delivery">
          <template #header>
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <UIcon name="i-lucide-bike" class="text-xl" />
                <div>
                  <p class="font-semibold text-sm text-muted">배달 ID</p>
                  <p class="font-mono text-xs">{{ delivery.id }}</p>
                </div>
              </div>
              <UBadge
                :color="STATUS_META[delivery.status].color"
                variant="subtle"
                size="lg"
              >
                {{ STATUS_META[delivery.status].label }}
              </UBadge>
            </div>
          </template>

          <div class="space-y-4">
            <div class="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p class="text-muted mb-0.5">주문 ID</p>
                <p class="font-mono text-xs">{{ delivery.orderId }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">배달 기사</p>
                <p>{{ delivery.courier || "미배정" }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">배달비</p>
                <p class="font-semibold text-primary">
                  {{ currencyFmt.format(delivery.deliveryFee) }}
                </p>
              </div>
              <div>
                <p class="text-muted mb-0.5">예약 시각</p>
                <p>
                  {{
                    delivery.scheduledAt
                      ? new Date(delivery.scheduledAt).toLocaleString("ko-KR")
                      : "-"
                  }}
                </p>
              </div>
            </div>

            <!-- 상태 변경 -->
            <div v-if="NEXT_STATUS[delivery.status]?.length">
              <p class="text-sm font-medium mb-2">상태 변경</p>
              <div class="flex flex-wrap gap-2">
                <UButton
                  v-for="next in NEXT_STATUS[delivery.status]"
                  :key="next"
                  :label="STATUS_META[next].label + '으로 변경'"
                  :color="STATUS_META[next].color"
                  variant="subtle"
                  :loading="updating"
                  @click="changeStatus(next)"
                />
              </div>
            </div>
            <UAlert
              v-else
              color="neutral"
              variant="subtle"
              icon="i-lucide-check-circle"
              title="최종 상태입니다."
            />
          </div>
        </UCard>

        <!-- 안내 -->
        <div
          v-if="!delivery && !error && !loading"
          class="text-center text-muted text-sm py-12"
        >
          <UIcon name="i-lucide-map-pin" class="text-4xl mb-3 block mx-auto" />
          배달 ID를 입력하여 배달 현황을 조회하고 상태를 관리하세요.
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
