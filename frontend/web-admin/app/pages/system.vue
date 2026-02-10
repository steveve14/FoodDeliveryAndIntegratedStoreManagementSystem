<script setup lang="ts">
import type { NavigationMenuItem } from "@nuxt/ui";

const links = [
  [
    {
      label: "에러 로그 관리",
      to: "/system/logs",
      icon: "i-lucide-alert-triangle",
    },
    {
      label: "관리자 활동",
      to: "/system/audit",
      icon: "i-lucide-file-clock",
    },
    {
      label: "앱 버전 관리",
      to: "/system/versions",
      icon: "i-lucide-smartphone",
    },
  ],
] satisfies NavigationMenuItem[][];
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
