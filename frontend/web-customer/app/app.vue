<script setup lang="ts">
const toast = useToast()

const onClick = () => {
  toast.add({ title: '주문 완료', description: '맛있는 음식이 배달됩니다!' })
}
</script>

<template>
  <UContainer class="py-10">
    <UCard>
      <template #header>
        <h1 class="text-xl font-bold">배달 앱 테스트</h1>
      </template>

      <div class="space-y-4">
        <p>Nuxt UI + pnpm 설정이 완료되었습니다.</p>
        
        <!-- Nuxt UI 컴포넌트 사용 (UButton, UInput 등) -->
        <div class="flex gap-2">
          <UInput placeholder="메뉴를 검색하세요" />
          <UButton icon="i-heroicons-magnifying-glass" @click="onClick">
            검색
          </UButton>
        </div>
      </div>
    </UCard>
    
    <!-- 토스트 알림을 위한 컴포넌트 -->
    <UNotifications />
  </UContainer>
</template>