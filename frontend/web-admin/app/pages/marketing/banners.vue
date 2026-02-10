<script setup lang="ts">
/**
 * [마케팅 > 배너/광고 관리]
 * Base Code 아키텍처 기반 구현
 */
import { h, ref, reactive, resolveComponent, watch, computed } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { FormSubmitEvent } from "@nuxt/ui";
import * as z from "zod";
import { format, addDays, subDays } from "date-fns"; // isWithinInterval 제거 (미사용)
import { getPaginationRowModel, getSortedRowModel } from "@tanstack/table-core";

// ==========================================
// 1. 컴포넌트 리졸브
// ==========================================
const UButton = resolveComponent("UButton");
const UBadge = resolveComponent("UBadge");
const UDropdownMenu = resolveComponent("UDropdownMenu");
const UCheckbox = resolveComponent("UCheckbox");
const UIcon = resolveComponent("UIcon");

const toast = useToast();
const table = ref<any>(null);

// ==========================================
// 2. 설정 및 데이터 정의
// ==========================================
const PAGE_TITLE = "배너/광고 관리";
const DATA_KEY = "banners";

type BannerItem = {
  id: number;
  title: string;
  imageUrl: string;
  linkUrl: string;
  position: "main_top" | "main_middle" | "sidebar" | "popup";
  priority: number; // 노출 순서
  startDate: string;
  endDate: string;
  status: "active" | "inactive" | "scheduled";
  clickCount: number;
  createdAt: string;
};

// 폼 스키마
const formSchema = z.object({
  title: z.string().min(2, "배너 제목을 입력해주세요."),
  linkUrl: z.string().url("유효한 URL을 입력해주세요.").or(z.literal("")),
  position: z.enum(["main_top", "main_middle", "sidebar", "popup"]),
  priority: z.number().min(0),
  startDate: z.string(),
  endDate: z.string(),
  status: z.enum(["active", "inactive"]),
  imageUrl: z.string().min(1, "이미지를 등록해주세요."),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. 상태 관리
// ==========================================
const columnFilters = ref([{ id: "title", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: "priority", desc: false }]); // 순서 기준 정렬

const positionFilter = ref("all");
const statusFilter = ref("all");
const isModalOpen = ref(false);
const isEditMode = ref(false);
const selectedId = ref<number | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);

// 폼 상태
const initialFormState: FormSchema = {
  title: "",
  linkUrl: "",
  position: "main_top",
  priority: 1,
  startDate: format(new Date(), "yyyy-MM-dd"),
  endDate: format(addDays(new Date(), 30), "yyyy-MM-dd"),
  status: "active",
  imageUrl: "",
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. 데이터 페칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<BannerItem[]>(
  DATA_KEY,
  async () => {
    const positions = ["main_top", "main_middle", "sidebar", "popup"] as const;

    return Array.from({ length: 30 }).map((_, i) => {
      // [수정] ?? "main_top"을 추가하여 undefined 타입 오류 해결
      const position = positions[i % positions.length] ?? "main_top";
      const isActive = i % 5 !== 0;

      return {
        id: 30 - i,
        title: `프로모션 배너 ${i + 1} - ${position}`,
        imageUrl: `https://picsum.photos/seed/banner${i}/400/200`,
        linkUrl: "https://example.com/event",
        position: position,
        priority: (i % 5) + 1,
        startDate: format(subDays(new Date(), i), "yyyy-MM-dd"),
        endDate: format(addDays(new Date(), 30 - i), "yyyy-MM-dd"),
        status: isActive ? "active" : "inactive",
        clickCount: Math.floor(Math.random() * 5000),
        createdAt: new Date().toISOString(),
      };
    });
  },
);

// ==========================================
// 5. 액션 핸들러
// ==========================================
function openCreateModal() {
  isEditMode.value = false;
  selectedId.value = null;
  Object.assign(formState, initialFormState);
  isModalOpen.value = true;
}

function openEditModal(row: BannerItem) {
  isEditMode.value = true;
  selectedId.value = row.id;
  Object.assign(formState, {
    title: row.title,
    linkUrl: row.linkUrl,
    position: row.position,
    priority: row.priority,
    startDate: row.startDate,
    endDate: row.endDate,
    status: row.status === "scheduled" ? "active" : row.status,
    imageUrl: row.imageUrl,
  });
  isModalOpen.value = true;
}

// 파일 선택 핸들러
function onFileSelect(event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    const previewUrl = URL.createObjectURL(file);
    formState.imageUrl = previewUrl;
  }
}

function removeImage() {
  formState.imageUrl = "";
  if (fileInput.value) fileInput.value.value = "";
}

async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  const action = isEditMode.value ? "수정" : "등록";
  toast.add({
    title: `${action} 완료`,
    description: `배너가 성공적으로 ${action}되었습니다.`,
    color: "success",
  });
  isModalOpen.value = false;
}

function onDelete(ids: number[]) {
  toast.add({
    title: "삭제 완료",
    description: `${ids.length}개의 배너가 삭제되었습니다.`,
    color: "error",
  });
  rowSelection.value = {};
}

function getRowItems(row: BannerItem) {
  return [
    { type: "label", label: "관리" },
    {
      label: "수정하기",
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

// ==========================================
// 6. 테이블 컬럼 정의
// ==========================================
const columnLabels: Record<string, string> = {
  select: "선택",
  id: "No.",
  imageUrl: "이미지",
  title: "배너 정보",
  position: "위치",
  priority: "순서",
  period: "게시 기간",
  status: "상태",
  actions: "관리",
};

const columns: TableColumn<BannerItem>[] = [
  {
    id: "select",
    header: ({ table }) =>
      h(UCheckbox, {
        modelValue: table.getIsSomePageRowsSelected()
          ? "indeterminate"
          : table.getIsAllPageRowsSelected(),
        "onUpdate:modelValue": (v: boolean) =>
          table.toggleAllPageRowsSelected(!!v),
        ariaLabel: "전체 선택",
      }),
    cell: ({ row }) =>
      h(UCheckbox, {
        modelValue: row.getIsSelected(),
        "onUpdate:modelValue": (v: boolean) => row.toggleSelected(!!v),
        ariaLabel: "행 선택",
      }),
    enableSorting: false,
  },
  {
    accessorKey: "id",
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(UButton, {
        color: "neutral",
        variant: "ghost",
        label: "No.",
        icon:
          isSorted === "asc"
            ? "i-lucide-arrow-up-narrow-wide"
            : isSorted === "desc"
              ? "i-lucide-arrow-down-wide-narrow"
              : "i-lucide-arrow-up-down",
        class: "-ml-2.5 font-bold hover:bg-gray-100 dark:hover:bg-gray-800",
        onClick: () => column.toggleSorting(column.getIsSorted() === "asc"),
      });
    },
  },
  {
    accessorKey: "imageUrl",
    header: "이미지",
    cell: ({ row }) =>
      h("img", {
        src: row.original.imageUrl,
        class:
          "h-10 w-20 object-cover rounded border border-gray-200 dark:border-gray-700 bg-gray-50",
      }),
  },
  {
    accessorKey: "title",
    header: "배너 정보",
    cell: ({ row }) =>
      h("div", { class: "flex flex-col max-w-[250px]" }, [
        h(
          "span",
          {
            class:
              "font-medium truncate cursor-pointer hover:underline hover:text-primary",
            onClick: () => openEditModal(row.original),
          },
          row.original.title,
        ),
        h(
          "span",
          { class: "text-xs text-gray-400 truncate" },
          row.original.linkUrl,
        ),
      ]),
  },
  {
    accessorKey: "position",
    header: "게시 위치",
    cell: ({ row }) => {
      const map: Record<string, string> = {
        main_top: "메인 상단",
        main_middle: "메인 중단",
        sidebar: "사이드바",
        popup: "팝업",
      };
      return h(
        UBadge,
        { color: "neutral", variant: "subtle" },
        () => map[row.original.position],
      );
    },
  },
  {
    accessorKey: "priority",
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(UButton, {
        color: "neutral",
        variant: "ghost",
        label: "순서",
        icon:
          isSorted === "asc"
            ? "i-lucide-arrow-up-narrow-wide"
            : isSorted === "desc"
              ? "i-lucide-arrow-down-wide-narrow"
              : "i-lucide-arrow-up-down",
        class: "-ml-2.5 font-bold hover:bg-gray-100 dark:hover:bg-gray-800",
        onClick: () => column.toggleSorting(column.getIsSorted() === "asc"),
      });
    },
    cell: ({ row }) => h("span", { class: "font-mono" }, row.original.priority),
  },
  {
    id: "period",
    header: "게시 기간",
    cell: ({ row }) =>
      h("div", { class: "text-xs flex flex-col" }, [
        h("span", `${row.original.startDate} ~`),
        h("span", row.original.endDate),
      ]),
  },
  {
    accessorKey: "status",
    header: "상태",
    cell: ({ row }) => {
      const today = new Date();
      const start = new Date(row.original.startDate);
      // const end = new Date(row.original.endDate);

      let status: "active" | "inactive" | "scheduled" = row.original
        .status as any;
      if (status === "active") {
        if (today < start) status = "scheduled";
        // else if (today > end) status = 'inactive' // 만료 로직 포함 시
      }

      const map = {
        active: { label: "게시중", color: "success" },
        inactive: { label: "중지", color: "neutral" },
        scheduled: { label: "예약", color: "warning" },
      } as const;

      const info = map[status] || map.inactive;
      return h(
        UBadge,
        { color: info.color, variant: "subtle" },
        () => info.label,
      );
    },
  },
  {
    id: "actions",
    cell: ({ row }) =>
      h(
        "div",
        { class: "text-right" },
        h(
          UDropdownMenu,
          {
            content: { align: "end" },
            items: getRowItems(row.original),
          },
          () =>
            h(UButton, {
              icon: "i-lucide-ellipsis-vertical",
              color: "neutral",
              variant: "ghost",
              class: "ml-auto",
            }),
        ),
      ),
    enableSorting: false,
  },
];

// ==========================================
// 7. Watchers & Computeds
// ==========================================
watch([positionFilter, statusFilter], () => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("position")
    ?.setFilterValue(
      positionFilter.value === "all" ? undefined : positionFilter.value,
    );
  table.value.tableApi
    .getColumn("status")
    ?.setFilterValue(
      statusFilter.value === "all" ? undefined : statusFilter.value,
    );
});

const titleSearch = computed({
  get: () =>
    (table.value?.tableApi?.getColumn("title")?.getFilterValue() as string) ||
    "",
  set: (val) =>
    table.value?.tableApi?.getColumn("title")?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="titleSearch"
        icon="i-lucide-search"
        placeholder="배너명 검색..."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
        <UButton
          label="배너 등록"
          icon="i-lucide-plus"
          color="primary"
          @click="openCreateModal"
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
        >
          <template #trailing
            ><UKbd>{{
              table?.tableApi?.getFilteredSelectedRowModel().rows.length
            }}</UKbd></template
          >
        </UButton>

        <USelect
          v-model="positionFilter"
          :items="[
            { label: '전체 위치', value: 'all' },
            { label: '메인 상단', value: 'main_top' },
            { label: '메인 중단', value: 'main_middle' },
            { label: '사이드바', value: 'sidebar' },
            { label: '팝업', value: 'popup' },
          ]"
          class="min-w-32"
        />

        <USelect
          v-model="statusFilter"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '게시중', value: 'active' },
            { label: '중지/만료', value: 'inactive' },
            { label: '예약됨', value: 'scheduled' },
          ]"
          class="min-w-32"
        />

        <UDropdownMenu
          :items="
            table?.tableApi
              ?.getAllColumns()
              .filter((c: any) => c.getCanHide())
              .map((c: any) => ({
                label: columnLabels[c.id] || c.id,
                type: 'checkbox',
                checked: c.getIsVisible(),
                onUpdateChecked: (v: boolean) =>
                  table?.tableApi?.getColumn(c.id)?.toggleVisibility(v),
                onSelect: (e: Event) => e.preventDefault(),
              })) || []
          "
          :content="{ align: 'end' }"
        >
          <UButton
            label="컬럼 설정"
            color="neutral"
            variant="outline"
            trailing-icon="i-lucide-settings-2"
          />
        </UDropdownMenu>
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
        총 {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}개 중
        {{ table?.tableApi?.getFilteredSelectedRowModel().rows.length || 0 }}개
        선택됨.
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
    :title="isEditMode ? '배너 수정' : '배너 등록'"
    :ui="{ wrapper: 'w-full sm:max-w-2xl' }"
  >
    <template #body>
      <UForm
        :schema="formSchema"
        :state="formState"
        class="space-y-4 p-4"
        @submit="onSubmit"
      >
        <UFormField label="배너 이미지" name="imageUrl" required class="w-full">
          <div class="flex flex-col gap-2">
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              class="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-primary-50 file:text-primary-700 hover:file:bg-primary-100"
              @change="onFileSelect"
            />
            <div
              v-if="formState.imageUrl"
              class="relative w-full mt-2 rounded-lg overflow-hidden border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800"
            >
              <img
                :src="formState.imageUrl"
                alt="Preview"
                class="w-full h-48 object-contain"
              />
              <UButton
                icon="i-lucide-x"
                color="gray"
                variant="solid"
                class="absolute top-2 right-2"
                @click="removeImage"
              />
            </div>
          </div>
        </UFormField>

        <div class="grid grid-cols-1 gap-4">
          <UFormField label="배너 제목" name="title" required class="w-full">
            <UInput
              v-model="formState.title"
              placeholder="관리용 배너 제목을 입력하세요."
              class="w-full"
            />
          </UFormField>
          <UFormField label="연결 링크 (URL)" name="linkUrl" class="w-full">
            <UInput
              v-model="formState.linkUrl"
              placeholder="https://example.com/event"
              class="w-full"
            />
          </UFormField>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <UFormField label="게시 위치" name="position" required class="w-full">
            <USelect
              v-model="formState.position"
              :items="[
                { label: '메인 상단', value: 'main_top' },
                { label: '메인 중단', value: 'main_middle' },
                { label: '사이드바', value: 'sidebar' },
                { label: '팝업', value: 'popup' },
              ]"
              class="w-full"
            />
          </UFormField>
          <UFormField
            label="노출 순서 (우선순위)"
            name="priority"
            required
            class="w-full"
          >
            <UInput
              v-model.number="formState.priority"
              type="number"
              placeholder="1"
              class="w-full"
            />
          </UFormField>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <UFormField
            label="게시 시작일"
            name="startDate"
            required
            class="w-full"
          >
            <UInput v-model="formState.startDate" type="date" class="w-full" />
          </UFormField>
          <UFormField
            label="게시 종료일"
            name="endDate"
            required
            class="w-full"
          >
            <UInput v-model="formState.endDate" type="date" class="w-full" />
          </UFormField>
        </div>

        <UFormField label="운영 상태" name="status" required class="w-full">
          <div class="flex items-center gap-4">
            <UCheckbox
              :model-value="formState.status === 'active'"
              @update:model-value="
                (v: boolean) => (formState.status = v ? 'active' : 'inactive')
              "
              label="활성화 (즉시 게시)"
            />
            <span class="text-xs text-gray-500"
              >* 기간 내라도 비활성화 시 노출되지 않습니다.</span
            >
          </div>
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
            :label="isEditMode ? '수정 완료' : '등록하기'"
            color="primary"
          />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
