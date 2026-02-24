<script setup lang="ts">
import * as z from "zod";
import type { FormSubmitEvent, AuthFormField } from "#ui/types";
const { loginWithGoogle } = useAuth(); // composable 가져오기
const toast = useToast();

definePageMeta({
  layout: "auth",
});

async function onSubmit(payload: FormSubmitEvent<Schema>) {
  try {
    // 실제 로그인 로직 호출
    await $fetch("/api/auth/login", {
      method: "POST",
      body: payload.data,
    });

    toast.add({
      title: "성공",
      description: "로그인되었습니다.",
      color: "success",
    });
  } catch (error) {
    toast.add({
      title: "오류",
      description: "로그인에 실패했습니다.",
      color: "error",
    });
  }
}

const fields: AuthFormField[] = [
  {
    name: "email",
    type: "email",
    label: "이메일",
    placeholder: "이메일을 입력해주세요",
    required: true,
  },
  {
    name: "password",
    label: "비밀번호",
    type: "password",
    placeholder: "비밀번호를 입력해주세요",
    required: true,
  },
  {
    name: "remember",
    label: "로그인 상태 유지",
    type: "checkbox",
  },
];

const schema = z.object({
  email: z.string().email("유효하지 않은 이메일입니다"),
  password: z.string().min(8, "비밀번호는 최소 8자 이상이어야 합니다"),
});

type Schema = z.output<typeof schema>;
</script>

<template>
  <div class="flex flex-col items-center justify-center gap-4 p-4">
    <UPageCard class="w-full max-w-md">
      <UAuthForm
        :schema="schema"
        title="로그인"
        description="서비스를 이용하려면 로그인이 필요합니다."
        icon="i-lucide-user"
        :fields="fields"
        @submit="onSubmit"
      >
        <template #footer>
          <div class="flex flex-col gap-4">
            <UDivider label="또는" />
            <UButton
              color="info"
              label="구글로 로그인"
              icon="i-simple-icons-google"
              block
              @click="loginWithGoogle"
            />
          </div>
        </template>
      </UAuthForm>
    </UPageCard>
  </div>
</template>
