<script setup lang="ts">
import * as z from "zod";
import type { FormSubmitEvent } from "@nuxt/ui";

const schema = z.object({
  name: z.string().min(2, "이름은 최소 2글자 이상이어야 합니다."),
  email: z.string().email("유효하지 않은 이메일 형식입니다."),
  password: z.string().min(8, "비밀번호는 최소 8자 이상이어야 합니다."),
});
const open = ref(false);
const loading = ref(false);

type Schema = z.output<typeof schema>;

const state = reactive<Partial<Schema>>({
  name: undefined,
  email: undefined,
  password: undefined,
});

const toast = useToast();
const { register } = useUserApi();

async function onSubmit(event: FormSubmitEvent<Schema>) {
  loading.value = true;
  try {
    const res = await register({
      name: event.data.name,
      email: event.data.email,
      password: event.data.password,
    });
    if (res.success) {
      toast.add({
        title: "등록 성공",
        description: `${res.data.name} 님이 등록되었습니다.`,
        color: "success",
      });
      open.value = false;
      Object.assign(state, {
        name: undefined,
        email: undefined,
        password: undefined,
      });
    } else {
      toast.add({ title: "등록 실패", color: "error" });
    }
  } catch (e: any) {
    toast.add({
      title: "오류",
      description: e?.message || "등록에 실패했습니다.",
      color: "error",
    });
  } finally {
    loading.value = false;
  }
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

        <UFormField label="비밀번호" placeholder="최소 8자" name="password">
          <UInput v-model="state.password" type="password" class="w-full" />
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
            :loading="loading"
          />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
