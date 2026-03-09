<script setup lang="ts">
import type { DropdownMenuItem } from '@nuxt/ui';
import { formatWon, useOrdering } from '~/composables/useOrdering';

type NavigationMenuItem = DropdownMenuItem;

const route = useRoute();
const open = ref(false);
const toast = useToast();
const { cartItems, cartTotal, loadCart, removeCartItem } = useOrdering();

await loadCart();

// --- 1. 장바구니 관련 상태 및 데이터 (기존 배달 기사 호출 대체) ---
const isCartModalOpen = ref(false);

// 장바구니 모달 열기
const openCartModal = () => {
  isCartModalOpen.value = true;
};

// 결제 페이지로 이동 로직 (모달에서 결제 클릭 시)
const proceedToCheckout = () => {
  if (cartItems.value.length === 0) {
    toast.add({
      title: '장바구니가 비어있습니다',
      description: '메뉴를 먼저 담아주세요.',
      icon: 'i-lucide-alert-circle',
      color: 'error',
    });
    return;
  }

  toast.add({
    title: '결제 페이지로 이동합니다',
    icon: 'i-lucide-credit-card',
    color: 'primary',
  });

  isCartModalOpen.value = false;
  navigateTo('/checkout');
};

const deleteCartItem = async (cartItemId: string) => {
  await removeCartItem(cartItemId);
  toast.add({
    title: '장바구니에서 삭제했습니다.',
    icon: 'i-lucide-trash-2',
    color: 'success',
  });
};
// ----------------------------------------------------

// 2. 네비게이션 메뉴 구성 (사용자 관점)
const links = computed(
  () =>
    [
      [
        {
          label: '홈',
          icon: 'i-lucide-house',
          to: '/',
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: '주문 내역',
          icon: 'i-lucide-receipt',
          to: '/orders',
          badge: '1', // 배달 중인 주문 알림
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: '찜한 가게',
          icon: 'i-lucide-heart',
          to: '/favorites',
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: '내 리뷰 관리',
          icon: 'i-lucide-star',
          to: '/reviews',
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: '혜택 및 쿠폰',
          id: 'benefits',
          icon: 'i-lucide-ticket',
          active: route.path.startsWith('/benefits'),
          defaultOpen: true,
          children: [
            {
              label: '내 쿠폰함',
              to: '/benefits/coupons',
              icon: 'i-lucide-tag',
            },
            {
              label: '이벤트',
              to: '/benefits/events',
              icon: 'i-lucide-gift',
            },
          ],
        },
        {
          label: '고객 센터',
          id: 'support',
          icon: 'i-lucide-headphones',
          active: route.path.startsWith('/support'),
          defaultOpen: false,
          children: [
            {
              label: '자주 묻는 질문',
              to: '/support/faq',
              icon: 'i-lucide-help-circle',
            },
            {
              label: '1:1 문의',
              to: '/support/inquiry',
              icon: 'i-lucide-message-square',
            },
          ],
        },
      ],
      [
        {
          label: '내 정보 설정',
          icon: 'i-lucide-user-cog',
          to: '/settings',
        },
      ],
    ] satisfies NavigationMenuItem[][],
);

// 3. 검색 그룹 (사용자 음식 검색 용도)
const groups = computed(() => [
  {
    id: 'recent-searches',
    label: '최근 검색어',
    items: [
      { label: '마라탕', icon: 'i-lucide-search' },
      { label: '초밥', icon: 'i-lucide-search' },
    ],
  },
  {
    id: 'popular-categories',
    label: '인기 카테고리',
    items: [
      { label: '치킨', icon: 'i-lucide-flame' },
      { label: '피자/양식', icon: 'i-lucide-flame' },
      { label: '카페/디저트', icon: 'i-lucide-flame' },
    ],
  },
]);
</script>

<template>
  <UDashboardGroup unit="rem">
    <UDashboardSidebar
      id="customer-sidebar"
      v-model:open="open"
      collapsible
      resizable
      class="bg-background/50 backdrop-blur"
    >
      <template #header="{ collapsed }">
        <div class="flex items-center gap-2 px-2 py-2 overflow-hidden">
          <div
            class="w-8 h-8 rounded-full bg-primary-100 dark:bg-primary-900 flex items-center justify-center text-primary-600 dark:text-primary-400 font-bold shrink-0"
          >
            <UIcon name="i-lucide-user" class="w-5 h-5" />
          </div>
          <div v-if="!collapsed" class="flex flex-col leading-tight min-w-0">
            <span class="font-bold truncate text-sm">김회원 님</span>
            <div class="flex items-center gap-1 mt-0.5">
              <UIcon name="i-lucide-map-pin" class="w-3 h-3 text-gray-500" />
              <span class="text-[11px] text-gray-500 truncate"
                >서울시 강남구 역삼동...</span
              >
            </div>
          </div>
        </div>
      </template>

      <template #default="{ collapsed }">
        <div class="px-2 mb-2">
          <UButton
            v-if="!collapsed"
            label="장바구니 확인"
            icon="i-lucide-shopping-cart"
            block
            @click="openCartModal"
          >
            <template #trailing>
              <UBadge color="primary" variant="solid" size="xs">
                {{ cartItems.length }}
              </UBadge>
            </template>
          </UButton>
          <UButton
            v-else
            icon="i-lucide-shopping-cart"
            variant="soft"
            @click="openCartModal"
          >
            <UBadge
              color="primary"
              variant="solid"
              size="xs"
              class="absolute -top-1 -right-1"
            >
              {{ cartItems.length }}
            </UBadge>
          </UButton>
        </div>

        <UDashboardSearchButton
          :collapsed="collapsed"
          class="bg-transparent ring-default"
          label="먹고 싶은 메뉴 검색..."
        />

        <UNavigationMenu
          :collapsed="collapsed"
          :items="links[0]"
          orientation="vertical"
          tooltip
          popover
        />

        <UNavigationMenu
          :collapsed="collapsed"
          :items="links[1]"
          orientation="vertical"
          tooltip
          class="mt-auto"
        />
      </template>

      <template #footer="{ collapsed }">
        <UserMenu :collapsed="collapsed" />
      </template>
    </UDashboardSidebar>

    <UDashboardSearch
      :groups="groups"
      placeholder="식당 이름이나 메뉴를 검색해보세요"
    />

    <slot />

    <NotificationsSlideover />

    <UModal v-model:open="isCartModalOpen" title="내 장바구니">
      <template #content>
        <UCard class="ring-0 divide-y divide-gray-100 dark:divide-gray-800">
          <template #header>
            <div class="flex items-center justify-between">
              <h3
                class="text-base font-semibold leading-6 text-gray-900 dark:text-white"
              >
                장바구니
              </h3>
              <UButton
                color="neutral"
                variant="ghost"
                icon="i-heroicons-x-mark-20-solid"
                class="-my-1"
                @click="isCartModalOpen = false"
              />
            </div>
          </template>

          <div class="flex flex-col gap-3 max-h-75 overflow-y-auto p-1">
            <div
              v-for="item in cartItems"
              :key="item.id"
              class="flex flex-col rounded-lg border border-gray-200 dark:border-gray-700 p-4 shadow-sm"
            >
              <div class="flex items-start justify-between gap-3">
                <div>
                  <span class="text-xs font-semibold text-primary-500 mb-1">{{
                    item.storeName
                  }}</span>
                  <div class="flex w-full items-center justify-between gap-4">
                    <span
                      class="text-sm font-medium text-gray-900 dark:text-white"
                    >
                      {{ item.menu }}
                      <span class="text-gray-500 text-xs ml-1"
                        >x {{ item.quantity }}</span
                      >
                    </span>
                  </div>
                </div>
                <div class="flex items-center gap-2">
                  <span class="text-sm font-bold text-gray-900 dark:text-white">
                    {{ formatWon(item.price * item.quantity) }}
                  </span>
                  <UButton
                    color="neutral"
                    variant="ghost"
                    icon="i-lucide-trash-2"
                    size="xs"
                    @click="deleteCartItem(item.id)"
                  />
                </div>
              </div>
            </div>

            <div
              v-if="cartItems.length === 0"
              class="text-center py-8 text-gray-500 text-sm flex flex-col items-center gap-2"
            >
              <UIcon
                name="i-lucide-shopping-bag"
                class="w-8 h-8 text-gray-300"
              />
              장바구니에 담긴 메뉴가 없습니다.
            </div>
          </div>

          <template #footer>
            <div class="space-y-4">
              <div
                class="flex items-center justify-between rounded-lg bg-neutral-50 px-4 py-3 text-sm dark:bg-neutral-900/60"
              >
                <span class="text-gray-500 dark:text-gray-400"
                  >총 결제 예정 금액</span
                >
                <span class="font-semibold text-gray-900 dark:text-white">{{
                  formatWon(cartTotal)
                }}</span>
              </div>

              <div class="flex justify-end gap-2">
                <UButton
                  color="neutral"
                  variant="ghost"
                  label="더 담기"
                  @click="isCartModalOpen = false"
                />
                <UButton
                  color="primary"
                  label="결제하러 가기"
                  :disabled="cartItems.length === 0"
                  @click="proceedToCheckout"
                />
              </div>
            </div>
          </template>
        </UCard>
      </template>
    </UModal>
  </UDashboardGroup>
</template>
