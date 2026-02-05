<script setup lang="ts">
import * as z from 'zod'
import type { FormSubmitEvent } from '@nuxt/ui'

const fileRef = ref<HTMLInputElement>()

// 1. 유효성 검사 메시지 한글화
const profileSchema = z.object({
  name: z.string().min(2, '이름은 최소 2글자 이상이어야 합니다.'),
  email: z.string().email('유효하지 않은 이메일 형식입니다.'),
  username: z.string().min(2, '사용자명은 최소 2글자 이상이어야 합니다.'),
  avatar: z.string().optional(),
  bio: z.string().optional()
})

type ProfileSchema = z.output<typeof profileSchema>

const profile = reactive<Partial<ProfileSchema>>({
  name: 'Benjamin Canac',
  email: 'ben@nuxtlabs.com',
  username: 'benjamincanac',
  avatar: undefined,
  bio: undefined
})

const toast = useToast()

async function onSubmit(event: FormSubmitEvent<ProfileSchema>) {
  // 2. 토스트 알림 메시지 한글화
  toast.add({
    title: '저장 완료',
    description: '프로필 설정이 성공적으로 업데이트되었습니다.',
    icon: 'i-lucide-check',
    color: 'success'
  })
  console.log(event.data)
}

function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement

  if (!input.files?.length) {
    return
  }

  profile.avatar = URL.createObjectURL(input.files[0]!)
}

function onFileClick() {
  fileRef.value?.click()
}
</script>

<template>
  <UForm
    id="settings"
    :schema="profileSchema"
    :state="profile"
    @submit="onSubmit"
  >
    <UPageCard
      title="프로필"
      description="이 정보는 다른 사용자에게 공개적으로 표시됩니다."
      variant="naked"
      orientation="horizontal"
      class="mb-4"
    >
      <UButton
        form="settings"
        label="변경 사항 저장"
        color="neutral"
        type="submit"
        class="w-fit lg:ms-auto"
      />
    </UPageCard>

    <UPageCard variant="subtle">
      <UFormField
        name="name"
        label="이름"
        description="영수증, 청구서 및 기타 안내 메일에 표시될 이름입니다."
        required
        class="flex max-sm:flex-col justify-between items-start gap-4"
      >
        <UInput
          v-model="profile.name"
          autocomplete="off"
        />
      </UFormField>
      <USeparator />
      
      <UFormField
        name="email"
        label="이메일"
        description="로그인, 영수증 수신 및 제품 업데이트 안내에 사용됩니다."
        required
        class="flex max-sm:flex-col justify-between items-start gap-4"
      >
        <UInput
          v-model="profile.email"
          type="email"
          autocomplete="off"
        />
      </UFormField>
      <USeparator />
      
      <UFormField
        name="username"
        label="사용자명(ID)"
        description="로그인 및 프로필 URL에 사용되는 고유한 아이디입니다."
        required
        class="flex max-sm:flex-col justify-between items-start gap-4"
      >
        <UInput
          v-model="profile.username"
          type="username"
          autocomplete="off"
        />
      </UFormField>
      <USeparator />
      
      <UFormField
        name="avatar"
        label="프로필 사진"
        description="JPG, GIF 또는 PNG 형식. 최대 1MB."
        class="flex max-sm:flex-col justify-between sm:items-center gap-4"
      >
        <div class="flex flex-wrap items-center gap-3">
          <UAvatar
            :src="profile.avatar"
            :alt="profile.name"
            size="lg"
          />
          <UButton
            label="파일 선택"
            color="neutral"
            @click="onFileClick"
          />
          <input
            ref="fileRef"
            type="file"
            class="hidden"
            accept=".jpg, .jpeg, .png, .gif"
            @change="onFileChange"
          >
        </div>
      </UFormField>
      <USeparator />
      
      <UFormField
        name="bio"
        label="자기소개"
        description="프로필에 표시될 간략한 소개글입니다. URL은 자동으로 링크됩니다."
        class="flex max-sm:flex-col justify-between items-start gap-4"
        :ui="{ container: 'w-full' }"
      >
        <UTextarea
          v-model="profile.bio"
          :rows="5"
          autoresize
          class="w-full"
        />
      </UFormField>
    </UPageCard>
  </UForm>
</template>