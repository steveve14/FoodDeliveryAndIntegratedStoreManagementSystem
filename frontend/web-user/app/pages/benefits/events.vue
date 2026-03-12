<script setup lang="ts">
interface EventItem {
  id: number;
  title: string;
  description: string;
  period: string;
  icon: string;
  isActive: boolean;
}

const events = ref<EventItem[]>([
  {
    id: 1,
    title: '신규 가입 웰컴 이벤트',
    description: '가입 후 첫 주문 시 배달비 무료!',
    period: '2026.03.01 ~ 2026.03.31',
    icon: 'i-lucide-party-popper',
    isActive: true,
  },
  {
    id: 2,
    title: '리뷰 쓰면 쿠폰 드려요',
    description: '포토 리뷰 작성 시 1,000원 쿠폰 즉시 지급',
    period: '2026.03.01 ~ 2026.04.30',
    icon: 'i-lucide-camera',
    isActive: true,
  },
  {
    id: 3,
    title: '주말 특별 할인',
    description: '매주 토/일 전 메뉴 10% 할인',
    period: '2026.02.01 ~ 2026.02.28',
    icon: 'i-lucide-percent',
    isActive: false,
  },
]);

const activeEvents = computed(() =>
  events.value.filter((e: EventItem) => e.isActive),
);
const endedEvents = computed(() =>
  events.value.filter((e: EventItem) => !e.isActive),
);
</script>

<template>
  <UDashboardPanel grow>
    <template #header>
      <UDashboardNavbar title="이벤트">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-6 p-4">
        <div>
          <h3 class="text-sm font-semibold text-gray-900 dark:text-white mb-3">
            진행 중인 이벤트
          </h3>
          <div class="flex flex-col gap-3">
            <div
              v-for="event in activeEvents"
              :key="event.id"
              class="rounded-lg border border-gray-200 dark:border-gray-700 p-4 shadow-sm"
            >
              <div class="flex items-start gap-3">
                <div
                  class="w-10 h-10 rounded-lg bg-primary-50 dark:bg-primary-900/30 flex items-center justify-center shrink-0"
                >
                  <UIcon :name="event.icon" class="w-5 h-5 text-primary-500" />
                </div>
                <div>
                  <div class="font-semibold text-sm">
                    {{ event.title }}
                  </div>
                  <p class="text-xs text-gray-500 mt-1">
                    {{ event.description }}
                  </p>
                  <span class="text-xs text-gray-400 mt-1 block">{{
                    event.period
                  }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="endedEvents.length > 0">
          <h3 class="text-sm font-semibold text-gray-500 mb-3">
            종료된 이벤트
          </h3>
          <div class="flex flex-col gap-3">
            <div
              v-for="event in endedEvents"
              :key="event.id"
              class="rounded-lg border border-gray-200 dark:border-gray-700 p-4 opacity-50"
            >
              <div class="flex items-start gap-3">
                <div
                  class="w-10 h-10 rounded-lg bg-gray-100 dark:bg-gray-800 flex items-center justify-center shrink-0"
                >
                  <UIcon :name="event.icon" class="w-5 h-5 text-gray-400" />
                </div>
                <div>
                  <div class="font-semibold text-sm">
                    {{ event.title }}
                  </div>
                  <p class="text-xs text-gray-500 mt-1">
                    {{ event.description }}
                  </p>
                  <span class="text-xs text-gray-400 mt-1 block">{{
                    event.period
                  }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
