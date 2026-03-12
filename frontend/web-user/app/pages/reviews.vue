<script setup lang="ts">
interface Review {
  id: number;
  storeName: string;
  menu: string;
  rating: number;
  content: string;
  createdAt: string;
}

const reviews = ref<Review[]>([
  {
    id: 1,
    storeName: '맛있는 치킨 본점',
    menu: '황금 올리브 치킨',
    rating: 5,
    content: '바삭하고 정말 맛있어요! 배달도 빨랐습니다.',
    createdAt: '2026-03-05',
  },
  {
    id: 2,
    storeName: '매콤 떡볶이',
    menu: '로제 떡볶이 세트',
    rating: 4,
    content: '양도 많고 맛있었어요. 다음에 또 시킬게요.',
    createdAt: '2026-03-01',
  },
]);

const renderStars = (rating: number) => {
  return Array.from({ length: 5 }, (_, i) => i < rating);
};
</script>

<template>
  <UDashboardPanel grow>
    <template #header>
      <UDashboardNavbar title="내 리뷰 관리" :badge="reviews.length">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-4 p-4">
        <div
          v-for="review in reviews"
          :key="review.id"
          class="rounded-lg border border-gray-200 dark:border-gray-700 p-4 shadow-sm"
        >
          <div class="flex items-center justify-between mb-2">
            <div>
              <span class="font-semibold text-sm">{{ review.storeName }}</span>
              <span class="text-xs text-gray-500 ml-2">{{ review.menu }}</span>
            </div>
            <span class="text-xs text-gray-400">{{ review.createdAt }}</span>
          </div>
          <div class="flex items-center gap-0.5 mb-2">
            <UIcon
              v-for="(filled, idx) in renderStars(review.rating)"
              :key="idx"
              name="i-lucide-star"
              class="w-4 h-4"
              :class="filled ? 'text-yellow-400' : 'text-gray-300'"
            />
          </div>
          <p class="text-sm text-gray-700 dark:text-gray-300">
            {{ review.content }}
          </p>
        </div>

        <div
          v-if="reviews.length === 0"
          class="text-center py-16 text-gray-500"
        >
          <UIcon
            name="i-lucide-star"
            class="w-10 h-10 mx-auto mb-2 text-gray-300"
          />
          <p class="text-sm">작성한 리뷰가 없습니다.</p>
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
