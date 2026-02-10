<script setup lang="ts">
/**
 * [FAQ 관리]
 * Base Code 아키텍처 기반 리팩토링
 */
import { h, ref, reactive, resolveComponent, watch, computed } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { FormSubmitEvent } from "@nuxt/ui";
import * as z from "zod";
import { format, subDays, subHours } from "date-fns";
import { getPaginationRowModel, getSortedRowModel } from "@tanstack/table-core";

// ==========================================
// 1. 컴포넌트 리졸브
// ==========================================
const UButton = resolveComponent("UButton");
const UBadge = resolveComponent("UBadge");
const UDropdownMenu = resolveComponent("UDropdownMenu");
const UCheckbox = resolveComponent("UCheckbox");
const UIcon = resolveComponent("UIcon");
const UTextarea = resolveComponent("UTextarea");

const toast = useToast();
const table = ref<any>(null);

// ==========================================
// 2. 설정 및 데이터 정의
// ==========================================
const PAGE_TITLE = "FAQ 관리";
const DATA_KEY = "faqs";

type FaqItem = {
  id: number;
  category: "member" | "payment" | "shipping" | "etc";
  question: string;
  answer: string;
  viewCount: number;
  isPublished: boolean;
  createdAt: string;
};

// 폼 스키마
const formSchema = z.object({
  category: z.enum(["member", "payment", "shipping", "etc"]).catch("etc"),
  question: z.string().min(2, "질문을 입력해주세요."),
  answer: z.string().min(5, "답변 내용을 입력해주세요."),
  isPublished: z.boolean().default(true),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. 상태 관리
// ==========================================
const columnFilters = ref([{ id: "question", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: "id", desc: true }]);

const categoryFilter = ref("all");
const isModalOpen = ref(false);
const isEditMode = ref(false);
const selectedId = ref<number | null>(null);

// 폼 상태
const initialFormState: FormSchema = {
  category: "member",
  question: "",
  answer: "",
  isPublished: true,
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. 데이터 페칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<FaqItem[]>(
  DATA_KEY,
  async () => {
    const categories = ["member", "payment", "shipping", "etc"] as const;

    return Array.from({ length: 50 }).map((_, i) => {
      const category = categories[i % 4] as
        | "member"
        | "payment"
        | "shipping"
        | "etc";

      return {
        id: 50 - i,
        category: category,
        question: `자주 묻는 질문 예시입니다 (${i + 1})?`,
        answer: `해당 질문에 대한 상세 답변 내용입니다.\n줄바꿈이 포함된 예시 텍스트입니다.`,
        viewCount: Math.floor(Math.random() * 2000),
        isPublished: i % 10 !== 0, // 10% 확률로 비공개
        createdAt: subDays(new Date(), i).toISOString(),
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

function openEditModal(row: FaqItem) {
  isEditMode.value = true;
  selectedId.value = row.id;
  Object.assign(formState, {
    category: row.category,
    question: row.question,
    answer: row.answer,
    isPublished: row.isPublished,
  });
  isModalOpen.value = true;
}

async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  const action = isEditMode.value ? "수정" : "등록";
  toast.add({
    title: `${action} 완료`,
    description: `FAQ가 성공적으로 ${action}되었습니다.`,
    color: "success",
  });
  isModalOpen.value = false;
}

function onDelete(ids: number[]) {
  toast.add({
    title: "삭제 완료",
    description: `${ids.length}개의 항목이 삭제되었습니다.`,
    color: "error",
  });
  rowSelection.value = {};
}

function getRowItems(row: FaqItem) {
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
  category: "카테고리",
  question: "질문",
  viewCount: "조회수",
  isPublished: "상태",
  createdAt: "등록일",
  actions: "관리",
};

const columns: TableColumn<FaqItem>[] = [
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
    accessorKey: "category",
    header: "카테고리",
    cell: ({ row }) => {
      const map: Record<string, string> = {
        member: "회원/계정",
        payment: "결제/환불",
        shipping: "배송/택배",
        etc: "기타",
      };
      return h(
        UBadge,
        { variant: "subtle", color: "neutral" },
        () => map[row.original.category],
      );
    },
  },
  {
    accessorKey: "question",
    header: "질문",
    cell: ({ row }) =>
      h(
        "span",
        {
          class:
            "font-medium cursor-pointer hover:underline hover:text-primary",
          onClick: () => openEditModal(row.original),
        },
        row.original.question,
      ),
  },
  {
    accessorKey: "viewCount",
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(UButton, {
        color: "neutral",
        variant: "ghost",
        label: "조회수",
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
    cell: ({ row }) => row.original.viewCount.toLocaleString(),
  },
  {
    accessorKey: "isPublished",
    header: "상태",
    cell: ({ row }) =>
      h(
        UBadge,
        {
          color: row.original.isPublished ? "success" : "neutral",
          variant: "subtle",
          size: "xs",
        },
        () => (row.original.isPublished ? "게시중" : "비공개"),
      ),
  },
  {
    accessorKey: "createdAt",
    header: "등록일",
    cell: ({ row }) => format(new Date(row.original.createdAt), "yyyy-MM-dd"),
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
watch(categoryFilter, (val) => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("category")
    ?.setFilterValue(val === "all" ? undefined : val);
});

const questionSearch = computed({
  get: () =>
    (table.value?.tableApi
      ?.getColumn("question")
      ?.getFilterValue() as string) || "",
  set: (val) =>
    table.value?.tableApi
      ?.getColumn("question")
      ?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="questionSearch"
        icon="i-lucide-search"
        placeholder="질문 검색..."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
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
          v-model="categoryFilter"
          :items="[
            { label: '전체 카테고리', value: 'all' },
            { label: '회원/계정', value: 'member' },
            { label: '결제/환불', value: 'payment' },
            { label: '배송/택배', value: 'shipping' },
            { label: '기타', value: 'etc' },
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
    :title="isEditMode ? 'FAQ 수정' : 'FAQ 등록'"
    :ui="{ wrapper: 'w-full sm:max-w-2xl' }"
  >
    <template #body>
      <UForm
        :schema="formSchema"
        :state="formState"
        class="space-y-4 p-4"
        @submit="onSubmit"
      >
        <div class="grid grid-cols-2 gap-4">
          <UFormField label="카테고리" name="category" required class="w-full">
            <USelect
              v-model="formState.category"
              :items="[
                { label: '회원/계정', value: 'member' },
                { label: '결제/환불', value: 'payment' },
                { label: '배송/택배', value: 'shipping' },
                { label: '기타', value: 'etc' },
              ]"
              class="w-full"
            />
          </UFormField>

          <div class="flex items-end pb-2">
            <UCheckbox
              v-model="formState.isPublished"
              label="공개 상태로 등록"
              color="primary"
            />
          </div>
        </div>

        <UFormField
          label="질문 (Question)"
          name="question"
          required
          class="w-full"
        >
          <UInput
            v-model="formState.question"
            placeholder="자주 묻는 질문을 입력하세요."
            class="w-full"
          />
        </UFormField>

        <UFormField label="답변 (Answer)" name="answer" required class="w-full">
          <UTextarea
            v-model="formState.answer"
            :rows="8"
            placeholder="답변 내용을 입력하세요."
            autoresize
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
            :label="isEditMode ? '수정 완료' : '등록하기'"
            color="primary"
          />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
