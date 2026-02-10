<script setup lang="ts">
import type { NavigationMenuItem } from "@nuxt/ui";
const route = useRoute();

const toast = useToast();
const open = ref(false);

const links = computed(
  () =>
    [
      [
        {
          label: "홈",
          icon: "i-lucide-house",
          to: "/",
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: "메시지함",
          icon: "i-lucide-inbox",
          to: "/inbox",
          badge: "4",
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: "고객 목록",
          icon: "i-lucide-user",
          to: "/customer",
          onSelect: () => {
            open.value = false;
          },
        },
        {
          label: "매장 관리",
          to: "/stores",
          icon: "i-lucide-store",
          onSelect: () => {
            open.value = false;
          },
        },
        {
          // 2. 서비스 운영
          id: "operation",
          label: "서비스 운영",
          icon: "i-lucide-headset",
          active: route.path.startsWith("/operation"),
          defaultOpen: route.path.startsWith("/operation"),
          children: [
            {
              label: "공지사항",
              to: "/operation/notices",
              icon: "i-lucide-megaphone",
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "1:1 문의",
              to: "/operation/inquiries",
              icon: "i-lucide-message-circle-question",
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "신고/제재",
              to: "/operation/reports",
              icon: "i-lucide-siren",
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "리뷰 관리",
              to: "/operation/reviews",
              icon: "i-lucide-star-half",
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "FAQ 관리",
              to: "/operation/faqs",
              icon: "i-lucide-help-circle",
              onSelect: () => {
                open.value = false;
              },
            },
          ],
        },
        {
          // 3. 마케팅 및 매출
          id: "marketing",
          label: "마케팅 및 매출",
          icon: "i-lucide-banknote",
          active:
            route.path.startsWith("/finance") ||
            route.path.startsWith("/marketing"),
          defaultOpen:
            route.path.startsWith("/finance") ||
            route.path.startsWith("/marketing"),
          children: [
            {
              label: "결제/정산",
              to: "/finance/transactions",
              icon: "i-lucide-receipt",
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "쿠폰 관리",
              to: "/marketing/coupons",
              icon: "i-lucide-ticket",
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "배너/광고",
              to: "/marketing/banners",
              icon: "i-lucide-monitor-play",
              onSelect: () => {
                open.value = false;
              },
            },
          ],
        },
        {
          // 4. 시스템 관리
          id: "system",
          label: "시스템 관리",
          icon: "i-lucide-settings",
          active: route.path.startsWith("/system"),
          defaultOpen: route.path.startsWith("/system"),
          children: [
            {
              label: "에러 로그 관리",
              to: "/system/logs",
              icon: "i-lucide-alert-triangle",
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "관리자 활동",
              to: "/system/audit",
              icon: "i-lucide-file-clock",
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "앱 버전 관리",
              to: "/system/versions",
              icon: "i-lucide-smartphone",
              onSelect: () => {
                open.value = false;
              },
            },
          ],
        },
        {
          // 5. 설정
          label: "설정",
          icon: "i-lucide-settings-2",
          active: route.path.startsWith("/settings"),
          defaultOpen: route.path.startsWith("/settings"),
          children: [
            {
              label: "프로필 설정",
              to: "/settings/profile",
              exact: true,
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "알람 설정",
              to: "/settings/notifications",
              onSelect: () => {
                open.value = false;
              },
            },
            {
              label: "보안 설정",
              to: "/settings/security",
              onSelect: () => {
                open.value = false;
              },
            },
          ],
        },
      ],
    ] satisfies NavigationMenuItem[][],
); // computed 내부이므로 끝에 괄호 주의

// computed인 links를 사용하므로 .value로 접근하거나 템플릿에서 그대로 사용
const groups = computed(() => [
  {
    id: "links",
    label: "빠른 이동",
    // links가 computed이므로 links.value로 접근
    items: links.value.flat(),
  },
  {
    id: "theme",
    label: "테마 설정",
    items: [],
  },
]);

onMounted(async () => {
  const cookie = useCookie("cookie-consent");
  if (cookie.value === "accepted") {
    return;
  }

  toast.add({
    title:
      "저희는 웹사이트 이용 경험을 향상시키기 위해 자사 쿠키를 사용합니다.",
    duration: 0,
    close: false,
    actions: [
      {
        label: "수락",
        color: "neutral",
        variant: "outline",
        onClick: () => {
          cookie.value = "accepted";
        },
      },
      {
        label: "거부",
        color: "neutral",
        variant: "ghost",
      },
    ],
  });
});
</script>

<template>
  <UDashboardGroup unit="rem">
    <UDashboardSidebar
      id="default"
      v-model:open="open"
      collapsible
      resizable
      class="bg-elevated/25"
      :ui="{ footer: 'lg:border-t lg:border-default' }"
    >
      <template #header="{ collapsed }">
        <TeamsMenu :collapsed="collapsed" />
      </template>

      <template #default="{ collapsed }">
        <UDashboardSearchButton
          :collapsed="collapsed"
          class="bg-transparent ring-default"
          label="검색..."
        />

        <UNavigationMenu
          :collapsed="collapsed"
          :items="links[0]"
          orientation="vertical"
          tooltip
          popover
        />

        <UNavigationMenu
          :collapsed="collapsed"
          :items="links[1]"
          orientation="vertical"
          tooltip
          class="mt-auto"
        />
      </template>

      <template #footer="{ collapsed }">
        <UserMenu :collapsed="collapsed" />
      </template>
    </UDashboardSidebar>

    <UDashboardSearch :groups="groups" placeholder="사용자, 매장, 주문 등..." />

    <slot />

    <NotificationsSlideover />
  </UDashboardGroup>
</template>
