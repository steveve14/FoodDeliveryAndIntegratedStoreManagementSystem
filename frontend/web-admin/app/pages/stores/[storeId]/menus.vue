<script setup lang="ts">
import * as z from "zod";
import type { FormSubmitEvent } from "@nuxt/ui";
import type { MenuDto, CreateMenuRequest } from "~/types/api";
import { useMenuApi } from "~/composables/api/useMenuApi";
import { useStoreApi } from "~/composables/api/useStoreApi";

const UBadge = resolveComponent("UBadge");
const UButton = resolveComponent("UButton");

const route = useRoute();
const toast = useToast();
const { getMenus, createMenu, updateMenu, deleteMenu } = useMenuApi();
const { getStore } = useStoreApi();

const storeId = computed(() => route.params.storeId as string);

// ── 상태 ──────────────────────────────────────────────────
const menus = ref<MenuDto[]>([]);
const storeName = ref("");
const loading = ref(true);
const modalOpen = ref(false);
const editingMenu = ref<MenuDto | null>(null);
const saving = ref(false);
const deleting = ref<string | null>(null);

// ── 데이터 로드 ────────────────────────────────────────────
async function loadData() {
  loading.value = true;
  try {
    const [storeRes, menuRes] = await Promise.all([
      getStore(storeId.value),
      getMenus(storeId.value),
    ]);
    if (storeRes.success) storeName.value = storeRes.data.name;
    if (menuRes.success) menus.value = menuRes.data;
  } catch {
    toast.add({
      title: "오류",
      description: "데이터 로드에 실패했습니다.",
      color: "error",
    });
  } finally {
    loading.value = false;
  }
}
await loadData();

// ── 메뉴 폼 ───────────────────────────────────────────────
const menuSchema = z.object({
  name: z.string().min(1, "메뉴명을 입력해주세요"),
  description: z.string().optional().default(""),
  price: z
    .number({ message: "정확한 금액을 입력해주세요" })
    .min(0, "0원 이상이어야 합니다"),
  available: z.boolean().default(true),
});
type MenuSchema = z.output<typeof menuSchema>;

const formState = reactive<Partial<MenuSchema>>({
  name: "",
  description: "",
  price: 0,
  available: true,
});

function openCreate() {
  editingMenu.value = null;
  Object.assign(formState, {
    name: "",
    description: "",
    price: 0,
    available: true,
  });
  modalOpen.value = true;
}

function openEdit(menu: MenuDto) {
  editingMenu.value = menu;
  Object.assign(formState, {
    name: menu.name,
    description: menu.description,
    price: menu.price,
    available: menu.available,
  });
  modalOpen.value = true;
}

async function onSubmit(e: FormSubmitEvent<MenuSchema>) {
  saving.value = true;
  try {
    const body: CreateMenuRequest = {
      name: e.data.name,
      description: e.data.description,
      price: e.data.price,
      available: e.data.available,
    };

    if (editingMenu.value) {
      const res = await updateMenu(storeId.value, editingMenu.value.id, body);
      if (res.success) {
        const idx = menus.value.findIndex(
          (m) => m.id === editingMenu.value!.id,
        );
        if (idx !== -1) menus.value[idx] = res.data;
        toast.add({
          title: "수정 완료",
          description: `"${res.data.name}" 메뉴가 수정되었습니다.`,
          color: "success",
        });
      }
    } else {
      const res = await createMenu(storeId.value, body);
      if (res.success) {
        menus.value.push(res.data);
        toast.add({
          title: "등록 완료",
          description: `"${res.data.name}" 메뉴가 추가되었습니다.`,
          color: "success",
        });
      }
    }
    modalOpen.value = false;
  } catch {
    toast.add({
      title: "오류",
      description: "저장에 실패했습니다.",
      color: "error",
    });
  } finally {
    saving.value = false;
  }
}

async function onDelete(menu: MenuDto) {
  if (!confirm(`"${menu.name}" 메뉴를 삭제하시겠습니까?`)) return;
  deleting.value = menu.id;
  try {
    await deleteMenu(storeId.value, menu.id);
    menus.value = menus.value.filter((m) => m.id !== menu.id);
    toast.add({
      title: "삭제 완료",
      description: `"${menu.name}" 메뉴가 삭제되었습니다.`,
      color: "success",
    });
  } catch {
    toast.add({
      title: "오류",
      description: "삭제에 실패했습니다.",
      color: "error",
    });
  } finally {
    deleting.value = null;
  }
}

const currencyFmt = new Intl.NumberFormat("ko-KR", {
  style: "currency",
  currency: "KRW",
});
</script>

<template>
  <UDashboardPanel id="store-menus">
    <template #header>
      <UDashboardNavbar
        :title="storeName ? `${storeName} — 메뉴 관리` : '메뉴 관리'"
      >
        <template #leading>
          <UButton
            icon="i-lucide-arrow-left"
            color="neutral"
            variant="ghost"
            to="/stores"
          />
        </template>
        <template #right>
          <UButton label="메뉴 추가" icon="i-lucide-plus" @click="openCreate" />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="p-4">
        <!-- 로딩 -->
        <div v-if="loading" class="flex justify-center py-16">
          <UIcon
            name="i-lucide-loader-circle"
            class="text-4xl animate-spin text-muted"
          />
        </div>

        <!-- 메뉴 없음 -->
        <div
          v-else-if="menus.length === 0"
          class="text-center text-muted text-sm py-16"
        >
          <UIcon name="i-lucide-utensils" class="text-4xl mb-3 block mx-auto" />
          등록된 메뉴가 없습니다.
          <UButton label="첫 메뉴 추가" class="mt-4" @click="openCreate" />
        </div>

        <!-- 메뉴 그리드 -->
        <div
          v-else
          class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4"
        >
          <UCard v-for="menu in menus" :key="menu.id" class="flex flex-col">
            <template #header>
              <div class="flex items-start justify-between gap-2">
                <div class="flex-1 min-w-0">
                  <p class="font-semibold truncate">{{ menu.name }}</p>
                  <p class="text-xs text-muted mt-0.5 line-clamp-2">
                    {{ menu.description || "설명 없음" }}
                  </p>
                </div>
                <UBadge
                  :color="menu.available ? 'success' : 'neutral'"
                  variant="subtle"
                  class="shrink-0"
                >
                  {{ menu.available ? "판매중" : "판매중지" }}
                </UBadge>
              </div>
            </template>

            <div class="flex items-center justify-between">
              <p class="text-primary font-bold text-lg">
                {{ currencyFmt.format(menu.price) }}
              </p>
              <div class="flex gap-1">
                <UButton
                  icon="i-lucide-pencil"
                  color="neutral"
                  variant="ghost"
                  size="sm"
                  @click="openEdit(menu)"
                />
                <UButton
                  icon="i-lucide-trash"
                  color="error"
                  variant="ghost"
                  size="sm"
                  :loading="deleting === menu.id"
                  @click="onDelete(menu)"
                />
              </div>
            </div>
          </UCard>
        </div>
      </div>

      <!-- 메뉴 등록/수정 모달 -->
      <UModal
        v-model:open="modalOpen"
        :title="editingMenu ? '메뉴 수정' : '메뉴 추가'"
        :description="
          editingMenu ? '메뉴 정보를 수정합니다.' : '새 메뉴를 추가합니다.'
        "
      >
        <template #body>
          <UForm
            :schema="menuSchema"
            :state="formState"
            class="space-y-4"
            @submit="onSubmit"
          >
            <UFormField label="메뉴명" name="name" required>
              <UInput
                v-model="formState.name"
                placeholder="후라이드 치킨"
                class="w-full"
              />
            </UFormField>

            <UFormField label="설명" name="description">
              <UTextarea
                v-model="formState.description"
                placeholder="바삭바삭한 후라이드"
                :rows="2"
                class="w-full"
              />
            </UFormField>

            <UFormField label="가격 (원)" name="price" required>
              <UInput
                v-model.number="formState.price"
                type="number"
                min="0"
                step="500"
                placeholder="18000"
                class="w-full"
              />
            </UFormField>

            <UFormField label="판매 상태" name="available">
              <USwitch
                v-model="formState.available"
                :label="formState.available ? '판매중' : '판매중지'"
              />
            </UFormField>

            <div class="flex justify-end gap-2 pt-2">
              <UButton
                label="취소"
                color="neutral"
                variant="subtle"
                @click="modalOpen = false"
              />
              <UButton
                type="submit"
                :label="editingMenu ? '수정' : '추가'"
                :loading="saving"
              />
            </div>
          </UForm>
        </template>
      </UModal>
    </template>
  </UDashboardPanel>
</template>
