<script setup lang="ts">
import * as z from 'zod';
import type { FormSubmitEvent } from '@nuxt/ui';
import type { EventDto } from '~/types/api';
import { useEventApi } from '~/composables/api/useEventApi';

const toast = useToast();
const { createEvent, getEvent } = useEventApi();

// ── 상태 ──────────────────────────────────────────────────
const activeTab = ref('publish');
const publishLoading = ref(false);
const searchLoading = ref(false);
const searchId = ref('');
const foundEvent = ref<EventDto | null>(null);
const searchError = ref<string | null>(null);
const publishedEvent = ref<EventDto | null>(null);

// ── 이벤트 발행 폼 ─────────────────────────────────────────
const publishSchema = z.object({
  type: z.string().min(1, '이벤트 타입을 입력해주세요'),
  payload: z.string().optional().default(''),
});
type PublishSchema = z.output<typeof publishSchema>;

const publishState = reactive<Partial<PublishSchema>>({
  type: '',
  payload: '',
});

const EVENT_TYPES = [
  'ORDER_CREATED',
  'ORDER_CONFIRMED',
  'ORDER_CANCELLED',
  'DELIVERY_STARTED',
  'DELIVERY_COMPLETED',
  'USER_REGISTERED',
  'STORE_CREATED',
];

async function onPublish (e: FormSubmitEvent<PublishSchema>) {
  publishLoading.value = true;
  publishedEvent.value = null;
  try {
    const res = await createEvent({
      type: e.data.type,
      payload: e.data.payload || '',
    });
    if (res.success) {
      publishedEvent.value = res.data;
      toast.add({
        title: '이벤트 발행 완료',
        description: `타입: ${res.data.type}`,
        color: 'success',
      });
      publishState.type = '';
      publishState.payload = '';
    } else {
      toast.add({ title: '발행 실패', color: 'error' });
    }
  } catch {
    toast.add({
      title: '오류',
      description: '이벤트 발행 중 오류가 발생했습니다.',
      color: 'error',
    });
  } finally {
    publishLoading.value = false;
  }
}

// ── 이벤트 조회 폼 ─────────────────────────────────────────
const searchSchema = z.object({
  eventId: z.string().min(1, '이벤트 ID를 입력해주세요'),
});

async function onSearch (e: FormSubmitEvent<{ eventId: string }>) {
  searchError.value = null;
  foundEvent.value = null;
  searchLoading.value = true;
  try {
    const res = await getEvent(e.data.eventId);
    if (res.success) {
      foundEvent.value = res.data;
    } else {
      searchError.value = '이벤트를 찾을 수 없습니다.';
    }
  } catch {
    searchError.value = '이벤트 조회 중 오류가 발생했습니다.';
  } finally {
    searchLoading.value = false;
  }
}
</script>

<template>
  <UDashboardPanel id="events">
    <template #header>
      <UDashboardNavbar title="이벤트 관리">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="max-w-2xl mx-auto p-4">
        <UTabs
          v-model="activeTab"
          :items="[
            { label: '이벤트 발행', value: 'publish', icon: 'i-lucide-send' },
            { label: '이벤트 조회', value: 'search', icon: 'i-lucide-search' },
          ]"
          class="mb-6"
        />

        <!-- ── 이벤트 발행 탭 ─────────────────────────────── -->
        <div v-if="activeTab === 'publish'" class="space-y-6">
          <UCard>
            <template #header>
              <div class="flex items-center gap-2">
                <UIcon name="i-lucide-zap" class="text-primary" />
                <span class="font-semibold">새 이벤트 발행</span>
              </div>
            </template>

            <UForm
              :schema="publishSchema"
              :state="publishState"
              class="space-y-4"
              @submit="onPublish"
            >
              <UFormField label="이벤트 타입" name="type" required>
                <div class="space-y-2 w-full">
                  <UInput
                    v-model="publishState.type"
                    placeholder="예: ORDER_CREATED"
                    class="w-full"
                  />
                  <div class="flex flex-wrap gap-1.5">
                    <UBadge
                      v-for="t in EVENT_TYPES"
                      :key="t"
                      variant="subtle"
                      color="neutral"
                      class="cursor-pointer hover:bg-elevated"
                      @click="publishState.type = t"
                    >
                      {{ t }}
                    </UBadge>
                  </div>
                </div>
              </UFormField>

              <UFormField label="페이로드 (JSON 직렬화 문자열)" name="payload">
                <UTextarea
                  v-model="publishState.payload"
                  placeholder="{&quot;orderId&quot;:&quot;uuid&quot;,&quot;amount&quot;:18000}"
                  :rows="4"
                  class="w-full font-mono text-sm"
                />
                <template #hint>
                  <span class="text-xs text-muted"
                    >payload는 직렬화된 JSON 문자열(string)로 저장됩니다.</span
                  >
                </template>
              </UFormField>

              <div class="flex justify-end">
                <UButton
                  type="submit"
                  label="이벤트 발행"
                  icon="i-lucide-send"
                  :loading="publishLoading"
                />
              </div>
            </UForm>
          </UCard>

          <!-- 발행 결과 -->
          <UCard v-if="publishedEvent" class="border-success/30 bg-success/5">
            <template #header>
              <div class="flex items-center gap-2 text-success">
                <UIcon name="i-lucide-check-circle" />
                <span class="font-semibold">발행 완료</span>
              </div>
            </template>
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div>
                <p class="text-muted mb-0.5">ID</p>
                <p class="font-mono text-xs">{{ publishedEvent.id }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">타입</p>
                <UBadge variant="subtle" color="primary">
{{
                  publishedEvent.type
                }}
</UBadge>
              </div>
              <div class="col-span-2">
                <p class="text-muted mb-0.5">페이로드</p>
                <pre class="bg-elevated rounded p-2 text-xs overflow-auto">{{
                  publishedEvent.payload
                }}</pre>
              </div>
              <div>
                <p class="text-muted mb-0.5">발행 시각</p>
                <p>
                  {{
                    new Date(publishedEvent.createdAt).toLocaleString("ko-KR")
                  }}
                </p>
              </div>
            </div>
          </UCard>
        </div>

        <!-- ── 이벤트 조회 탭 ─────────────────────────────── -->
        <div v-if="activeTab === 'search'" class="space-y-6">
          <UForm
            :schema="searchSchema"
            :state="{ eventId: searchId }"
            class="flex gap-2"
            @submit="onSearch"
          >
            <UFormField name="eventId" class="flex-1">
              <UInput
                v-model="searchId"
                placeholder="이벤트 ID를 입력하세요 (UUID)"
                icon="i-lucide-search"
                class="w-full"
              />
            </UFormField>
            <UButton
              type="submit"
              label="조회"
              :loading="searchLoading"
              icon="i-lucide-search"
            />
          </UForm>

          <UAlert
            v-if="searchError"
            color="error"
            variant="subtle"
            icon="i-lucide-alert-circle"
            :title="searchError"
          />

          <UCard v-if="foundEvent">
            <template #header>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <UIcon name="i-lucide-activity" />
                  <span class="font-semibold">이벤트 상세</span>
                </div>
                <UBadge variant="subtle" color="primary">
{{
                  foundEvent.type
                }}
</UBadge>
              </div>
            </template>
            <div class="space-y-3 text-sm">
              <div>
                <p class="text-muted mb-0.5">ID</p>
                <p class="font-mono text-xs">{{ foundEvent.id }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">페이로드</p>
                <pre class="bg-elevated rounded p-2 text-xs overflow-auto">{{
                  foundEvent.payload
                }}</pre>
              </div>
              <div>
                <p class="text-muted mb-0.5">발행 시각</p>
                <p>
                  {{ new Date(foundEvent.createdAt).toLocaleString("ko-KR") }}
                </p>
              </div>
            </div>
          </UCard>

          <div
            v-if="!foundEvent && !searchError && !searchLoading"
            class="text-center text-muted text-sm py-12"
          >
            <UIcon
              name="i-lucide-activity"
              class="text-4xl mb-3 block mx-auto"
            />
            이벤트 ID를 입력하여 발행된 이벤트를 조회하세요.
          </div>
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
