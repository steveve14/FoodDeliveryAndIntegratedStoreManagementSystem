<script setup lang="ts">
import { format } from 'date-fns'
import { ko } from 'date-fns/locale' // ★ 한국어 로케일 추가
import type { Mail } from '~/types'

defineProps<{
  mail: Mail
}>()

const emits = defineEmits(['close'])

// 1. 드롭다운 메뉴 한글화
const dropdownItems = [[{
  label: '읽지 않음으로 표시',
  icon: 'i-lucide-check-circle'
}, {
  label: '중요 메일로 표시',
  icon: 'i-lucide-triangle-alert'
}], [{
  label: '별표 표시',
  icon: 'i-lucide-star'
}, {
  label: '메일 숨기기',
  icon: 'i-lucide-circle-pause'
}]]

const toast = useToast()

const reply = ref('')
const loading = ref(false)

function onSubmit() {
  loading.value = true

  setTimeout(() => {
    reply.value = ''

    // 2. 전송 완료 메시지 한글화
    toast.add({
      title: '발송 완료',
      description: '메일이 성공적으로 발송되었습니다.',
      icon: 'i-lucide-check-circle',
      color: 'success'
    })

    loading.value = false
  }, 1000)
}
</script>

<template>
  <UDashboardPanel id="inbox-2">
    <UDashboardNavbar :title="mail.subject" :toggle="false">
      <template #leading>
        <UButton
          icon="i-lucide-x"
          color="neutral"
          variant="ghost"
          class="-ms-1.5"
          @click="emits('close')"
        />
      </template>

      <template #right>
        <UTooltip text="보관함으로 이동">
          <UButton
            icon="i-lucide-inbox"
            color="neutral"
            variant="ghost"
          />
        </UTooltip>

        <UTooltip text="답장하기">
          <UButton icon="i-lucide-reply" color="neutral" variant="ghost" />
        </UTooltip>

        <UDropdownMenu :items="dropdownItems">
          <UButton
            icon="i-lucide-ellipsis-vertical"
            color="neutral"
            variant="ghost"
          />
        </UDropdownMenu>
      </template>
    </UDashboardNavbar>

    <div class="flex flex-col sm:flex-row justify-between gap-1 p-4 sm:px-6 border-b border-default">
      <div class="flex items-start gap-4 sm:my-1.5">
        <UAvatar
          v-bind="mail.from.avatar"
          :alt="mail.from.name"
          size="3xl"
        />

        <div class="min-w-0">
          <p class="font-semibold text-highlighted">
            {{ mail.from.name }}
          </p>
          <p class="text-muted">
            {{ mail.from.email }}
          </p>
        </div>
      </div>

      <p class="max-sm:pl-16 text-muted text-sm sm:mt-2">
        {{ format(new Date(mail.date), 'M월 d일 HH:mm', { locale: ko }) }}
      </p>
    </div>

    <div class="flex-1 p-4 sm:p-6 overflow-y-auto">
      <p class="whitespace-pre-wrap">
        {{ mail.body }}
      </p>
    </div>

    <div class="pb-4 px-4 sm:px-6 shrink-0">
      <UCard variant="subtle" class="mt-auto" :ui="{ header: 'flex items-center gap-1.5 text-dimmed' }">
        <template #header>
          <UIcon name="i-lucide-reply" class="size-5" />

          <span class="text-sm truncate">
            {{ mail.from.name }} ({{ mail.from.email }})님에게 답장하기
          </span>
        </template>

        <form @submit.prevent="onSubmit">
          <UTextarea
            v-model="reply"
            color="neutral"
            variant="none"
            required
            autoresize
            placeholder="답장 내용을 입력하세요..."
            :rows="4"
            :disabled="loading"
            class="w-full"
            :ui="{ base: 'p-0 resize-none' }"
          />

          <div class="flex items-center justify-between">
            <UTooltip text="파일 첨부">
              <UButton
                color="neutral"
                variant="ghost"
                icon="i-lucide-paperclip"
              />
            </UTooltip>

            <div class="flex items-center justify-end gap-2">
              <UButton
                color="neutral"
                variant="ghost"
                label="임시 저장"
              />
              <UButton
                type="submit"
                color="neutral"
                :loading="loading"
                label="보내기"
                icon="i-lucide-send"
              />
            </div>
          </div>
        </form>
      </UCard>
    </div>
  </UDashboardPanel>
</template>