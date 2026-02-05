<script setup lang="ts">
import * as z from 'zod'
import type { FormSubmitEvent } from '@nuxt/ui'

// 1. 유효성 검사 메시지 한글화
const schema = z.object({
  name: z.string().min(2, '이름은 최소 2글자 이상이어야 합니다.'),
  email: z.string().email('유효하지 않은 이메일 형식입니다.')
})
const open = ref(false)

type Schema = z.output<typeof schema>

const state = reactive<Partial<Schema>>({
  name: undefined,
  email: undefined
})

const toast = useToast()
async function onSubmit(event: FormSubmitEvent<Schema>) {
  // 2. 토스트 알림 메시지 한글화
  toast.add({ 
    title: '등록 성공', 
    description: `신규 고객 '${event.data.name}'님이 성공적으로 추가되었습니다.`, 
    color: 'success' 
  })
  open.value = false
}
</script>

<template>
  <UModal 
    v-model:open="open" 
    title="신규 고객 등록" 
    description="데이터베이스에 새로운 고객 정보를 추가합니다."
  >
    <UButton label="신규 등록" icon="i-lucide-plus" />

    <template #body>
      <UForm
        :schema="schema"
        :state="state"
        class="space-y-4"
        @submit="onSubmit"
      >
        <UFormField label="이름" placeholder="홍길동" name="name">
          <UInput v-model="state.name" class="w-full" />
        </UFormField>
        
        <UFormField label="이메일" placeholder="hong@example.com" name="email">
          <UInput v-model="state.email" class="w-full" />
        </UFormField>

        <div class="flex justify-end gap-2">
          <UButton
            label="취소"
            color="neutral"
            variant="subtle"
            @click="open = false"
          />
          <UButton
            label="등록"
            color="primary"
            variant="solid"
            type="submit"
          />
        </div>
      </UForm>
    </template>
  </UModal>
</template>