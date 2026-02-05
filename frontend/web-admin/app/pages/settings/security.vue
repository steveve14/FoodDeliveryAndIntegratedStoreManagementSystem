<script setup lang="ts">
import * as z from 'zod'
import type { FormError } from '@nuxt/ui'

const passwordSchema = z.object({
  current: z.string().min(8, '최소 8자 이상이어야 합니다'),
  new: z.string().min(8, '최소 8자 이상이어야 합니다')
})

type PasswordSchema = z.output<typeof passwordSchema>

const password = reactive<Partial<PasswordSchema>>({
  current: undefined,
  new: undefined
})

const validate = (state: Partial<PasswordSchema>): FormError[] => {
  const errors: FormError[] = []
  if (state.current && state.new && state.current === state.new) {
    errors.push({ name: 'new', message: '새 비밀번호는 현재 비밀번호와 달라야 합니다' })
  }
  return errors
}
</script>

<template>
  <UPageCard
    title="비밀번호"
    description="새 비밀번호를 설정하기 전에 현재 비밀번호를 확인해 주세요."
    variant="subtle"
  >
    <UForm
      :schema="passwordSchema"
      :state="password"
      :validate="validate"
      class="flex flex-col gap-4 max-w-xs"
    >
      <UFormField name="current">
        <UInput
          v-model="password.current"
          type="password"
          placeholder="현재 비밀번호"
          class="w-full"
        />
      </UFormField>

      <UFormField name="new">
        <UInput
          v-model="password.new"
          type="password"
          placeholder="새 비밀번호"
          class="w-full"
        />
      </UFormField>

      <UButton label="변경하기" class="w-fit" type="submit" />
    </UForm>
  </UPageCard>

  <UPageCard
    title="계정"
    description="더 이상 서비스를 이용하지 않으시나요? 여기서 계정을 삭제할 수 있습니다. 이 작업은 되돌릴 수 없으며, 계정과 관련된 모든 정보가 영구적으로 삭제됩니다."
    class="bg-linear-to-tl from-error/10 from-5% to-default"
  >
    <template #footer>
      <UButton label="계정 삭제" color="error" />
    </template>
  </UPageCard>
</template>