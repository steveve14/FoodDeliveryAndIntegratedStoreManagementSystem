<script setup lang="ts">
interface Coupon {
  id: number;
  name: string;
  discount: string;
  minOrder: string;
  expiresAt: string;
  isUsed: boolean;
}

const coupons = ref<Coupon[]>([
  {
    id: 1,
    name: '첫 주문 할인',
    discount: '3,000원',
    minOrder: '15,000원 이상',
    expiresAt: '2026-03-31',
    isUsed: false,
  },
  {
    id: 2,
    name: '치킨 카테고리 할인',
    discount: '2,000원',
    minOrder: '18,000원 이상',
    expiresAt: '2026-04-15',
    isUsed: false,
  },
  {
    id: 3,
    name: '리뷰 작성 감사 쿠폰',
    discount: '1,000원',
    minOrder: '10,000원 이상',
    expiresAt: '2026-02-28',
    isUsed: true,
  },
]);

const activeCoupons = computed(() =>
  coupons.value.filter((c: Coupon) => !c.isUsed),
);
const usedCoupons = computed(() =>
  coupons.value.filter((c: Coupon) => c.isUsed),
);
</script>

<template>
  <UDashboardPanel grow>
    <template #header>
      <UDashboardNavbar title="내 쿠폰함" :badge="activeCoupons.length">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-6 p-4">
        <div>
          <h3 class="text-sm font-semibold text-gray-900 dark:text-white mb-3">
            사용 가능한 쿠폰
          </h3>
          <div class="flex flex-col gap-3">
            <div
              v-for="coupon in activeCoupons"
              :key="coupon.id"
              class="flex items-center gap-4 rounded-lg border-2 border-dashed border-primary-300 dark:border-primary-700 p-4 bg-primary-50/50 dark:bg-primary-900/10"
            >
              <div class="flex-1">
                <div class="font-semibold text-sm">
                  {{ coupon.name }}
                </div>
                <div class="text-xs text-gray-500 mt-1">
                  {{ coupon.minOrder }} | ~ {{ coupon.expiresAt }}
                </div>
              </div>
              <span
                class="text-lg font-bold text-primary-600 dark:text-primary-400"
                >{{ coupon.discount }}</span
              >
            </div>
          </div>
          <div
            v-if="activeCoupons.length === 0"
            class="text-center py-8 text-sm text-gray-500"
          >
            사용 가능한 쿠폰이 없습니다.
          </div>
        </div>

        <div>
          <h3 class="text-sm font-semibold text-gray-500 mb-3">사용 완료</h3>
          <div class="flex flex-col gap-3">
            <div
              v-for="coupon in usedCoupons"
              :key="coupon.id"
              class="flex items-center gap-4 rounded-lg border border-gray-200 dark:border-gray-700 p-4 opacity-50"
            >
              <div class="flex-1">
                <div class="font-semibold text-sm line-through">
                  {{ coupon.name }}
                </div>
                <div class="text-xs text-gray-400 mt-1">
                  {{ coupon.minOrder }} | ~ {{ coupon.expiresAt }}
                </div>
              </div>
              <span class="text-lg font-bold text-gray-400">{{
                coupon.discount
              }}</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
