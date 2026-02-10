<script setup lang="ts">
import type { NavigationMenuItem } from "@nuxt/ui";

const links = [
  [
    {
      label: "결제/정산",
      icon: "i-lucide-receipt",
      to: "/finance/transactions",
    },
    {
      label: "쿠폰 관리",
      icon: "i-lucide-ticket",
      to: "/marketing/coupons",
    },
    {
      label: "배너/광고",
      to: "/marketing/banners",
      icon: "i-lucide-monitor-play",
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
