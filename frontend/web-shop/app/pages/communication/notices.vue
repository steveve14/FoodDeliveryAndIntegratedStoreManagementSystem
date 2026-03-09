<script setup lang="ts">
const noticeTitle = ref('주말 배달 지연 안내');
const noticeContent = ref(
  '주말 저녁 시간대 주문 증가로 배달이 10~20분 지연될 수 있습니다. 양해 부탁드립니다.',
);
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="가게 공지사항" />

    <div class="p-4">
      <UPageCard
        title="고객 노출 공지 작성"
        description="배달앱/매장 페이지 상단에 노출될 안내 문구를 관리합니다."
        variant="subtle"
      >
        <div class="space-y-4">
          <UFormField label="제목">
            <UInput
              v-model="noticeTitle"
              placeholder="공지 제목을 입력하세요"
            />
          </UFormField>
          <UFormField label="내용">
            <UTextarea v-model="noticeContent" :rows="5" />
          </UFormField>
          <UButton label="공지 저장" color="primary" />
        </div>
      </UPageCard>
    </div>
  </UDashboardPanel>
</template>
