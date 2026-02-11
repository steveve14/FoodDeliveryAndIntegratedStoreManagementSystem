<script setup lang="ts">
import type { NavigationMenuItem } from "@nuxt/ui";

const route = useRoute();
const open = ref(false);
const toast = useToast();

// --- [추가됨] 1. 배달 기사 호출 관련 상태 및 데이터 ---
const isRiderModalOpen = ref(false);
const selectedOrderId = ref<number | null>(null);

// 배달 대기 중인 주문 더미 데이터
const pendingOrders = ref([
  {
    id: 101,
    menu: "황금 올리브 치킨 외 2건",
    address: "서울시 강남구 역삼동 123-45",
    time: "10분 전 접수",
    price: "24,000원",
  },
  {
    id: 102,
    menu: "양념 반 후라이드 반",
    address: "서울시 강남구 논현동 55-1",
    time: "5분 전 접수",
    price: "19,000원",
  },
  {
    id: 103,
    menu: "치즈볼 세트 A",
    address: "서울시 서초구 서초동 777",
    time: "방금 전 접수",
    price: "12,000원",
  },
]);

// 기사 호출 모달 열기
const openCallRiderModal = () => {
  // 초기화 및 모달 오픈
  selectedOrderId.value = null;
  isRiderModalOpen.value = true;
};

// 실제 기사 호출 로직 (모달에서 확인 클릭 시)
const callRider = () => {
  if (!selectedOrderId.value) {
    toast.add({
      title: "주문 선택 필요",
      description: "배달할 주문을 먼저 선택해주세요.",
      icon: "i-lucide-alert-circle",
    });
    return;
  }

  const order = pendingOrders.value.find((o) => o.id === selectedOrderId.value);

  toast.add({
    title: "배달 호출 완료",
    description: `'${order?.menu}' 건에 대해 기사님을 호출했습니다.`,
    icon: "i-lucide-bell",
    color: "primary",
  });

  isRiderModalOpen.value = false;
};
// ----------------------------------------------------

// 2. 네비게이션 메뉴 구성
const links = computed(
  () =>
    [
      [
        {
          label: "홈",
          icon: "i-lucide-house",
          to: "/",
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: "메뉴/상품 관리",
          icon: "i-lucide-utensils",
          to: "/products",
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: "재고 관리",
          icon: "i-lucide-package",
          to: "/inventory",
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: "실시간 주문접수",
          icon: "i-lucide-list-checks",
          to: "/orders",
          badge: "3", // 신규 주문 개수
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: "배달 관리",
          id: "delivery",
          icon: "i-lucide-truck",
          active: route.path.startsWith("/delivery"),
          defaultOpen: true,
          children: [
            {
              label: "라이더 실시간 위치",
              to: "/delivery/tracking",
              icon: "i-lucide-map-pin",
            },
            {
              label: "배달원/기사 관리",
              to: "/delivery/riders",
              icon: "i-lucide-bike",
            },
            {
              label: "배달 대행사 설정",
              to: "/delivery/agencies",
              icon: "i-lucide-building-2",
            },
          ],
        },

        {
          label: "리뷰 및 고객소통",
          id: "communication",
          icon: "i-lucide-messages-square",
          active: route.path.startsWith("/communication"),
          defaultOpen: route.path.startsWith("/communication"),
          children: [
            {
              label: "리뷰 답글",
              to: "/communication/reviews",
              icon: "i-lucide-star",
            },
            {
              label: "단골 관리",
              to: "/communication/customers",
              icon: "i-lucide-users",
            },
            {
              label: "가게 공지사항",
              to: "/communication/notices",
              icon: "i-lucide-megaphone",
            },
          ],
        },
        {
          label: "매출 및 정산",
          id: "finance",
          icon: "i-lucide-banknote",
          active: route.path.startsWith("/finance"),
          defaultOpen: route.path.startsWith("/finance"),
          children: [
            {
              label: "판매 리포트",
              to: "/finance/reports",
              icon: "i-lucide-trending-up",
            },
            {
              label: "정산 내역",
              to: "/finance/settlements",
              icon: "i-lucide-receipt-text",
            },
          ],
        },
        {
          label: "마케팅 도구",
          icon: "i-lucide-ticket",
          to: "/marketing",
          onSelect: () => {
            open.value = false;
          },
        },
      ],
      [
        {
          label: "매장 설정",
          icon: "i-lucide-store",
          to: "/settings",
        },
      ],
    ] satisfies NavigationMenuItem[][],
);

// 3. 검색 그룹 (커맨드 팰릿 용)
const groups = computed(() => [
  {
    id: "active-riders",
    label: "운행 중인 기사",
    items: [
      {
        label: "김배달 (010-1234-5678)",
        icon: "i-lucide-bike",
        suffix: "배송 중",
      },
      {
        label: "이라이더 (010-9876-5432)",
        icon: "i-lucide-bike",
        suffix: "대기 중",
      },
    ],
  },
  {
    id: "agencies",
    label: "연동 대행사 상태",
    items: [
      { label: "생생배달 (정상)", icon: "i-lucide-check-circle" },
      { label: "바로고 (지연-기상악화)", icon: "i-lucide-alert-triangle" },
    ],
  },
]);
</script>

<template>
  <UDashboardGroup unit="rem">
    <UDashboardSidebar
      id="store-admin-sidebar"
      v-model:open="open"
      collapsible
      resizable
      class="bg-background/50 backdrop-blur"
    >
      <template #header="{ collapsed }">
        <div class="flex items-center gap-2 px-2 py-2 overflow-hidden">
          <div
            class="w-8 h-8 rounded-lg bg-primary-600 flex items-center justify-center text-white font-bold"
          >
            <UIcon name="i-lucide-store" class="w-5 h-5" />
          </div>
          <div v-if="!collapsed" class="flex flex-col leading-tight min-w-0">
            <span class="font-bold truncate text-sm">맛있는 치킨 본점</span>
            <div class="flex items-center gap-1">
              <span class="w-1.5 h-1.5 rounded-full bg-green-500"></span>
              <span class="text-[10px] text-gray-500 uppercase font-medium"
                >영업 중</span
              >
            </div>
          </div>
        </div>
      </template>

      <template #default="{ collapsed }">
        <div class="px-2 mb-2">
          <UButton
            v-if="!collapsed"
            label="배달 기사 호출"
            icon="i-lucide-bike"
            block
            @click="openCallRiderModal"
          />
          <UButton
            v-else
            icon="i-lucide-bike"
            variant="soft"
            @click="openCallRiderModal"
          />
        </div>

        <UDashboardSearchButton
          :collapsed="collapsed"
          class="bg-transparent ring-default"
          label="검색..."
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

    <UDashboardSearch :groups="groups" placeholder="사용자, 매장, 주문 등..." />

    <slot />

    <NotificationsSlideover />

    <UModal v-model:open="isRiderModalOpen" title="배달 요청할 주문 선택">
      <template #content>
        <UCard class="ring-0 divide-y divide-gray-100 dark:divide-gray-800">
          <template #header>
            <div class="flex items-center justify-between">
              <h3
                class="text-base font-semibold leading-6 text-gray-900 dark:text-white"
              >
                배달 요청할 주문 선택
              </h3>
              <UButton
                color="neutral"
                variant="ghost"
                icon="i-heroicons-x-mark-20-solid"
                class="-my-1"
                @click="isRiderModalOpen = false"
              />
            </div>
          </template>

          <div class="flex flex-col gap-2 max-h-75 overflow-y-auto p-1">
            <div
              v-for="order in pendingOrders"
              :key="order.id"
              class="relative flex cursor-pointer rounded-lg border px-4 py-3 shadow-sm focus:outline-none"
              :class="[
                selectedOrderId === order.id
                  ? 'border-primary-500 ring-2 ring-primary-500 bg-primary-50 dark:bg-primary-950/20'
                  : 'border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800',
              ]"
              @click="selectedOrderId = order.id"
            >
              <div class="flex w-full items-center justify-between">
                <div class="flex flex-col">
                  <span
                    class="text-sm font-medium text-gray-900 dark:text-white"
                  >
                    {{ order.menu }}
                  </span>
                  <span class="text-xs text-gray-500 mt-1">{{
                    order.address
                  }}</span>
                  <span class="text-xs text-primary-500 mt-0.5 font-bold">{{
                    order.time
                  }}</span>
                </div>
                <div class="flex flex-col items-end">
                  <span
                    class="text-sm font-semibold text-gray-900 dark:text-white"
                  >
                    {{ order.price }}
                  </span>
                  <UIcon
                    v-if="selectedOrderId === order.id"
                    name="i-heroicons-check-circle-20-solid"
                    class="h-5 w-5 text-primary-600 mt-2"
                  />
                </div>
              </div>
            </div>

            <div
              v-if="pendingOrders.length === 0"
              class="text-center py-6 text-gray-500 text-sm"
            >
              배달 대기 중인 주문이 없습니다.
            </div>
          </div>

          <template #footer>
            <div class="flex justify-end gap-2">
              <UButton
                color="neutral"
                variant="ghost"
                label="취소"
                @click="isRiderModalOpen = false"
              />
              <UButton
                color="primary"
                label="기사님 호출하기"
                :disabled="!selectedOrderId"
                @click="callRider"
              />
            </div>
          </template>
        </UCard>
      </template>
    </UModal>
  </UDashboardGroup>
</template>
