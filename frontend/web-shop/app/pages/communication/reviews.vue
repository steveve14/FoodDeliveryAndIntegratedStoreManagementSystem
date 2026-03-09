<script setup lang="ts">
interface Review {
  id: number;
  customer: string;
  rating: number;
  content: string;
  replied: boolean;
}

const reviews = ref<Review[]>([
  {
    id: 1,
    customer: '단골손님',
    rating: 5,
    content: '항상 바삭하고 빨라요. 다음에도 주문할게요.',
    replied: true,
  },
  {
    id: 2,
    customer: '처음주문',
    rating: 3,
    content: '맛은 좋았지만 배달 시간이 조금 길었어요.',
    replied: false,
  },
]);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="리뷰 답글" :badge="reviews.length" />

    <div class="p-4 space-y-4">
      <UPageCard
        v-for="review in reviews"
        :key="review.id"
        :title="`${review.customer}님의 리뷰`"
        variant="subtle"
      >
        <div class="space-y-2">
          <p class="text-sm">평점: {{ "★".repeat(review.rating) }}</p>
          <p class="text-sm text-muted">{{ review.content }}</p>
          <UBadge
            :color="review.replied ? 'success' : 'warning'"
            variant="subtle"
          >
            {{ review.replied ? "답글 완료" : "답글 필요" }}
          </UBadge>
        </div>
      </UPageCard>
    </div>
  </UDashboardPanel>
</template>
