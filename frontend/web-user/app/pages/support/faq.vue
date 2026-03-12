<script setup lang="ts">
interface FaqItem {
  id: number;
  question: string;
  answer: string;
  category: string;
}

const faqs = ref<FaqItem[]>([
  {
    id: 1,
    question: '주문 취소는 어떻게 하나요?',
    answer:
      "주문 접수 전이라면 '주문 내역'에서 취소 버튼을 눌러 취소할 수 있습니다. 이미 접수된 주문은 가게에 직접 연락해 주세요.",
    category: '주문',
  },
  {
    id: 2,
    question: '배달 예상 시간이 지났는데 안 와요.',
    answer:
      "배달 지연 시 '주문 내역 > 배달 현황'에서 실시간 위치를 확인하실 수 있습니다. 30분 이상 지연 시 고객센터로 문의해 주세요.",
    category: '배달',
  },
  {
    id: 3,
    question: '쿠폰은 어떻게 사용하나요?',
    answer:
      "결제 시 '쿠폰 적용' 버튼을 눌러 사용 가능한 쿠폰을 선택할 수 있습니다. 최소 주문 금액 조건을 확인해 주세요.",
    category: '혜택',
  },
  {
    id: 4,
    question: '환불은 언제 되나요?',
    answer:
      '결제 수단에 따라 1~3 영업일 이내 환불이 진행됩니다. 카드 결제의 경우 카드사 사정에 따라 다소 지연될 수 있습니다.',
    category: '결제',
  },
]);

const selectedCategory = ref('전체');
const categories = ['전체', '주문', '배달', '혜택', '결제'];

const filteredFaqs = computed(() => {
  if (selectedCategory.value === '전체') {
    return faqs.value;
  }
  return faqs.value.filter(
    (faq: FaqItem) => faq.category === selectedCategory.value,
  );
});
</script>

<template>
  <UDashboardPanel grow>
    <template #header>
      <UDashboardNavbar title="자주 묻는 질문">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>

      <UDashboardToolbar>
        <template #left>
          <div class="flex gap-2">
            <UButton
              v-for="cat in categories"
              :key="cat"
              :label="cat"
              :variant="selectedCategory === cat ? 'solid' : 'ghost'"
              :color="selectedCategory === cat ? 'primary' : 'neutral'"
              size="sm"
              @click="selectedCategory = cat"
            />
          </div>
        </template>
      </UDashboardToolbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-3 p-4">
        <UAccordion
          :items="
            filteredFaqs.map((faq: FaqItem) => ({
              label: faq.question,
              content: faq.answer,
              value: String(faq.id),
            }))
          "
        />

        <div
          v-if="filteredFaqs.length === 0"
          class="text-center py-16 text-gray-500"
        >
          <UIcon
            name="i-lucide-help-circle"
            class="w-10 h-10 mx-auto mb-2 text-gray-300"
          />
          <p class="text-sm">해당 카테고리의 질문이 없습니다.</p>
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
