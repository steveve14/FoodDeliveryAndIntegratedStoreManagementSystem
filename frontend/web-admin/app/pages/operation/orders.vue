<script setup lang="ts">
import * as z from "zod";
import type { FormSubmitEvent } from "@nuxt/ui";
import type { OrderDto, OrderStatus } from "~/types/api";
import { useOrderApi } from "~/composables/api/useOrderApi";

// ── 컴포넌트 ──────────────────────────────────────────────
const UBadge = resolveComponent("UBadge");
const UButton = resolveComponent("UButton");

const toast = useToast();
const { getOrder, updateOrderStatus } = useOrderApi();

// ── 상태 ──────────────────────────────────────────────────
const searchId = ref("");
const loading = ref(false);
const updating = ref(false);
const order = ref<OrderDto | null>(null);
const error = ref<string | null>(null);

// ── 주문 상태 메타 ─────────────────────────────────────────
const STATUS_META: Record<
  OrderStatus,
  {
    label: string;
    color: "success" | "warning" | "primary" | "error" | "neutral" | "info";
  }
> = {
  CREATED: { label: "접수됨", color: "info" },
  CONFIRMED: { label: "확인됨", color: "primary" },
  PREPARING: { label: "조리중", color: "warning" },
  READY: { label: "준비완료", color: "success" },
  DELIVERING: { label: "배달중", color: "warning" },
  COMPLETED: { label: "완료", color: "success" },
  CANCELLED: { label: "취소됨", color: "error" },
};

const NEXT_STATUS: Partial<Record<OrderStatus, OrderStatus[]>> = {
  CREATED: ["CONFIRMED", "CANCELLED"],
  CONFIRMED: ["PREPARING", "CANCELLED"],
  PREPARING: ["READY", "CANCELLED"],
  READY: ["DELIVERING"],
  DELIVERING: ["COMPLETED"],
};

// ── 검색 ──────────────────────────────────────────────────
const searchSchema = z.object({
  orderId: z.string().min(1, "주문 ID를 입력해주세요"),
});

async function onSearch(e: FormSubmitEvent<{ orderId: string }>) {
  error.value = null;
  order.value = null;
  loading.value = true;
  try {
    const res = await getOrder(e.data.orderId);
    if (res.success) {
      order.value = res.data;
    } else {
      error.value = "주문을 찾을 수 없습니다.";
    }
  } catch {
    error.value = "주문 조회 중 오류가 발생했습니다.";
  } finally {
    loading.value = false;
  }
}

// ── 상태 변경 ──────────────────────────────────────────────
async function changeStatus(status: OrderStatus) {
  if (!order.value) return;
  updating.value = true;
  try {
    const res = await updateOrderStatus(order.value.id, status);
    if (res.success) {
      order.value = res.data;
      toast.add({
        title: "상태 변경 완료",
        description: `주문 상태가 "${STATUS_META[status].label}"으로 변경되었습니다.`,
        color: "success",
      });
    }
  } catch {
    toast.add({
      title: "오류",
      description: "상태 변경에 실패했습니다.",
      color: "error",
    });
  } finally {
    updating.value = false;
  }
}

// ── 금액 포맷터 ────────────────────────────────────────────
const currencyFmt = new Intl.NumberFormat("ko-KR", {
  style: "currency",
  currency: "KRW",
});
</script>

<template>
  <UDashboardPanel id="orders">
    <template #header>
      <UDashboardNavbar title="주문 관리">
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
          :state="{ orderId: searchId }"
          class="flex gap-2"
          @submit="onSearch"
        >
          <UFormField name="orderId" class="flex-1">
            <UInput
              v-model="searchId"
              placeholder="주문 ID를 입력하세요 (UUID)"
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

        <!-- 주문 상세 카드 -->
        <UCard v-if="order">
          <template #header>
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <UIcon name="i-lucide-shopping-bag" class="text-xl" />
                <div>
                  <p class="font-semibold text-sm text-muted">주문 ID</p>
                  <p class="font-mono text-xs">{{ order.id }}</p>
                </div>
              </div>
              <UBadge
                :color="STATUS_META[order.status].color"
                variant="subtle"
                size="lg"
              >
                {{ STATUS_META[order.status].label }}
              </UBadge>
            </div>
          </template>

          <div class="space-y-4">
            <!-- 기본 정보 -->
            <div class="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p class="text-muted mb-0.5">고객 ID</p>
                <p class="font-mono">{{ order.userId }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">가게 ID</p>
                <p class="font-mono">{{ order.storeId }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">총 금액</p>
                <p class="font-semibold text-primary">
                  {{ currencyFmt.format(order.totalAmount) }}
                </p>
              </div>
              <div>
                <p class="text-muted mb-0.5">주문 시각</p>
                <p>{{ new Date(order.createdAt).toLocaleString("ko-KR") }}</p>
              </div>
            </div>

            <!-- 주문 항목 -->
            <div>
              <p class="text-sm font-medium mb-2">주문 항목</p>
              <div
                class="rounded-lg border border-default divide-y divide-default"
              >
                <div
                  v-for="item in order.items"
                  :key="item.productId"
                  class="flex items-center justify-between px-4 py-2 text-sm"
                >
                  <span class="text-muted font-mono text-xs">{{
                    item.productId
                  }}</span>
                  <span>{{ item.quantity }}개</span>
                  <span class="font-semibold">{{
                    currencyFmt.format(item.price * item.quantity)
                  }}</span>
                </div>
              </div>
            </div>

            <!-- 상태 변경 -->
            <div v-if="NEXT_STATUS[order.status]?.length">
              <p class="text-sm font-medium mb-2">상태 변경</p>
              <div class="flex flex-wrap gap-2">
                <UButton
                  v-for="next in NEXT_STATUS[order.status]"
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
              title="최종 상태입니다. 더 이상 상태를 변경할 수 없습니다."
            />
          </div>
        </UCard>

        <!-- 안내 -->
        <div
          v-if="!order && !error && !loading"
          class="text-center text-muted text-sm py-12"
        >
          <UIcon
            name="i-lucide-package-search"
            class="text-4xl mb-3 block mx-auto"
          />
          주문 ID를 입력하여 주문 상태를 조회하고 관리하세요.
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
