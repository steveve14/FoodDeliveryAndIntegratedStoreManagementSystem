<script setup lang="ts">
const toast = useToast();

const profile = ref({
  name: '김회원',
  email: 'kim@example.com',
  phone: '010-1234-5678',
  address: '서울시 강남구 역삼동 123-45',
});

const notifications = ref({
  orderStatus: true,
  promotions: false,
  reviews: true,
});

const saveSettings = () => {
  toast.add({
    title: '설정이 저장되었습니다.',
    icon: 'i-lucide-check-circle',
    color: 'success',
  });
};
</script>

<template>
  <UDashboardPanel id="settings" :ui="{ body: 'lg:py-12' }">
    <template #header>
      <UDashboardNavbar title="내 정보 설정">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-8 w-full lg:max-w-2xl mx-auto p-4">
        <!-- 프로필 정보 -->
        <div class="rounded-lg border border-gray-200 dark:border-gray-700 p-6">
          <h3 class="text-base font-semibold mb-4">프로필 정보</h3>
          <div class="flex flex-col gap-4">
            <UInput v-model="profile.name" placeholder="이름" />
            <UInput v-model="profile.email" placeholder="이메일" type="email" />
            <UInput v-model="profile.phone" placeholder="전화번호" />
            <UInput v-model="profile.address" placeholder="기본 배달 주소" />
          </div>
        </div>

        <!-- 알림 설정 -->
        <div class="rounded-lg border border-gray-200 dark:border-gray-700 p-6">
          <h3 class="text-base font-semibold mb-4">알림 설정</h3>
          <div class="flex flex-col gap-4">
            <div class="flex items-center justify-between">
              <span class="text-sm">주문 상태 알림</span>
              <USwitch v-model="notifications.orderStatus" />
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm">프로모션 알림</span>
              <USwitch v-model="notifications.promotions" />
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm">리뷰 답변 알림</span>
              <USwitch v-model="notifications.reviews" />
            </div>
          </div>
        </div>

        <div class="flex justify-end">
          <UButton label="저장" color="primary" @click="saveSettings" />
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
