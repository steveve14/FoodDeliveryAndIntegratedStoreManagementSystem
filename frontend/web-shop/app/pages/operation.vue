<script setup lang="ts">
import type { NavigationMenuItem } from '@nuxt/ui'

const links = [[{
  label: '공지사항',
  icon: 'i-lucide-megaphone',
  to: '/operation/notices',
  exact: true
}, {
  label: '1:1 문의',
  icon: 'i-lucide-message-circle-question',
  to: '/operation/inquiries'
}, {
  label: '신고/제재',
  icon: 'i-lucide-siren',
  to: '/operation/reports'
}, {
  label: '리뷰 관리',
  icon: 'i-lucide-star-half',
  to: '/operation/reviews'
}, {
  label: 'FAQ 관리',
  icon: 'i-lucide-help-circle',
  to: '/operation/faqs'
}]] satisfies NavigationMenuItem[][]
</script>

<template>
  <UDashboardPanel id="operation">
    <template #header>
      <UDashboardNavbar title="서비스 운영">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>

      <UDashboardToolbar>
        <UNavigationMenu :items="links" highlight class="-mx-1 flex-1" />
      </UDashboardToolbar>
    </template>

    <template #body>
      <div class="flex flex-col h-full w-full">
        <NuxtPage />
      </div>
    </template>
  </UDashboardPanel>
</template>