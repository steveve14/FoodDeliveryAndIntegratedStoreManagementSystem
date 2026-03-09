<script setup lang="ts">
import { formatWon, useOrdering } from '~/composables/useOrdering';

const route = useRoute();
const toast = useToast();
const { $api } = useApi();
const { getStoreById, addMenuToCart, loadCart, loadStores, loadStoreById } =
  useOrdering();

await loadCart();
await loadStores();

const storeId = computed(() => String(route.params.id));
await loadStoreById(storeId.value);
const store = computed(() => getStoreById(storeId.value));

const addToCart = async (menuId: string, goToCheckout = true) => {
  if (!store.value) {
    return;
  }

  const menu = store.value.menus.find(item => item.id === menuId);
  if (!menu) {
    return;
  }

  await addMenuToCart(store.value, menu);
  toast.add({
    title: `${menu.name}을(를) 장바구니에 담았습니다.`,
    icon: 'i-lucide-shopping-cart',
    color: 'success',
  });

  if (goToCheckout) {
    await navigateTo('/checkout');
  }
};

const addFavorite = async () => {
  if (!store.value) {
    return;
  }

  await $api('/api/v1/users/me/favorites', {
    method: 'POST',
    body: {
      storeId: String(store.value.id),
      name: store.value.name,
      category: store.value.category,
      rating: store.value.rating,
      deliveryTime: store.value.eta,
      minOrder: store.value.minOrder,
      imageIcon: store.value.heroIcon,
    },
  });

  toast.add({
    title: '찜한 가게에 추가했습니다.',
    icon: 'i-lucide-heart',
    color: 'success',
  });
};
</script>

<template>
  <UDashboardPanel grow>
    <template #header>
      <UDashboardNavbar :title="store?.name ?? '매장을 찾을 수 없습니다.'">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>

        <template #right>
          <UButton
            color="neutral"
            variant="ghost"
            icon="i-lucide-heart"
            @click="addFavorite"
          >
            찜하기
          </UButton>
          <UButton
            color="neutral"
            variant="ghost"
            to="/checkout"
            icon="i-lucide-shopping-cart"
          >
            장바구니
          </UButton>
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div v-if="store" class="flex flex-col gap-6 p-4 sm:p-6">
        <section class="rounded-3xl border border-primary/15 bg-primary/5 p-6">
          <div
            class="flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between"
          >
            <div class="space-y-4">
              <div
                class="inline-flex size-14 items-center justify-center rounded-3xl bg-primary/10 text-primary-600"
              >
                <UIcon :name="store.heroIcon" class="size-7" />
              </div>
              <div>
                <h1 class="text-3xl font-semibold text-highlighted">
                  {{ store.name }}
                </h1>
                <p class="mt-2 max-w-2xl text-sm leading-6 text-muted">
                  {{ store.description }}
                </p>
              </div>
              <div class="flex flex-wrap gap-2">
                <UBadge color="primary" variant="soft">
                  {{ store.eta }}
                </UBadge>
                <UBadge color="neutral" variant="subtle">
                  배달비 {{ store.deliveryFee }}
                </UBadge>
                <UBadge color="neutral" variant="subtle">
                  최소 주문 {{ store.minOrder }}
                </UBadge>
                <UBadge color="warning" variant="subtle">
                  평점 {{ store.rating }} / 리뷰
                  {{ store.reviewCount.toLocaleString() }}
                </UBadge>
              </div>
            </div>

            <UCard class="w-full max-w-sm">
              <div class="space-y-4">
                <div>
                  <p class="text-sm font-medium text-muted">오늘 혜택</p>
                  <p class="mt-1 text-lg font-semibold text-highlighted">
                    {{ store.promo }}
                  </p>
                </div>
                <div class="flex flex-wrap gap-2">
                  <UBadge
                    v-for="tag in store.tags"
                    :key="tag"
                    color="neutral"
                    variant="subtle"
                  >
                    {{ tag }}
                  </UBadge>
                </div>
                <UButton block to="/checkout" icon="i-lucide-credit-card">
                  장바구니 확인
                </UButton>
              </div>
            </UCard>
          </div>
        </section>

        <section class="space-y-4">
          <div>
            <h2 class="text-lg font-semibold text-highlighted">메뉴 선택</h2>
            <p class="text-sm text-muted">
              대표 메뉴부터 바로 담고 결제로 이동할 수 있습니다.
            </p>
          </div>

          <div class="grid gap-4 xl:grid-cols-2">
            <UPageCard
              v-for="menu in store.menus"
              :key="menu.id"
              :title="menu.name"
              :description="menu.description"
              variant="subtle"
            >
              <div class="space-y-4">
                <div class="flex items-center justify-between gap-3">
                  <span class="text-lg font-semibold text-highlighted">{{
                    formatWon(menu.price)
                  }}</span>
                  <UBadge v-if="menu.badge" color="primary" variant="soft">
                    {{ menu.badge }}
                  </UBadge>
                </div>

                <div class="flex gap-2">
                  <UButton
                    color="neutral"
                    variant="soft"
                    block
                    @click="addToCart(menu.id, false)"
                  >
                    담고 계속 보기
                  </UButton>
                  <UButton block @click="addToCart(menu.id)">
                    담고 결제하기
                  </UButton>
                </div>
              </div>
            </UPageCard>
          </div>
        </section>
      </div>

      <div v-else class="flex min-h-[60vh] items-center justify-center p-6">
        <UCard class="w-full max-w-md text-center">
          <div class="space-y-4">
            <UIcon name="i-lucide-store" class="mx-auto size-10 text-muted" />
            <div>
              <h2 class="text-lg font-semibold text-highlighted">
                매장을 찾을 수 없습니다.
              </h2>
              <p class="mt-2 text-sm text-muted">
                홈에서 다른 매장을 선택해 주세요.
              </p>
            </div>
            <UButton to="/" block> 홈으로 돌아가기 </UButton>
          </div>
        </UCard>
      </div>
    </template>
  </UDashboardPanel>
</template>
