<script setup lang="ts">
// 현재 라우트 확인을 위해 사용 (활성화 상태 표시)
const route = useRoute()

const tabs = [
  { name: '홈', icon: 'i-heroicons-home', path: '/' },
  { name: '주문내역', icon: 'i-heroicons-document-text', path: '/orders' },
  { name: '즐겨찾기', icon: 'i-heroicons-heart', path: '/favorites' },
  { name: '마이', icon: 'i-heroicons-user', path: '/profile' },
]
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- 페이지 내용이 여기에 표시됩니다 -->
    <slot />

    <!-- Bottom Navigation -->
    <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 safe-area-pb z-50">
      <div class="max-w-md mx-auto">
        <div class="flex justify-around py-2">
          <NuxtLink
            v-for="tab in tabs"
            :key="tab.name"
            :to="tab.path"
            class="flex flex-col items-center py-2 px-4 transition-colors"
            :class="route.path === tab.path ? 'text-green-600' : 'text-gray-400 hover:text-gray-600'"
          >
            <!-- Nuxt UI 아이콘 사용 -->
            <UIcon :name="tab.icon" class="w-6 h-6 mb-1" />
            <span class="text-xs font-medium">{{ tab.name }}</span>
          </NuxtLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 아이폰 하단 바 대응 */
.safe-area-pb {
  padding-bottom: env(safe-area-inset-bottom);
}
</style>