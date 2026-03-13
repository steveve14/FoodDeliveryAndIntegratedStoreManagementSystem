<script setup lang="ts">
type OrderStatus =
  | 'pending' |
  'cooking' |
  'delivering' |
  'completed' |
  'cancelled';

interface OrderItem {
  id: string;
  customer: string;
  menu: string;
  totalPrice: number;
  requestedAt: string;
  status: OrderStatus;
}

interface OrderApiItem {
  id: string;
  userId: string;
  storeId: string;
  totalAmount: number;
  status: string;
  createdAt: string;
  items: Array<{
    productId: string;
    quantity: number;
    price: number;
  }>;
}

interface StoreSummary {
  id: string;
}

const { $api } = useApi();

const q = ref('');
const selectedStatus = ref<OrderStatus | 'all'>('all');
const orders = ref<OrderItem[]>([]);
const updatingOrderId = ref<string | null>(null);

const statusFromApi = (status: string): OrderStatus => {
  switch (status) {
    case 'COOKING':
      return 'cooking';
    case 'DELIVERING':
      return 'delivering';
    case 'DONE':
      return 'completed';
    case 'CANCELLED':
      return 'cancelled';
    default:
      return 'pending';
  }
};

const formatRequestedAt = (value: string) => {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString('ko-KR', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const summarizeMenu = (items: OrderApiItem['items']) => {
  if (items.length === 0) {
    return '주문 항목 없음';
  }
  if (items.length === 1) {
    return `메뉴 1건 x ${items[0]?.quantity ?? 1}`;
  }
  return `메뉴 ${items.length}건`;
};

const loadOrders = async () => {
  const myStore = await $api<StoreSummary>('/api/v1/stores/me');
  if (!myStore.success || !myStore.data) {
    orders.value = [];
    return;
  }

  const res = await $api<OrderApiItem[]>(
    `/api/v1/orders/store/${myStore.data.id}`,
  );
  if (!res.success || !res.data) {
    orders.value = [];
    return;
  }

  orders.value = res.data.map(order => ({
    id: order.id,
    customer: `고객 ${order.userId.slice(0, 8)}`,
    menu: summarizeMenu(order.items),
    totalPrice: order.totalAmount,
    requestedAt: formatRequestedAt(order.createdAt),
    status: statusFromApi(order.status),
  }));
};

await loadOrders();

const statusMap: Record<
  OrderStatus,
  {
    label: string;
    color: 'warning' | 'secondary' | 'success' | 'neutral' | 'error';
  }
> = {
  pending: { label: '접수 대기', color: 'warning' },
  cooking: { label: '조리 중', color: 'secondary' },
  delivering: { label: '배달 중', color: 'success' },
  completed: { label: '완료', color: 'neutral' },
  cancelled: { label: '취소', color: 'error' },
};

const nextStatusMap: Record<OrderStatus, OrderStatus | null> = {
  pending: 'cooking',
  cooking: 'delivering',
  delivering: 'completed',
  completed: null,
  cancelled: null,
};

const statusToApi = (status: OrderStatus): string => {
  switch (status) {
    case 'pending':
      return 'CREATED';
    case 'cooking':
      return 'COOKING';
    case 'delivering':
      return 'DELIVERING';
    case 'completed':
      return 'DONE';
    case 'cancelled':
      return 'CANCELLED';
    default:
      return 'CREATED';
  }
};

const updateOrderStatus = async (order: OrderItem, nextStatus: OrderStatus) => {
  if (updatingOrderId.value) {
    return;
  }

  updatingOrderId.value = order.id;
  try {
    const res = await $api<OrderApiItem>(`/api/v1/orders/${order.id}/status`, {
      method: 'PATCH',
      body: {
        status: statusToApi(nextStatus),
      },
    });

    if (!res.success || !res.data) {
      return;
    }

    const target = orders.value.find(item => item.id === order.id);
    if (target) {
      target.status = statusFromApi(res.data.status);
    }
  } finally {
    updatingOrderId.value = null;
  }
};

const filteredOrders = computed(() => {
  return orders.value.filter((order) => {
    const matchesSearch =
      order.customer.toLowerCase().includes(q.value.toLowerCase()) ||
      order.menu.toLowerCase().includes(q.value.toLowerCase());
    const matchesStatus =
      selectedStatus.value === 'all' || order.status === selectedStatus.value;
    return matchesSearch && matchesStatus;
  });
});

const formatPrice = (price: number) =>
  new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0,
  }).format(price);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar
      title="실시간 주문접수"
      :badge="orders.length"
    />

    <UDashboardToolbar>
      <template #left>
        <UInput
          v-model="q"
          icon="i-lucide-search"
          placeholder="고객명 또는 메뉴 검색..."
          class="w-full sm:w-72"
        />
        <USelect
          v-model="selectedStatus"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '접수 대기', value: 'pending' },
            { label: '조리 중', value: 'cooking' },
            { label: '배달 중', value: 'delivering' },
            { label: '완료', value: 'completed' },
            { label: '취소', value: 'cancelled' }
          ]"
          class="w-36"
        />
      </template>
    </UDashboardToolbar>

    <div class="grid grid-cols-1 gap-4 p-4 sm:grid-cols-2 xl:grid-cols-3">
      <UPageCard
        v-for="order in filteredOrders"
        :key="order.id"
        :title="`${order.customer}님 주문`"
        :description="`주문번호 #${order.id}`"
        variant="subtle"
      >
        <div class="space-y-2 text-sm">
          <p class="text-highlighted">
            {{ order.menu }}
          </p>
          <p class="text-muted">
            요청 시각: {{ order.requestedAt }}
          </p>
          <p class="font-semibold">
            결제금액: {{ formatPrice(order.totalPrice) }}
          </p>
          <UBadge
            :color="statusMap[order.status].color"
            variant="subtle"
          >
            {{ statusMap[order.status].label }}
          </UBadge>

          <div class="pt-2 flex items-center gap-2">
            <UButton
              v-if="nextStatusMap[order.status]"
              size="xs"
              color="primary"
              variant="soft"
              :loading="updatingOrderId === order.id"
              @click="updateOrderStatus(order, nextStatusMap[order.status]!)"
            >
              다음 단계: {{ statusMap[nextStatusMap[order.status]!].label }}
            </UButton>
            <UButton
              v-if="order.status === 'pending' || order.status === 'cooking'"
              size="xs"
              color="error"
              variant="soft"
              :loading="updatingOrderId === order.id"
              @click="updateOrderStatus(order, 'cancelled')"
            >
              주문 취소
            </UButton>
          </div>
        </div>
      </UPageCard>
    </div>
  </UDashboardPanel>
</template>
