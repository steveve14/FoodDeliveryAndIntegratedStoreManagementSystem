<script setup lang="ts">
/**
 * [시스템 > 앱 버전 관리]
 * Base Code 아키텍처 기반 구현
 */
import { h, ref, reactive, resolveComponent, watch, computed } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { FormSubmitEvent } from "@nuxt/ui";
import * as z from "zod";
import { format } from "date-fns";
import { getPaginationRowModel, getSortedRowModel } from "@tanstack/table-core";

// 1. 컴포넌트 리졸브
const UButton = resolveComponent("UButton");
const UBadge = resolveComponent("UBadge");
const UDropdownMenu = resolveComponent("UDropdownMenu");
const UCheckbox = resolveComponent("UCheckbox");
const UIcon = resolveComponent("UIcon");

const toast = useToast();
const table = ref<any>(null);

// 2. 설정 및 데이터 정의
const PAGE_TITLE = "앱 버전 관리";
const DATA_KEY = "app_versions";

type VersionItem = {
  id: number;
  platform: "ios" | "android";
  version: string;
  buildNumber: number;
  isRequired: boolean; // 강제 업데이트 여부
  releaseNotes: string;
  status: "active" | "inactive";
  releaseDate: string;
};

const formSchema = z.object({
  platform: z.enum(["ios", "android"]),
  version: z
    .string()
    .regex(/^\d+\.\d+\.\d+$/, "x.y.z 형식을 입력해주세요 (예: 1.0.2)"),
  buildNumber: z.number().min(1),
  isRequired: z.boolean(),
  status: z.enum(["active", "inactive"]),
  releaseNotes: z.string().optional(),
});

type FormSchema = z.output<typeof formSchema>;

// 3. 상태 관리
const columnFilters = ref([{ id: "version", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: "buildNumber", desc: true }]);

const platformFilter = ref("all");
const isModalOpen = ref(false);
const isEditMode = ref(false);
const selectedId = ref<number | null>(null);

// 폼 상태
const initialFormState: FormSchema = {
  platform: "ios",
  version: "",
  buildNumber: 1,
  isRequired: false,
  status: "active",
  releaseNotes: "",
};
const formState = reactive<FormSchema>({ ...initialFormState });

// 4. 데이터 페칭
const { data, status: loadingStatus } = await useAsyncData<VersionItem[]>(
  DATA_KEY,
  async () => {
    return Array.from({ length: 20 }).map((_, i) => ({
      id: 20 - i,
      platform: i % 2 === 0 ? "ios" : "android",
      version: `1.${Math.floor(i / 2)}.${i % 3}`,
      buildNumber: 100 + i,
      isRequired: i % 5 === 0,
      status: i < 4 ? "active" : "inactive",
      releaseDate: new Date().toISOString(),
      releaseNotes: "버그 수정 및 성능 개선",
    }));
  },
);

// 5. 액션 핸들러
function openCreateModal() {
  isEditMode.value = false;
  Object.assign(formState, initialFormState);
  isModalOpen.value = true;
}

function openEditModal(row: VersionItem) {
  isEditMode.value = true;
  selectedId.value = row.id;
  Object.assign(formState, row);
  isModalOpen.value = true;
}

async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  const action = isEditMode.value ? "수정" : "배포";
  toast.add({
    title: `${action} 완료`,
    description: `버전 정보가 ${action}되었습니다.`,
    color: "success",
  });
  isModalOpen.value = false;
}

function onDelete(ids: number[]) {
  toast.add({
    title: "삭제 완료",
    description: "선택한 버전 정보가 삭제되었습니다.",
    color: "error",
  });
  rowSelection.value = {};
}

function getRowItems(row: VersionItem) {
  return [
    { type: "label", label: "관리" },
    {
      label: "수정",
      icon: "i-lucide-edit",
      onSelect: () => openEditModal(row),
    },
    { type: "separator" },
    {
      label: "삭제",
      icon: "i-lucide-trash",
      color: "error",
      onSelect: () => onDelete([row.id]),
    },
  ];
}

// 6. 컬럼 정의
const columns: TableColumn<VersionItem>[] = [
  {
    id: "select",
    header: ({ table }) =>
      h(UCheckbox, {
        modelValue: table.getIsSomePageRowsSelected()
          ? "indeterminate"
          : table.getIsAllPageRowsSelected(),
        "onUpdate:modelValue": (v: boolean) =>
          table.toggleAllPageRowsSelected(!!v),
      }),
    cell: ({ row }) =>
      h(UCheckbox, {
        modelValue: row.getIsSelected(),
        "onUpdate:modelValue": (v: boolean) => row.toggleSelected(!!v),
      }),
    enableSorting: false,
  },
  {
    accessorKey: "platform",
    header: "OS",
    cell: ({ row }) =>
      h(UIcon, {
        name:
          row.original.platform === "ios"
            ? "i-lucide-apple"
            : "i-lucide-smartphone",
        class:
          row.original.platform === "ios"
            ? "w-5 h-5 text-gray-900 dark:text-white"
            : "w-5 h-5 text-green-500",
      }),
  },
  { accessorKey: "version", header: "버전명" },
  { accessorKey: "buildNumber", header: "빌드 번호" },
  {
    accessorKey: "isRequired",
    header: "업데이트 유형",
    cell: ({ row }) =>
      row.original.isRequired
        ? h(UBadge, { color: "error", variant: "solid" }, () => "강제 업데이트")
        : h(UBadge, { color: "neutral", variant: "subtle" }, () => "선택"),
  },
  {
    accessorKey: "status",
    header: "상태",
    cell: ({ row }) =>
      h(
        UBadge,
        {
          color: row.original.status === "active" ? "success" : "neutral",
          variant: "subtle",
        },
        () => (row.original.status === "active" ? "배포중" : "비활성"),
      ),
  },
  {
    accessorKey: "releaseDate",
    header: "배포일",
    cell: ({ row }) => format(new Date(row.original.releaseDate), "yyyy-MM-dd"),
  },
  {
    id: "actions",
    cell: ({ row }) =>
      h(
        "div",
        { class: "text-right" },
        h(
          UDropdownMenu,
          { content: { align: "end" }, items: getRowItems(row.original) },
          () =>
            h(UButton, {
              icon: "i-lucide-ellipsis-vertical",
              color: "neutral",
              variant: "ghost",
            }),
        ),
      ),
    enableSorting: false,
  },
];

// 7. Watchers
watch(platformFilter, (val) => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("platform")
    ?.setFilterValue(val === "all" ? undefined : val);
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UButton
        label="버전 등록"
        icon="i-lucide-plus"
        color="primary"
        @click="openCreateModal"
      />
      <div class="flex flex-wrap items-center justify-end gap-3 mb-4">
        <USelect
          v-model="platformFilter"
          :items="[
            { label: '전체 OS', value: 'all' },
            { label: 'iOS', value: 'ios' },
            { label: 'Android', value: 'android' },
          ]"
          class="min-w-32"
        />
        <UButton
          v-if="table?.tableApi?.getFilteredSelectedRowModel().rows.length"
          label="선택 삭제"
          color="error"
          variant="subtle"
          icon="i-lucide-trash"
          @click="
            onDelete(
              table?.tableApi
                ?.getFilteredSelectedRowModel()
                .rows.map((r: any) => r.original.id),
            )
          "
        />
      </div>
    </div>

    <UTable
      ref="table"
      v-model:column-filters="columnFilters"
      v-model:column-visibility="columnVisibility"
      v-model:row-selection="rowSelection"
      v-model:pagination="pagination"
      v-model:sorting="sorting"
      :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }"
      :sorting-options="{ getSortedRowModel: getSortedRowModel() } as any"
      :data="data || []"
      :columns="columns"
      :loading="loadingStatus === 'pending'"
      class="shrink-0"
      :ui="{
        base: 'table-fixed border-separate border-spacing-0',
        thead: '[&>tr]:bg-elevated/50 [&>tr]:after:content-none',
        tbody: '[&>tr]:last:[&>td]:border-b-0',
        th: 'py-2 first:rounded-l-lg last:rounded-r-lg border-y border-default first:border-l last:border-r',
        td: 'border-b border-default',
        separator: 'h-0',
      }"
    />

    <div
      class="flex items-center justify-between gap-3 border-t border-default pt-4 mt-auto"
    >
      <div class="text-sm text-muted">
        총 {{ table?.tableApi?.getFilteredRowModel().rows.length }}개 버전
      </div>
      <UPagination
        :default-page="
          (table?.tableApi?.getState().pagination.pageIndex || 0) + 1
        "
        :items-per-page="table?.tableApi?.getState().pagination.pageSize"
        :total="table?.tableApi?.getFilteredRowModel().rows.length"
        @update:page="(p: number) => table?.tableApi?.setPageIndex(p - 1)"
      />
    </div>
  </div>

  <UModal
    v-model:open="isModalOpen"
    :title="isEditMode ? '버전 수정' : '새 버전 등록'"
    :ui="{ wrapper: 'w-full sm:max-w-xl' }"
  >
    <template #body>
      <UForm
        :schema="formSchema"
        :state="formState"
        class="space-y-4 p-4"
        @submit="onSubmit"
      >
        <UFormField label="플랫폼" name="platform" required class="w-full">
          <USelect
            v-model="formState.platform"
            :items="[
              { label: 'iOS', value: 'ios' },
              { label: 'Android', value: 'android' },
            ]"
            class="w-full"
          />
        </UFormField>

        <div class="grid grid-cols-2 gap-4">
          <UFormField
            label="버전명 (x.y.z)"
            name="version"
            required
            class="w-full"
          >
            <UInput
              v-model="formState.version"
              placeholder="1.0.0"
              class="w-full"
            />
          </UFormField>
          <UFormField
            label="빌드 번호"
            name="buildNumber"
            required
            class="w-full"
          >
            <UInput
              v-model.number="formState.buildNumber"
              type="number"
              class="w-full"
            />
          </UFormField>
        </div>

        <div
          class="flex justify-between items-center bg-gray-50 dark:bg-gray-800 p-3 rounded"
        >
          <UFormField name="isRequired" label="강제 업데이트 여부">
            <UCheckbox
              :model-value="formState.isRequired"
              @update:model-value="(v: boolean) => (formState.isRequired = v)"
              label="필수 업데이트 (구버전 사용 불가)"
            />
          </UFormField>

          <UFormField name="status" label="배포 상태">
            <UCheckbox
              :model-value="formState.status === 'active'"
              @update:model-value="
                (v: boolean) => (formState.status = v ? 'active' : 'inactive')
              "
              label="즉시 활성화"
            />
          </UFormField>
        </div>

        <UFormField label="릴리즈 노트" name="releaseNotes" class="w-full">
          <UTextarea
            v-model="formState.releaseNotes"
            :rows="4"
            placeholder="업데이트 내용을 입력하세요"
            class="w-full"
          />
        </UFormField>

        <div
          class="flex justify-end gap-2 pt-4 border-t border-default mt-auto"
        >
          <UButton
            label="취소"
            color="neutral"
            variant="ghost"
            @click="isModalOpen = false"
          />
          <UButton
            type="submit"
            :label="isEditMode ? '수정' : '등록'"
            color="primary"
          />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
