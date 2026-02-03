<script setup lang="ts">
import * as z from 'zod'
import type { FormSubmitEvent} from '@nuxt/ui'

const toast = useToast()

// 1. 레이아웃 설정
definePageMeta({
  layout: 'auth'
})


const providers = [{
  label: 'Google',
  icon: 'i-simple-icons-google',
  onClick: () => {
    toast.add({ title: 'Google', description: 'Login with Google' })
  }
}]

const schema = z.object({
  email: z.email('Invalid email'),
  password: z.string('Password is required').min(8, 'Must be at least 8 characters')
})

type Schema = z.output<typeof schema>

function onSubmit(payload: FormSubmitEvent<Schema>) {
  console.log('Submitted', payload)
}
</script>

<template>
  <UCard class="w-full">
    <template #header>
      <div class="text-center">
        <h1 class="text-xl font-bold text-gray-900 dark:text-white">로그인</h1>
        <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
          계정에 로그인하여 대시보드에 접속하세요.
        </p>
      </div>
    </template>

    <UForm :schema="schema" class="space-y-4" @submit="onSubmit">
        <UPageCard class="w-full max-w-md">
        <UAuthForm
            :schema="schema"
            title="Login"
            description="Enter your credentials to access your account."
            icon="i-lucide-user"
            :providers="providers"
            @submit="onSubmit"
        />
        </UPageCard>
    </UForm>
  </UCard>
</template>