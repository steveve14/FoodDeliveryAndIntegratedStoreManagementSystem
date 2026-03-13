<script setup lang="ts">
import { h, resolveComponent } from 'vue';
import type { TableColumn } from '@nuxt/ui';
import type { OrderDto, OrderStatus } from '~/types/api';
import { useOrderApi } from '~/composables/api/useOrderApi';
import { useAdminHomeSource } from '~/composables/useAdminHomeSource';

const UBadge = resolveComponent('UBadge');
const UButton = resolveComponent('UButton');

const toast = useToast();
const { updateOrderStatus } = useOrderApi();
const { data: source, refresh, status: sourceStatus } = await useAdminHomeSource();

const searchFilter = ref('');
const statusFilter = ref<'all' | OrderStatus>('all');
const detailOpen = ref(false);
const selectedOrderId = ref<string | null>(null);
const updatingOrderId = ref<string | null>(null);

const STATUS_META: Record<
  OrderStatus,
  {
    label: string;
    color: 'success' | 'warning' | 'primary' | 'error' | 'neutral' | 'info';
  }
> = {
  CREATED: { label: '접수됨', color: 'info' },
  COOKING: { label: '조리중', color: 'warning' },
  DELIVERING: { label: '배달중', color: 'primary' },
  DONE: { label: '완료', color: 'success' },
  CANCELLED: { label: '취소됨', color: 'error' },
};

const STATUS_OPTIONS: { label: string; value: 'all' | OrderStatus }[] = [
  { label: '전체 상태', value: 'all' },
  { label: '접수됨', value: 'CREATED' },
  { label: '조리중', value: 'COOKING' },
  { label: '배달중', value: 'DELIVERING' },
  { label: '완료', value: 'DONE' },
  { label: '취소', value: 'CANCELLED' },
];

const NEXT_STATUS: Partial<Record<OrderStatus, OrderStatus[]>> = {
  CREATED: ['COOKING', 'CANCELLED'],
  COOKING: ['DELIVERING', 'CANCELLED'],
  DELIVERING: ['DONE', 'CANCELLED'],
};

const currencyFmt = new Intl.NumberFormat('ko-KR', {
  style: 'currency',
  currency: 'KRW',
  maximumFractionDigits: 0,
});

const customerEmailById = computed(() => {
  return Object.fromEntries(
    (source.value?.customers ?? []).map(customer => [customer.id, customer.email]),
  );
});

const storeNameById = computed(() => {
  return Object.fromEntries(
    (source.value?.stores ?? []).map(store => [store.id, store.name]),
  );
});

const filteredOrders = computed(() => {
  const keyword = searchFilter.value.trim().toLowerCase();

  return (source.value?.orders ?? [])
    .filter((order) => {
      if (statusFilter.value !== 'all' && order.status !== statusFilter.value) {
        return false;
      }

      if (!keyword) {
        return true;
      }

      const email = customerEmailById.value[order.userId] ?? '';
      const storeName = storeNameById.value[order.storeId] ?? '';
      return [order.id, order.userId, order.storeId, order.status, email, storeName]
        .some(value => value.toLowerCase().includes(keyword));
    })
    .sort((left, right) => new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime());
});

const selectedOrder = computed(() => {
  if (!selectedOrderId.value) {
    return null;
  }

  return (source.value?.orders ?? []).find(order => order.id === selectedOrderId.value) ?? null;
});

function openOrderDetail (order: OrderDto) {
  selectedOrderId.value = order.id;
  detailOpen.value = true;
}

async function changeStatus (orderId: string, nextStatus: OrderStatus) {
  updatingOrderId.value = orderId;
  try {
    const res = await updateOrderStatus(orderId, nextStatus);
    if (!res.success) {
      throw new Error();
    }

    await refresh();
    selectedOrderId.value = orderId;
    toast.add({
      title: '주문 상태 변경 완료',
      description: `주문 상태가 ${STATUS_META[nextStatus].label}로 변경되었습니다.`,
      color: 'success',
    });
  } catch {
    toast.add({
      title: '주문 상태 변경 실패',
      description: '잠시 후 다시 시도해주세요.',
      color: 'error',
    });
  } finally {
    updatingOrderId.value = null;
  }
}

const columns: TableColumn<OrderDto>[] = [
  {
    accessorKey: 'id',
    header: '주문 ID',
    cell: ({ row }) => h('span', { class: 'font-mono text-xs' }, row.original.id),
  },
  {
    accessorKey: 'createdAt',
    header: '주문 시각',
    cell: ({ row }) => new Date(row.original.createdAt).toLocaleString('ko-KR'),
  },
  {
    id: 'store',
    header: '가게',
    cell: ({ row }) => storeNameById.value[row.original.storeId] ?? row.original.storeId,
  },
  {
    id: 'customer',
    header: '고객',
    cell: ({ row }) => customerEmailById.value[row.original.userId] ?? row.original.userId,
  },
  {
    accessorKey: 'totalAmount',
    header: () => h('div', { class: 'text-right' }, '금액'),
    cell: ({ row }) => h('div', { class: 'text-right font-medium' }, currencyFmt.format(row.original.totalAmount)),
  },
  {
    accessorKey: 'status',
    header: '상태',
    cell: ({ row }) => h(
      UBadge,
      {
        variant: 'subtle',
        color: STATUS_META[row.original.status]?.color ?? 'neutral',
      },
      () => STATUS_META[row.original.status]?.label ?? row.original.status,
    ),
  },
  {
    id: 'actions',
    header: '관리',
    cell: ({ row }) => h(
      'div',
      { class: 'flex justify-end gap-2' },
      [
        h(UButton, {
          label: '상세',
          color: 'neutral',
          variant: 'outline',
          onClick: () => openOrderDetail(row.original),
        }),
        NEXT_STATUS[row.original.status]?.[0] ?
          h(UButton, {
            label: `${STATUS_META[NEXT_STATUS[row.original.status]![0]].label}`,
            color: STATUS_META[NEXT_STATUS[row.original.status]![0]].color,
            variant: 'subtle',
            loading: updatingOrderId.value === row.original.id,
            onClick: () => changeStatus(row.original.id, NEXT_STATUS[row.original.status]![0]),
          }) :
          null,
      ],
    ),
  },
];
</script>

<template>
  <UDashboardPanel id="orders">
    <template #header>
      <UDashboardNavbar title="주문 관리">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
        <template #right>
          <UButton
            label="새로고침"
            icon="i-lucide-refresh-cw"
            color="neutral"
            variant="outline"
            :loading="sourceStatus === 'pending'"
            @click="refresh"
          />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <UAlert
        v-if="sourceStatus === 'error'"
        color="error"
        variant="subtle"
        icon="i-lucide-alert-circle"
        title="주문 목록을 불러오지 못했습니다."
        description="store별 집계 호출 중 실패한 요청이 있을 수 있습니다. 새로고침 후 다시 확인해주세요."
        class="mb-4"
      />

      <div class="flex flex-wrap items-center justify-between gap-2 mb-4">
        <UInput
          v-model="searchFilter"
          class="max-w-md"
          icon="i-lucide-search"
          placeholder="주문 ID, 고객 이메일, 가게명으로 검색"
        />

        <USelect
          v-model="statusFilter"
          :items="STATUS_OPTIONS"
          class="min-w-40"
        />
      </div>

      <UTable
        :data="filteredOrders"
        :columns="columns"
        :loading="sourceStatus === 'pending'"
        class="flex-1"
        :ui="{
          base: 'table-fixed border-separate border-spacing-0',
          thead: '[&>tr]:bg-elevated/50',
          th: 'py-2 first:rounded-l-lg last:rounded-r-lg border-y border-default first:border-l last:border-r',
          td: 'border-b border-default',
          separator: 'h-0',
        }"
      />

      <div class="flex items-center justify-between gap-3 border-t border-default pt-4 mt-4">
        <div class="text-sm text-muted">
          총 {{ filteredOrders.length }}건의 주문
        </div>
      </div>

      <UModal
        v-model:open="detailOpen"
        title="주문 상세"
        description="store별 주문 집계 기준 상세 정보입니다."
      >
        <template #body>
          <div v-if="selectedOrder" class="space-y-4 text-sm">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-muted mb-0.5">주문 ID</p>
                <p class="font-mono text-xs">{{ selectedOrder.id }}</p>
              </div>
              <UBadge :color="STATUS_META[selectedOrder.status].color" variant="subtle">
                {{ STATUS_META[selectedOrder.status].label }}
              </UBadge>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-muted mb-0.5">가게</p>
                <p>{{ storeNameById[selectedOrder.storeId] ?? selectedOrder.storeId }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">고객</p>
                <p>{{ customerEmailById[selectedOrder.userId] ?? selectedOrder.userId }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">주문 시각</p>
                <p>{{ new Date(selectedOrder.createdAt).toLocaleString('ko-KR') }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">총 금액</p>
                <p class="font-semibold text-primary">{{ currencyFmt.format(selectedOrder.totalAmount) }}</p>
              </div>
            </div>

            <div>
              <p class="text-sm font-medium mb-2">주문 항목</p>
              <div class="rounded-lg border border-default divide-y divide-default">
                <div
                  v-for="item in selectedOrder.items"
                  :key="item.productId"
                  class="flex items-center justify-between px-4 py-2"
                >
                  <span class="font-mono text-xs text-muted">{{ item.productId }}</span>
                  <span>{{ item.quantity }}개</span>
                  <span class="font-semibold">{{ currencyFmt.format(item.price * item.quantity) }}</span>
                </div>
              </div>
            </div>

            <div v-if="NEXT_STATUS[selectedOrder.status]?.length">
              <p class="text-sm font-medium mb-2">상태 변경</p>
              <div class="flex flex-wrap gap-2">
                <UButton
                  v-for="nextStatus in NEXT_STATUS[selectedOrder.status]"
                  :key="nextStatus"
                  :label="`${STATUS_META[nextStatus].label}로 변경`"
                  :color="STATUS_META[nextStatus].color"
                  variant="subtle"
                  :loading="updatingOrderId === selectedOrder.id"
                  @click="changeStatus(selectedOrder.id, nextStatus)"
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

          <div v-else class="text-center text-muted py-6">
            선택한 주문 정보를 찾을 수 없습니다.
          </div>
        </template>
      </UModal>
    </template>
  </UDashboardPanel>
</template>
