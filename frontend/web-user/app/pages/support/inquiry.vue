<script setup lang="ts">
const toast = useToast();

const inquiryForm = ref({
  category: '',
  title: '',
  content: '',
});

const categoryOptions = [
  { label: '주문/결제', value: 'order' },
  { label: '배달', value: 'delivery' },
  { label: '환불/취소', value: 'refund' },
  { label: '앱 이용', value: 'app' },
  { label: '기타', value: 'etc' },
];

interface InquiryHistory {
  id: number;
  title: string;
  category: string;
  status: 'waiting' | 'answered';
  createdAt: string;
}

const inquiries = ref<InquiryHistory[]>([
  {
    id: 1,
    title: '배달이 너무 늦게 도착했습니다',
    category: '배달',
    status: 'answered',
    createdAt: '2026-03-03',
  },
  {
    id: 2,
    title: '쿠폰이 적용되지 않아요',
    category: '주문/결제',
    status: 'waiting',
    createdAt: '2026-03-06',
  },
]);

const statusMap = {
  waiting: { label: '답변 대기', color: 'warning' as const },
  answered: { label: '답변 완료', color: 'success' as const },
};

const submitInquiry = () => {
  if (
    !inquiryForm.value.category ||
    !inquiryForm.value.title ||
    !inquiryForm.value.content
  ) {
    toast.add({
      title: '모든 항목을 입력해 주세요.',
      icon: 'i-lucide-alert-circle',
      color: 'error',
    });
    return;
  }

  toast.add({
    title: '문의가 접수되었습니다.',
    description: '확인 후 빠르게 답변 드리겠습니다.',
    icon: 'i-lucide-check-circle',
    color: 'success',
  });

  inquiryForm.value = { category: '', title: '', content: '' };
};
</script>

<template>
  <UDashboardPanel grow>
    <template #header>
      <UDashboardNavbar title="1:1 문의">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-8 p-4 max-w-2xl mx-auto">
        <!-- 문의 작성 -->
        <div class="rounded-lg border border-gray-200 dark:border-gray-700 p-6">
          <h3 class="text-base font-semibold mb-4">새 문의 작성</h3>
          <div class="flex flex-col gap-4">
            <USelect
              v-model="inquiryForm.category"
              :items="categoryOptions"
              placeholder="문의 유형을 선택하세요"
            />
            <UInput
              v-model="inquiryForm.title"
              placeholder="제목을 입력하세요"
            />
            <UTextarea
              v-model="inquiryForm.content"
              placeholder="문의 내용을 입력하세요"
              :rows="5"
            />
            <div class="flex justify-end">
              <UButton
                label="문의 접수"
                color="primary"
                @click="submitInquiry"
              />
            </div>
          </div>
        </div>

        <!-- 문의 내역 -->
        <div>
          <h3 class="text-base font-semibold mb-3">문의 내역</h3>
          <div class="flex flex-col gap-3">
            <div
              v-for="inquiry in inquiries"
              :key="inquiry.id"
              class="flex items-center justify-between rounded-lg border border-gray-200 dark:border-gray-700 p-4"
            >
              <div>
                <div class="font-medium text-sm">{{ inquiry.title }}</div>
                <div class="text-xs text-gray-500 mt-1">
                  {{ inquiry.category }} · {{ inquiry.createdAt }}
                </div>
              </div>
              <UBadge
                :color="statusMap[inquiry.status].color"
                variant="subtle"
                size="sm"
              >
                {{ statusMap[inquiry.status].label }}
              </UBadge>
            </div>
          </div>

          <div
            v-if="inquiries.length === 0"
            class="text-center py-8 text-sm text-gray-500"
          >
            문의 내역이 없습니다.
          </div>
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
