<script setup lang="ts">
const toast = useToast();
const { user: sessionUser } = useAuth();
const { $api } = useApi();

interface UserProfileDto {
  id: string;
  email: string;
  name: string;
  username?: string | null;
  phone?: string | null;
  avatarUrl?: string | null;
  location?: string | null;
}

const profile = ref({
  name: sessionUser.value?.name || '',
  email: sessionUser.value?.email || '',
  username: '',
  phone: '',
  location: '',
  avatarUrl: '',
});

const isLoadingProfile = ref(false);
const isSaving = ref(false);
const isUploadingAvatar = ref(false);

const avatarPreview = computed(() => profile.value.avatarUrl || undefined);

const notifications = ref({
  orderStatus: true,
  promotions: false,
  reviews: true,
});

async function loadProfile (): Promise<void> {
  if (!sessionUser.value?.id) {
    return;
  }

  isLoadingProfile.value = true;
  try {
    const response = await $api<UserProfileDto>(`/api/v1/users/${sessionUser.value.id}/profile`);
    const data = response.data;
    profile.value = {
      name: data?.name || sessionUser.value.name || '',
      email: data?.email || sessionUser.value.email || '',
      username: data?.username || '',
      phone: data?.phone || '',
      location: data?.location || '',
      avatarUrl: data?.avatarUrl || '',
    };
  } catch {
    toast.add({
      title: '프로필을 불러오지 못했습니다.',
      icon: 'i-lucide-alert-circle',
      color: 'error',
    });
  } finally {
    isLoadingProfile.value = false;
  }
}

watch(
  () => sessionUser.value?.id,
  () => {
    loadProfile();
  },
  { immediate: true },
);

const saveSettings = async () => {
  if (!sessionUser.value?.id) {
    toast.add({
      title: '로그인이 필요합니다.',
      icon: 'i-lucide-alert-circle',
      color: 'error',
    });
    return;
  }

  isSaving.value = true;
  try {
    await $api<UserProfileDto>(`/api/v1/users/${sessionUser.value.id}/profile`, {
      method: 'PUT',
      body: {
        name: profile.value.name,
        username: profile.value.username || null,
        phone: profile.value.phone || null,
        avatarUrl: profile.value.avatarUrl || null,
        location: profile.value.location || null,
      },
    });

    if (sessionUser.value) {
      sessionUser.value = {
        ...sessionUser.value,
        name: profile.value.name,
        email: profile.value.email,
      };
    }

    toast.add({
      title: '설정이 저장되었습니다.',
      icon: 'i-lucide-check-circle',
      color: 'success',
    });
  } catch {
    toast.add({
      title: '설정 저장에 실패했습니다.',
      icon: 'i-lucide-alert-circle',
      color: 'error',
    });
  } finally {
    isSaving.value = false;
  }
};

const onAvatarSelected = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file || !sessionUser.value?.id) {
    return;
  }

  isUploadingAvatar.value = true;
  try {
    const formData = new FormData();
    formData.append('file', file);

    const response = await $api<UserProfileDto>(`/api/v1/users/${sessionUser.value.id}/avatar`, {
      method: 'POST',
      body: formData,
    });

    profile.value.avatarUrl = response.data?.avatarUrl || '';
    toast.add({
      title: '프로필 이미지가 업데이트되었습니다.',
      icon: 'i-lucide-image-up',
      color: 'success',
    });
  } catch {
    toast.add({
      title: '이미지 업로드에 실패했습니다.',
      icon: 'i-lucide-alert-circle',
      color: 'error',
    });
  } finally {
    target.value = '';
    isUploadingAvatar.value = false;
  }
};
</script>

<template>
  <UDashboardPanel id="settings" :ui="{ body: 'lg:py-12' }">
    <template #header>
      <UDashboardNavbar title="내 정보 설정">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-8 w-full lg:max-w-2xl mx-auto p-4">
        <!-- 프로필 정보 -->
        <div class="rounded-lg border border-gray-200 dark:border-gray-700 p-6">
          <h3 class="text-base font-semibold mb-4">프로필 정보</h3>
          <div class="flex flex-col gap-4" :class="{ 'opacity-70 pointer-events-none': isLoadingProfile }">
            <div class="flex flex-col gap-2">
              <span class="text-sm text-gray-600 dark:text-gray-300">프로필 이미지</span>
              <div class="flex items-center gap-4">
                <UAvatar :src="avatarPreview" :alt="profile.name || '사용자'" size="xl" />
                <label class="inline-flex">
                  <input
                    type="file"
                    class="hidden"
                    accept="image/*"
                    :disabled="isUploadingAvatar"
                    @change="onAvatarSelected"
                  >
                  <UButton
                    as="span"
                    :loading="isUploadingAvatar"
                    label="이미지 업로드"
                    icon="i-lucide-upload"
                    color="neutral"
                    variant="soft"
                  />
                </label>
              </div>
            </div>
            <UInput v-model="profile.name" placeholder="이름" />
            <UInput v-model="profile.email" placeholder="이메일" type="email" disabled />
            <UInput v-model="profile.username" placeholder="닉네임" />
            <UInput v-model="profile.phone" placeholder="전화번호" />
            <UInput v-model="profile.location" placeholder="기본 배달 주소" />
            <UInput v-model="profile.avatarUrl" placeholder="프로필 이미지 URL" />
          </div>
        </div>

        <!-- 알림 설정 -->
        <div class="rounded-lg border border-gray-200 dark:border-gray-700 p-6">
          <h3 class="text-base font-semibold mb-4">알림 설정</h3>
          <div class="flex flex-col gap-4">
            <div class="flex items-center justify-between">
              <span class="text-sm">주문 상태 알림</span>
              <USwitch v-model="notifications.orderStatus" />
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm">프로모션 알림</span>
              <USwitch v-model="notifications.promotions" />
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm">리뷰 답변 알림</span>
              <USwitch v-model="notifications.reviews" />
            </div>
          </div>
        </div>

        <div class="flex justify-end">
          <UButton label="저장" color="primary" :loading="isSaving" @click="saveSettings" />
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
