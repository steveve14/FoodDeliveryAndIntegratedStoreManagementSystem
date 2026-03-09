<script setup lang="ts">
import * as z from 'zod';
import type { FormSubmitEvent, AuthFormField } from '#ui/types';

const { $api } = useApi();
const { user } = useAuth();
const toast = useToast();

definePageMeta({
  layout: 'auth',
});

const fields: AuthFormField[] = [
  {
    name: 'name',
    type: 'text',
    label: '이름',
    placeholder: '이름을 입력해주세요',
    required: true,
  },
  {
    name: 'email',
    type: 'email',
    label: '이메일',
    placeholder: '이메일을 입력해주세요',
    required: true,
  },
  {
    name: 'password',
    label: '비밀번호',
    type: 'password',
    placeholder: '비밀번호를 입력해주세요 (8자 이상)',
    required: true,
  },
  {
    name: 'passwordConfirm',
    label: '비밀번호 확인',
    type: 'password',
    placeholder: '비밀번호를 다시 입력해주세요',
    required: true,
  },
];

const schema = z
  .object({
    name: z.string().min(2, '이름은 최소 2자 이상이어야 합니다'),
    email: z.string().email('유효하지 않은 이메일입니다'),
    password: z.string().min(8, '비밀번호는 최소 8자 이상이어야 합니다'),
    passwordConfirm: z.string(),
  })
  .refine(
    (data: { password: string; passwordConfirm: string }) =>
      data.password === data.passwordConfirm,
    {
      message: '비밀번호가 일치하지 않습니다',
      path: ['passwordConfirm'],
    },
  );

type Schema = z.output<typeof schema>;

async function onSubmit (payload: FormSubmitEvent<Schema>) {
  try {
    const res = await $api<{
      id: string;
      email: string;
      name: string;
      role: string;
    }>('/api/v1/auth/signup', {
      method: 'POST',
      body: {
        name: payload.data.name,
        email: payload.data.email,
        password: payload.data.password,
      },
    });

    if (!res.success) {
      throw new Error(res.error || '회원가입에 실패했습니다.');
    }

    // 서버가 DB 저장 검증 후 쿠키(httpOnly)를 설정하므로 사용자 정보만 상태에 저장
    user.value = {
      id: res.data.id,
      email: res.data.email,
      name: res.data.name,
      role: res.data.role as import('~/composables/useAuth').UserRole,
    };

    toast.add({
      title: '회원가입 완료',
      description: '환영합니다! 자동으로 로그인되었습니다.',
      color: 'success',
    });
    await navigateTo('/');
  } catch (error: unknown) {
    const message =
      error instanceof Error ? error.message : '회원가입에 실패했습니다.';
    toast.add({
      title: '오류',
      description: message,
      color: 'error',
    });
  }
}
</script>

<template>
  <div class="flex flex-col items-center justify-center gap-4 p-4">
    <UPageCard class="w-full max-w-md">
      <UAuthForm
        :schema="schema"
        title="회원가입"
        description="계정을 만들고 맛있는 음식을 주문해보세요."
        icon="i-lucide-user-plus"
        :fields="fields"
        @submit="onSubmit"
      >
        <template #footer>
          <p class="text-sm text-center text-gray-500">
            이미 계정이 있으신가요?
            <NuxtLink
              to="/login"
              class="text-primary-500 font-medium hover:underline"
            >
              로그인
            </NuxtLink>
          </p>
        </template>
      </UAuthForm>
    </UPageCard>
  </div>
</template>
