<script setup lang="ts">
import { formatWon, useOrdering } from '~/composables/useOrdering';

const toast = useToast();
const { cartItems, cartTotal, clearCart, loadCart } = useOrdering();
const { $api } = useApi();

await loadCart();

const customer = reactive({
  address: '서울시 강남구 역삼동 123-45',
  request: '문 앞에 두고 벨 눌러주세요.',
  payment: 'card',
});

const deliveryFee = computed(() => {
  return cartItems.value.length >= 2 ? 0 : 2500;
});

const discountAmount = computed(() => {
  return cartItems.value.length > 0 ? 4000 : 0;
});

const payableAmount = computed(() => {
  return Math.max(
    cartTotal.value + deliveryFee.value - discountAmount.value,
    0,
  );
});

const placeOrder = async () => {
  if (cartItems.value.length === 0) {
    toast.add({
      title: '장바구니가 비어 있습니다.',
      description: '매장에서 메뉴를 먼저 담아주세요.',
      icon: 'i-lucide-alert-circle',
      color: 'warning',
    });
    return;
  }

  const orderItems = cartItems.value.map(item => ({
    productId: item.menuId,
    quantity: item.quantity,
    price: item.price,
  }));

  const res = await $api('/api/v1/orders', {
    method: 'POST',
    body: {
      items: orderItems,
      address: customer.address,
      totalAmount: payableAmount.value,
    },
  });

  if (!res.success) {
    toast.add({
      title: '주문 처리에 실패했습니다.',
      description: '잠시 후 다시 시도해 주세요.',
      icon: 'i-lucide-alert-circle',
      color: 'error',
    });
    return;
  }

  await clearCart();
  toast.add({
    title: '주문이 접수되었습니다.',
    description: '주문 내역 화면에서 배달 상태를 확인할 수 있습니다.',
    icon: 'i-lucide-badge-check',
    color: 'success',
  });

  await navigateTo('/orders');
};
</script>

<template>
  <UDashboardPanel grow>
    <template #header>
      <UDashboardNavbar title="결제하기">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div
        class="grid gap-6 p-4 sm:p-6 xl:grid-cols-[minmax(0,1.5fr)_380px] xl:items-start"
      >
        <section class="space-y-6">
          <UCard>
            <div class="space-y-4">
              <div>
                <h2 class="text-lg font-semibold text-highlighted">
                  배달 정보
                </h2>
                <p class="text-sm text-muted">
                  기본 배달지와 요청 사항을 확인하세요.
                </p>
              </div>

              <div class="grid gap-4">
                <UInput
                  v-model="customer.address"
                  label="배달 주소"
                />
                <UTextarea
                  v-model="customer.request"
                  label="요청 사항"
                  :rows="4"
                />
              </div>
            </div>
          </UCard>

          <UCard>
            <div class="space-y-4">
              <div>
                <h2 class="text-lg font-semibold text-highlighted">
                  주문 메뉴
                </h2>
                <p class="text-sm text-muted">
                  담은 메뉴를 확인하고 결제를 진행하세요.
                </p>
              </div>

              <div
                v-if="cartItems.length"
                class="space-y-3"
              >
                <div
                  v-for="item in cartItems"
                  :key="item.id"
                  class="rounded-2xl border border-default p-4"
                >
                  <div class="flex items-start justify-between gap-4">
                    <div>
                      <p class="text-sm font-semibold text-primary-600">
                        {{ item.storeName }}
                      </p>
                      <p class="mt-1 font-medium text-highlighted">
                        {{ item.menu }}
                      </p>
                      <p class="mt-1 text-sm text-muted">
                        수량 {{ item.quantity }}개
                      </p>
                    </div>
                    <span class="text-sm font-semibold text-highlighted">{{
                      formatWon(item.price * item.quantity)
                    }}</span>
                  </div>
                </div>
              </div>

              <div
                v-else
                class="rounded-2xl border border-dashed border-default px-6 py-12 text-center"
              >
                <UIcon
                  name="i-lucide-shopping-bag"
                  class="mx-auto size-10 text-muted"
                />
                <p class="mt-3 text-sm font-medium text-highlighted">
                  장바구니가 비어 있습니다.
                </p>
                <p class="mt-1 text-sm text-muted">
                  홈이나 매장 상세에서 메뉴를 먼저 담아주세요.
                </p>
                <UButton
                  to="/"
                  class="mt-4"
                >
                  홈으로 이동
                </UButton>
              </div>
            </div>
          </UCard>
        </section>

        <aside class="space-y-4 xl:sticky xl:top-6">
          <UCard>
            <div class="space-y-4">
              <div>
                <h2 class="text-lg font-semibold text-highlighted">
                  결제 수단
                </h2>
                <p class="text-sm text-muted">
                  현재는 샘플 결제 수단으로 구성되어 있습니다.
                </p>
              </div>

              <URadioGroup
                v-model="customer.payment"
                :items="[
                  { label: '카드 결제', value: 'card' },
                  { label: '간편 결제', value: 'easy' },
                  { label: '만나서 결제', value: 'cash' }
                ]"
              />
            </div>
          </UCard>

          <UCard>
            <div class="space-y-4">
              <div class="flex items-center justify-between text-sm">
                <span class="text-muted">메뉴 금액</span>
                <span class="font-medium text-highlighted">{{
                  formatWon(cartTotal)
                }}</span>
              </div>
              <div class="flex items-center justify-between text-sm">
                <span class="text-muted">배달비</span>
                <span class="font-medium text-highlighted">{{
                  formatWon(deliveryFee)
                }}</span>
              </div>
              <div class="flex items-center justify-between text-sm">
                <span class="text-muted">할인</span>
                <span class="font-medium text-primary-600">-{{ formatWon(discountAmount) }}</span>
              </div>
              <div class="border-t border-default pt-4">
                <div class="flex items-center justify-between">
                  <span class="font-medium text-highlighted">최종 결제 금액</span>
                  <span class="text-xl font-semibold text-highlighted">{{
                    formatWon(payableAmount)
                  }}</span>
                </div>
              </div>

              <UButton
                block
                size="lg"
                :disabled="cartItems.length === 0"
                @click="placeOrder"
              >
                주문 확정
              </UButton>
            </div>
          </UCard>
        </aside>
      </div>
    </template>
  </UDashboardPanel>
</template>
