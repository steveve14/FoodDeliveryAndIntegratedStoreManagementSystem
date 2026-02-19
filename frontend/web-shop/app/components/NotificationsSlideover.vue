<script setup lang="ts">
import { formatTimeAgo } from "@vueuse/core";
import type { UseTimeAgoMessages } from "@vueuse/core"; // 타입 임포트 (선택사항)
import type { Notification } from "~/types";

const { isNotificationsSlideoverOpen } = useDashboard();

const { data: notifications } =
  await useFetch<Notification[]>("/api/notifications");

// 한국어 변환 설정
const koMessages: UseTimeAgoMessages = {
  justNow: "방금 전",
  past: (n) => (n.match(/\d/) ? `${n} 전` : n),
  future: (n) => (n.match(/\d/) ? `${n} 후` : n),
  month: (n) => `${n}개월`,
  year: (n) => `${n}년`,
  day: (n) => `${n}일`,
  week: (n) => `${n}주`,
  hour: (n) => `${n}시간`,
  minute: (n) => `${n}분`,
  second: (n) => `${n}초`,
  invalid: "유효하지 않은 날짜",
};

// 래퍼 함수 생성
function formatTimeKr(dateString: string) {
  return formatTimeAgo(new Date(dateString), { messages: koMessages });
}
</script>

<template>
  <USlideover v-model:open="isNotificationsSlideoverOpen" title="알림">
    <template #body>
      <NuxtLink
        v-for="notification in notifications"
        :key="notification.id"
        :to="`/inbox?id=${notification.id}`"
        class="px-3 py-2.5 rounded-md hover:bg-elevated/50 flex items-center gap-3 relative -mx-3 first:-mt-3 last:-mb-3"
      >
        <UChip color="error" :show="!!notification.unread" inset>
          <UAvatar
            v-bind="notification.sender.avatar"
            :alt="notification.sender.name"
            size="md"
          />
        </UChip>

        <div class="text-sm flex-1">
          <p class="flex items-center justify-between">
            <span class="text-highlighted font-medium">{{
              notification.sender.name
            }}</span>

            <time
              :datetime="notification.date"
              class="text-muted text-xs"
              v-text="formatTimeKr(notification.date)"
            />
          </p>

          <p class="text-dimmed">
            {{ notification.body }}
          </p>
        </div>
      </NuxtLink>
    </template>
  </USlideover>
</template>
