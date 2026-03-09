<script setup lang="ts">
import { formatWon } from '~/composables/useOrdering';

type OrderStatus = 'placed' | 'confirmed' | 'preparing' | 'delivering' | 'completed';

interface OrderItem {
  id: string;
  storeId: string;
  storeName: string;
  menu: string;
  totalPrice: number;
  orderedAt: string;
  eta: string;
  status: OrderStatus;
}

interface OrderApiItem {
  id: string;
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

const q = ref('');
const selectedStatus = ref<OrderStatus | 'all'>('all');
const { $api } = useApi();
const { stores, loadStores } = useOrdering();
const orders = ref<OrderItem[]>([]);

await loadStores();

const mapStatus = (status: string): OrderStatus => {
  switch (status) {
    case 'CREATED':
      return 'placed';
    case 'COOKING':
      return 'preparing';
    case 'DELIVERING':
      return 'delivering';
    case 'DONE':
      return 'completed';
    default:
      return 'confirmed';
  }
};

const mapEta = (status: OrderStatus) => {
  switch (status) {
    case 'placed':
      return '주문 접수됨';
    case 'confirmed':
      return '매장 확인 중';
    case 'preparing':
      return '조리 중';
    case 'delivering':
      return '배달 중';
    case 'completed':
      return '배달 완료';
  }
};

const summarizeMenu = (items: OrderApiItem['items']) => {
  if (items.length <= 1) {
    return '메뉴 1건';
  }
  return `메뉴 ${items.length}건`;
};

const loadOrders = async () => {
  const res = await $api<OrderApiItem[]>('/api/v1/orders/my');
  if (!res.success || !res.data) {
    orders.value = [];
    return;
  }

  orders.value = res.data.map((order) => {
    const mappedStatus = mapStatus(order.status);
    const store = stores.value.find(item => item.id === order.storeId);

    return {
      id: order.id,
      storeId: order.storeId,
      storeName: store?.name ?? '매장 정보 없음',
      menu: summarizeMenu(order.items),
      totalPrice: order.totalAmount,
      orderedAt: new Date(order.createdAt).toLocaleString('ko-KR', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
      }),
      eta: mapEta(mappedStatus),
      status: mappedStatus,
    };
  });
};

await loadOrders();

const statusMap: Record<
  OrderStatus,
  {
    label: string;
    color: 'neutral' | 'primary' | 'secondary' | 'success' | 'warning';
  }
> = {
  placed: { label: '주문 접수', color: 'warning' },
  confirmed: { label: '매장 확인', color: 'primary' },
  preparing: { label: '조리 중', color: 'secondary' },
  delivering: { label: '배달 중', color: 'success' },
  completed: { label: '배달 완료', color: 'neutral' },
};

const filteredOrders = computed(() => {
  const keyword = q.value.trim().toLowerCase();

  return orders.value.filter((order) => {
    const matchesSearch =
      keyword.length === 0 ||
      order.storeName.toLowerCase().includes(keyword) ||
      order.menu.toLowerCase().includes(keyword);
    const matchesStatus =
      selectedStatus.value === 'all' || order.status === selectedStatus.value;

    return matchesSearch && matchesStatus;
  });
});
</script>

<template>
  <UDashboardPanel grow>
    <template #header>
      <UDashboardNavbar title="주문 내역" :badge="orders.length">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-6 p-4 sm:p-6">
        <section class="grid gap-4 lg:grid-cols-[minmax(0,1fr)_220px]">
          <UInput
            v-model="q"
            icon="i-lucide-search"
            placeholder="매장명 또는 메뉴 검색"
          />
          <USelect
            v-model="selectedStatus"
            :items="[
              { label: '전체 상태', value: 'all' },
              { label: '주문 접수', value: 'placed' },
              { label: '매장 확인', value: 'confirmed' },
              { label: '조리 중', value: 'preparing' },
              { label: '배달 중', value: 'delivering' },
              { label: '배달 완료', value: 'completed' },
            ]"
          />
        </section>

        <section class="grid gap-4 xl:grid-cols-2">
          <UCard v-for="order in filteredOrders" :key="order.id">
            <div class="space-y-4">
              <div class="flex items-start justify-between gap-4">
                <div>
                  <p class="text-sm font-semibold text-highlighted">
                    {{ order.storeName }}
                  </p>
                  <p class="mt-1 text-sm text-muted">
                    주문번호 #{{ order.id }}
                  </p>
                </div>
                <UBadge :color="statusMap[order.status].color" variant="soft">
                  {{ statusMap[order.status].label }}
                </UBadge>
              </div>

              <div class="rounded-2xl bg-neutral-50 p-4 dark:bg-neutral-900/60">
                <p class="font-medium text-highlighted">
                  {{ order.menu }}
                </p>
                <div
                  class="mt-2 flex flex-wrap gap-x-4 gap-y-2 text-sm text-muted"
                >
                  <span>{{ order.orderedAt }}</span>
                  <span>{{ order.eta }}</span>
                  <span>{{ formatWon(order.totalPrice) }}</span>
                </div>
              </div>

              <div class="flex gap-2">
                <UButton
                  color="neutral"
                  variant="soft"
                  :to="`/stores/${order.storeId}`"
                  block
                >
                  같은 매장 다시 주문
                </UButton>
                <UButton to="/support/inquiry" block>
                  문의하기
                </UButton>
              </div>
            </div>
          </UCard>

          <div
            v-if="filteredOrders.length === 0"
            class="rounded-3xl border border-dashed border-default px-6 py-12 text-center xl:col-span-2"
          >
            <UIcon
              name="i-lucide-receipt-text"
              class="mx-auto size-10 text-muted"
            />
            <p class="mt-3 text-sm font-medium text-highlighted">
              조건에 맞는 주문이 없습니다.
            </p>
            <p class="mt-1 text-sm text-muted">
              검색어나 상태를 바꿔 다시 확인해 주세요.
            </p>
          </div>
        </section>
      </div>
    </template>
  </UDashboardPanel>
</template>
