<script setup lang="ts">
import * as z from "zod";
import type { FormSubmitEvent } from "@nuxt/ui";
import type { UpdateProfileRequest } from "~/types/api";

const { user: authUser } = useAuth();
const { getProfile, updateProfile } = useUserApi();
const toast = useToast();

const profileSchema = z.object({
  name: z.string().min(2, "이름은 최소 2글자 이상이어야 합니다."),
  email: z.string().email("유효하지 않은 이메일 형식입니다."),
  username: z.string().min(2, "사용자명은 최소 2글자 이상이어야 합니다."),
  phone: z.string().optional(),
  avatarUrl: z.string().url("유효한 이미지 URL을 입력해 주세요.").or(z.literal("")),
  location: z.string().optional(),
});

type ProfileSchema = z.output<typeof profileSchema>;

const profile = reactive<Partial<ProfileSchema>>({
  name: "",
  email: "",
  username: "",
  phone: "",
  avatarUrl: "",
  location: "",
});

const isSubmitting = ref(false);

const { data: profileResponse, refresh: refreshProfile } = await useAsyncData(
  () => `admin-settings-profile-${authUser.value?.id ?? 'guest'}`,
  async () => {
    if (!authUser.value?.id) {
      return null;
    }

    const response = await getProfile(authUser.value.id);
    return response.data;
  },
  {
    watch: [() => authUser.value?.id],
    default: () => null,
  },
);

watchEffect(() => {
  const currentProfile = profileResponse.value;
  const authEmail = authUser.value?.email ?? "";

  profile.name = currentProfile?.name ?? authUser.value?.name ?? "";
  profile.email = currentProfile?.email ?? authEmail;
  profile.username = currentProfile?.username ?? (authEmail ? authEmail.split("@")[0] : "");
  profile.phone = currentProfile?.phone ?? "";
  profile.avatarUrl = currentProfile?.avatarUrl ?? "";
  profile.location = currentProfile?.location ?? "";
});

async function onSubmit(event: FormSubmitEvent<ProfileSchema>) {
  if (!authUser.value?.id) {
    return;
  }

  isSubmitting.value = true;

  try {
    await updateProfile(authUser.value.id, {
      name: event.data.name,
      username: event.data.username,
      phone: event.data.phone || undefined,
      avatarUrl: event.data.avatarUrl || undefined,
      location: event.data.location || undefined,
    } satisfies UpdateProfileRequest);

    await refreshProfile();

    toast.add({
      title: "저장 완료",
      description: "프로필 설정이 DB 기준으로 업데이트되었습니다.",
      icon: "i-lucide-check",
      color: "success",
    });
  } catch {
    toast.add({
      title: "저장 실패",
      description: "프로필 저장 중 오류가 발생했습니다.",
      icon: "i-lucide-circle-alert",
      color: "error",
    });
  } finally {
    isSubmitting.value = false;
  }
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
        :loading="isSubmitting"
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
        <UInput v-model="profile.name" autocomplete="off" />
      </UFormField>
      <USeparator />

      <UFormField
        name="email"
        label="이메일"
        description="로그인, 영수증 수신 및 제품 업데이트 안내에 사용됩니다."
        required
        class="flex max-sm:flex-col justify-between items-start gap-4"
      >
        <UInput v-model="profile.email" type="email" autocomplete="off" disabled />
      </UFormField>
      <USeparator />

      <UFormField
        name="username"
        label="사용자명(ID)"
        description="로그인 및 프로필 URL에 사용되는 고유한 아이디입니다."
        required
        class="flex max-sm:flex-col justify-between items-start gap-4"
      >
        <UInput v-model="profile.username" type="username" autocomplete="off" />
      </UFormField>
      <USeparator />

      <UFormField
        name="phone"
        label="연락처"
        description="관리자 연락 및 알림에 사용할 번호입니다."
        class="flex max-sm:flex-col justify-between items-start gap-4"
      >
        <UInput v-model="profile.phone" autocomplete="off" />
      </UFormField>
      <USeparator />

      <UFormField
        name="avatarUrl"
        label="아바타 URL"
        description="DB에 저장된 프로필 이미지 URL입니다."
        class="flex max-sm:flex-col justify-between items-start gap-4"
        :ui="{ container: 'w-full' }"
      >
        <div class="w-full space-y-3">
          <UInput v-model="profile.avatarUrl" autocomplete="off" class="w-full" />
          <UAvatar :src="profile.avatarUrl || undefined" :alt="profile.name" size="lg" />
        </div>
      </UFormField>
      <USeparator />

      <UFormField
        name="location"
        label="위치"
        description="운영자 프로필에 표시할 지역 또는 사무실 정보입니다."
        class="flex max-sm:flex-col justify-between items-start gap-4"
      >
        <UInput v-model="profile.location" autocomplete="off" />
      </UFormField>
    </UPageCard>
  </UForm>
</template>
