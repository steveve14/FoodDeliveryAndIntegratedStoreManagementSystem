<script setup lang="ts">
import type { DropdownMenuItem } from '@nuxt/ui';
import type { CategoryItem, StoreItem } from '~/composables/useOrdering';
import { useOrdering } from '~/composables/useOrdering';

const { isNotificationsSlideoverOpen } = useDashboard();
const toast = useToast();
const {
  quickActions,
  categories,
  stores,
  reorderItems,
  loadStores,
  loadReorderItems,
  addBestSellerToCart,
} = useOrdering();

await loadStores();
await loadReorderItems();

const menuItems = [
  [
    {
      label: '주문 내역',
      icon: 'i-lucide-receipt',
      to: '/orders',
    },
    {
      label: '찜한 가게',
      icon: 'i-lucide-heart',
      to: '/favorites',
    },
    {
      label: '쿠폰함',
      icon: 'i-lucide-ticket-percent',
      to: '/benefits/coupons',
    },
  ],
] satisfies DropdownMenuItem[][];

const selectedCategory = ref<string>('all');
const searchQuery = ref('');

const orderNow = async (storeId: string) => {
  const added = await addBestSellerToCart(storeId);

  if (!added) {
    toast.add({
      title: '주문 가능한 메뉴가 없습니다.',
      icon: 'i-lucide-alert-circle',
      color: 'error',
    });
    return;
  }

  toast.add({
    title: `${added.store.name}의 ${added.menu.name}을(를) 장바구니에 담았습니다.`,
    icon: 'i-lucide-shopping-cart',
    color: 'success',
  });

  await navigateTo('/checkout');
};

const startSearch = () => {
  const firstMatch = filteredStores.value[0];

  if (!firstMatch) {
    toast.add({
      title: '검색 결과가 없습니다.',
      description: '다른 키워드나 카테고리로 다시 찾아보세요.',
      icon: 'i-lucide-search-x',
      color: 'warning',
    });
    return;
  }

  navigateTo(`/stores/${firstMatch.id}`);
};

const filteredStores = computed(() => {
  const keyword = searchQuery.value.trim().toLowerCase();

  return stores.value.filter((store: StoreItem) => {
    const matchesCategory =
      selectedCategory.value === 'all' ||
      store.category ===
      categories.find(
        (category: CategoryItem) => category.id === selectedCategory.value,
      )?.label;

    const matchesKeyword =
      keyword.length === 0 ||
      store.name.toLowerCase().includes(keyword) ||
      store.category.toLowerCase().includes(keyword) ||
      store.bestseller.toLowerCase().includes(keyword) ||
      store.tags.some((tag: string) => tag.toLowerCase().includes(keyword));

    return matchesCategory && matchesKeyword;
  });
});
</script>

<template>
  <UDashboardPanel
    id="home"
    grow
  >
    <template #header>
      <UDashboardNavbar
        title="지금 바로 주문"
        :ui="{ right: 'gap-3' }"
      >
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>

        <template #right>
          <UTooltip
            text="알림"
            :shortcuts="['N']"
          >
            <UButton
              color="neutral"
              variant="ghost"
              square
              @click="isNotificationsSlideoverOpen = true"
            >
              <UChip
                color="error"
                inset
              >
                <UIcon
                  name="i-lucide-bell"
                  class="size-5 shrink-0"
                />
              </UChip>
            </UButton>
          </UTooltip>

          <UDropdownMenu :items="menuItems">
            <UButton
              icon="i-lucide-ellipsis"
              color="neutral"
              variant="ghost"
            />
          </UDropdownMenu>
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-6 p-4 sm:p-6">
        <section
          class="overflow-hidden rounded-3xl border border-primary/15 bg-[radial-gradient(circle_at_top_left,rgba(0,193,106,0.22),transparent_42%),linear-gradient(135deg,rgba(255,255,255,0.96),rgba(244,252,248,0.92))] p-5 shadow-sm ring-1 ring-inset ring-white/60 dark:border-primary/20 dark:bg-[radial-gradient(circle_at_top_left,rgba(0,193,106,0.18),transparent_35%),linear-gradient(135deg,rgba(10,14,12,0.96),rgba(9,24,17,0.9))] sm:p-7"
        >
          <div
            class="grid gap-6 xl:grid-cols-[minmax(0,1.6fr)_minmax(320px,0.9fr)] xl:items-start"
          >
            <div class="space-y-5">
              <div
                class="inline-flex items-center gap-2 rounded-full bg-white/80 px-3 py-1.5 text-xs font-medium text-primary-700 ring-1 ring-primary/10 dark:bg-white/10 dark:text-primary-200"
              >
                <UIcon
                  name="i-lucide-map-pinned"
                  class="size-4"
                />
                서울시 강남구 역삼동 735-11
              </div>

              <div class="space-y-3">
                <h1
                  class="text-3xl font-semibold tracking-tight text-highlighted sm:text-4xl"
                >
                  10초 안에 메뉴 찾고 바로 주문하세요
                </h1>
                <p class="max-w-2xl text-sm leading-6 text-muted sm:text-base">
                  자주 먹는 메뉴, 빠른 배달 매장, 오늘 할인 중인 가게를 한
                  화면에서 고르고 결제까지 이어지는 주문 중심 홈이다.
                </p>
              </div>

              <div class="grid gap-3 sm:grid-cols-[minmax(0,1fr)_auto]">
                <UInput
                  v-model="searchQuery"
                  icon="i-lucide-search"
                  size="xl"
                  placeholder="메뉴명, 매장명, 카테고리를 검색하세요"
                />
                <UButton
                  size="xl"
                  icon="i-lucide-search"
                  class="justify-center px-6"
                  @click="startSearch"
                >
                  주문 시작
                </UButton>
              </div>

              <div class="flex flex-wrap gap-2">
                <UButton
                  v-for="action in quickActions"
                  :key="action"
                  color="neutral"
                  variant="soft"
                  size="sm"
                  class="rounded-full"
                >
                  {{ action }}
                </UButton>
              </div>
            </div>

            <UCard
              class="border-white/60 bg-white/80 shadow-sm backdrop-blur dark:border-white/10 dark:bg-white/5"
            >
              <div class="space-y-4">
                <div class="flex items-start justify-between gap-4">
                  <div>
                    <p class="text-sm font-medium text-muted">
                      현재 가장 빠른 주문
                    </p>
                    <h2 class="mt-1 text-xl font-semibold text-highlighted">
                      역삼 덮밥연구소
                    </h2>
                  </div>
                  <UBadge
                    color="primary"
                    variant="soft"
                  >
                    18~25분
                  </UBadge>
                </div>

                <div class="grid grid-cols-3 gap-3">
                  <div class="rounded-2xl bg-primary/6 p-3">
                    <p class="text-xs text-muted">
                      대표 메뉴
                    </p>
                    <p class="mt-1 text-sm font-semibold text-highlighted">
                      직화 제육
                    </p>
                  </div>
                  <div class="rounded-2xl bg-primary/6 p-3">
                    <p class="text-xs text-muted">
                      배달비
                    </p>
                    <p class="mt-1 text-sm font-semibold text-highlighted">
                      2,000원
                    </p>
                  </div>
                  <div class="rounded-2xl bg-primary/6 p-3">
                    <p class="text-xs text-muted">
                      최소 주문
                    </p>
                    <p class="mt-1 text-sm font-semibold text-highlighted">
                      9,900원
                    </p>
                  </div>
                </div>

                <div
                  class="rounded-2xl border border-dashed border-primary/20 p-4"
                >
                  <div class="flex items-center justify-between text-sm">
                    <span class="text-muted">오늘 적용 가능한 쿠폰</span>
                    <span class="font-semibold text-primary-600">3장</span>
                  </div>
                  <p class="mt-2 text-sm text-highlighted">
                    첫 주문 4,000원 할인과 점심 타임 무료배달 쿠폰을 바로 적용할
                    수 있습니다.
                  </p>
                </div>

                <UButton
                  to="/benefits/coupons"
                  block
                  size="lg"
                  icon="i-lucide-ticket-percent"
                >
                  쿠폰 확인하고 주문하기
                </UButton>
              </div>
            </UCard>
          </div>
        </section>

        <section class="space-y-4">
          <div class="flex items-center justify-between gap-3">
            <div>
              <h2 class="text-lg font-semibold text-highlighted">
                카테고리별 바로 주문
              </h2>
              <p class="text-sm text-muted">
                가장 많이 찾는 메뉴를 한 번에 고를 수 있습니다.
              </p>
            </div>
            <UButton
              color="neutral"
              variant="ghost"
              to="/favorites"
              icon="i-lucide-heart"
            >
              찜한 가게 보기
            </UButton>
          </div>

          <div class="grid grid-cols-2 gap-3 sm:grid-cols-3 xl:grid-cols-6">
            <button
              v-for="category in categories"
              :key="category.id"
              type="button"
              class="flex items-center gap-3 rounded-2xl border p-4 text-left transition hover:border-primary/50 hover:bg-primary/5"
              :class="
                selectedCategory === category.id
                  ? 'border-primary/60 bg-primary/8 shadow-sm'
                  : 'border-default'
              "
              @click="selectedCategory = category.id"
            >
              <div
                class="flex size-10 items-center justify-center rounded-2xl bg-primary/10 text-primary-600"
              >
                <UIcon
                  :name="category.icon"
                  class="size-5"
                />
              </div>
              <div>
                <p class="text-sm font-semibold text-highlighted">
                  {{ category.label }}
                </p>
                <p class="text-xs text-muted">
                  즉시 주문
                </p>
              </div>
            </button>
          </div>
        </section>

        <div
          class="grid gap-6 xl:grid-cols-[minmax(0,1.7fr)_360px] xl:items-start"
        >
          <section class="space-y-4">
            <div class="flex items-center justify-between gap-3">
              <div>
                <h2 class="text-lg font-semibold text-highlighted">
                  지금 주문하기 좋은 매장
                </h2>
                <p class="text-sm text-muted">
                  배달 속도, 재주문율, 할인 여부를 기준으로 정렬했습니다.
                </p>
              </div>
              <UBadge
                color="neutral"
                variant="soft"
              >
                {{ filteredStores.length }}개 매장
              </UBadge>
            </div>

            <div class="grid gap-4 lg:grid-cols-2">
              <UPageCard
                v-for="store in filteredStores"
                :key="store.id"
                :title="store.name"
                :description="`${store.category} · 대표메뉴 ${store.bestseller}`"
                variant="subtle"
                class="overflow-hidden"
              >
                <div class="space-y-4">
                  <div class="flex items-start justify-between gap-4">
                    <div class="flex items-center gap-3">
                      <div
                        class="flex size-12 items-center justify-center rounded-2xl bg-primary/10 text-primary-600"
                      >
                        <UIcon
                          :name="store.heroIcon"
                          class="size-6"
                        />
                      </div>
                      <div class="space-y-1 text-sm text-muted">
                        <p class="flex items-center gap-1.5 text-highlighted">
                          <UIcon
                            name="i-lucide-star"
                            class="size-4 text-amber-400"
                          />
                          <span class="font-semibold">{{ store.rating }}</span>
                          <span>리뷰 {{ store.reviewCount.toLocaleString() }}</span>
                        </p>
                        <p>
                          {{ store.eta }} · {{ store.deliveryFee }} · 최소
                          {{ store.minOrder }}
                        </p>
                      </div>
                    </div>
                    <UBadge
                      color="primary"
                      variant="soft"
                    >
                      추천
                    </UBadge>
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

                  <div class="flex gap-2">
                    <UButton
                      :to="`/stores/${store.id}`"
                      color="neutral"
                      variant="soft"
                      block
                    >
                      메뉴 보기
                    </UButton>
                    <UButton
                      block
                      @click="orderNow(store.id)"
                    >
                      바로 주문
                    </UButton>
                  </div>
                </div>
              </UPageCard>
            </div>

            <div
              v-if="filteredStores.length === 0"
              class="rounded-3xl border border-dashed border-default px-6 py-12 text-center"
            >
              <UIcon
                name="i-lucide-search-x"
                class="mx-auto size-10 text-muted"
              />
              <p class="mt-3 text-sm font-medium text-highlighted">
                검색 조건에 맞는 매장이 없습니다.
              </p>
              <p class="mt-1 text-sm text-muted">
                카테고리를 바꾸거나 다른 메뉴명으로 다시 검색해 보세요.
              </p>
            </div>
          </section>

          <aside class="space-y-4 xl:sticky xl:top-6">
            <UCard>
              <div class="space-y-4">
                <div class="flex items-center justify-between gap-3">
                  <div>
                    <p class="text-sm font-medium text-muted">
                      최근 주문 기반
                    </p>
                    <h2 class="text-lg font-semibold text-highlighted">
                      빠른 재주문
                    </h2>
                  </div>
                  <UButton
                    color="neutral"
                    variant="ghost"
                    size="sm"
                    to="/orders"
                  >
                    전체 보기
                  </UButton>
                </div>

                <div class="space-y-3">
                  <div
                    v-for="item in reorderItems"
                    :key="item.id"
                    class="rounded-2xl border border-default p-4"
                  >
                    <div class="flex items-start justify-between gap-3">
                      <div>
                        <p class="text-sm font-semibold text-highlighted">
                          {{ item.storeName }}
                        </p>
                        <p class="mt-1 text-sm text-muted">
                          {{ item.menu }}
                        </p>
                      </div>
                      <span class="text-sm font-semibold text-highlighted">{{
                        item.totalPrice
                      }}</span>
                    </div>
                    <div class="mt-3 flex items-center justify-between gap-3">
                      <span class="text-xs text-muted">{{
                        item.orderedAt
                      }}</span>
                      <UButton
                        size="sm"
                        variant="soft"
                        @click="orderNow(item.storeId)"
                      >
                        다시 담기
                      </UButton>
                    </div>
                  </div>
                </div>
              </div>
            </UCard>

            <UCard class="border-primary/15 bg-primary/5">
              <div class="space-y-3">
                <div
                  class="flex items-center gap-2 text-primary-700 dark:text-primary-300"
                >
                  <UIcon
                    name="i-lucide-badge-percent"
                    class="size-5"
                  />
                  <p class="text-sm font-semibold">
                    오늘의 주문 혜택
                  </p>
                </div>
                <h3 class="text-lg font-semibold text-highlighted">
                  점심시간 무료배달 + 첫 주문 4,000원 할인
                </h3>
                <p class="text-sm leading-6 text-muted">
                  11시부터 14시까지 한식, 분식 카테고리 주문 시 무료배달이 자동
                  적용됩니다.
                </p>
                <UButton
                  to="/benefits/events"
                  color="primary"
                  variant="soft"
                  block
                >
                  진행 중인 이벤트 보기
                </UButton>
              </div>
            </UCard>
          </aside>
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
