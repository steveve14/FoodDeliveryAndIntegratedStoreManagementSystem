<script setup lang="ts">
/**
 * [1:1 문의 관리]
 * Base Code(공지사항)의 아키텍처를 기반으로 리팩토링된 코드입니다.
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
const UAvatar = resolveComponent("UAvatar"); // 첨부 이미지 표시용

const toast = useToast();
const table = ref<any>(null);

// ==========================================
// 2. 설정 및 데이터 정의
// ==========================================
const PAGE_TITLE = "1:1 문의 관리";
const DATA_KEY = "inquiries";

type InquiryItem = {
  id: number;
  type: string;
  title: string;
  content: string;
  user: string;
  status: "pending" | "resolved";
  createdAt: string;
  answer?: string; // 답변 내용
  imageUrl?: string; // 첨부 이미지
};

// 폼 스키마 (답변 등록 위주)
const formSchema = z.object({
  title: z.string(),
  type: z.string(),
  user: z.string(),
  status: z.enum(["pending", "resolved"]),
  content: z.string(),
  answer: z.string().optional(), // 답변은 선택사항 (대기중일 수 있으므로)
  imageUrl: z.string().optional(),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. 상태 관리
// ==========================================
const columnFilters = ref([{ id: "title", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: "id", desc: true }]); // 초기 정렬: 최신순

const statusFilter = ref("all");
const isSlideoverOpen = ref(false);
const selectedId = ref<number | null>(null);

// 폼 상태
const initialFormState: FormSchema = {
  title: "",
  type: "",
  user: "",
  status: "pending",
  content: "",
  answer: "",
  imageUrl: "",
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. 데이터 페칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<InquiryItem[]>(
  DATA_KEY,
  async () => {
    const types = ["결제/환불", "계정 이용", "시스템 장애", "기타 문의"];
    const titles = [
      "결제가 중복으로 되었습니다.",
      "로그인이 안돼요",
      "환불 규정이 어떻게 되나요?",
      "앱이 자꾸 꺼집니다.",
      "회원 탈퇴는 어디서 하나요?",
    ];

    return Array.from({ length: 50 }).map((_, i) => {
      const isPending = i % 3 === 0;
      const randomType: string = types[i % types.length]!;
      const randomTitle: string = titles[i % titles.length]!;
      const date = subHours(subDays(new Date(), i % 10), i * 2).toISOString();

      return {
        id: 50 - i,
        user: `User-${1000 + i}`,
        type: randomType,
        title: randomTitle,
        content: `문의사항 상세 내용입니다. (${randomTitle})\n빠른 확인 부탁드립니다.`,
        status: isPending ? "pending" : "resolved",
        createdAt: date,
        answer: isPending
          ? ""
          : "문의주셔서 감사합니다. 해당 내용은 처리되었습니다.",
        imageUrl:
          Math.random() > 0.8
            ? `https://picsum.photos/seed/${i}/200/200`
            : undefined,
      };
    });
  },
);

// ==========================================
// 5. 액션 핸들러
// ==========================================
function openEditModal(row: InquiryItem) {
  selectedId.value = row.id;
  Object.assign(formState, {
    title: row.title,
    type: row.type,
    user: row.user,
    status: row.status,
    content: row.content,
    answer: row.answer || "",
    imageUrl: row.imageUrl,
  });
  isSlideoverOpen.value = true;
}

// 답변 등록/수정 처리
async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  // 실제 로직: API 호출하여 답변 저장 및 상태 변경
  if (formState.answer && formState.status === "pending") {
    formState.status = "resolved"; // 답변이 있으면 완료 처리 (예시)
  }
  toast.add({
    title: "저장 완료",
    description: "문의 내용이 수정되었습니다.",
    color: "success",
  });
  isSlideoverOpen.value = false;
}

function onDelete(ids: number[]) {
  toast.add({
    title: "삭제 완료",
    description: `${ids.length}개의 항목이 삭제되었습니다.`,
    color: "error",
  });
  rowSelection.value = {};
}

function getRowItems(row: InquiryItem) {
  return [
    { type: "label", label: "관리" },
    {
      label: row.status === "pending" ? "답변하기" : "상세보기",
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
  type: "유형",
  title: "제목",
  user: "작성자",
  status: "상태",
  createdAt: "등록일",
  actions: "관리",
};

const columns: TableColumn<InquiryItem>[] = [
  // 1. 체크박스
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
  // 2. No. (정렬 가능)
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
  // 3. 유형
  {
    accessorKey: "type",
    header: "유형",
    cell: ({ row }) =>
      h(UBadge, { variant: "subtle", color: "gray" }, () => row.original.type),
  },
  // 4. 제목 (이미지 있으면 아이콘 표시)
  {
    accessorKey: "title",
    header: "문의 제목",
    cell: ({ row }) =>
      h("div", { class: "flex items-center gap-2" }, [
        // 첨부 이미지 썸네일
        row.original.imageUrl &&
          h(UAvatar, { src: row.original.imageUrl, size: "2xs" }),
        h(
          "span",
          {
            class:
              "font-medium truncate max-w-[300px] cursor-pointer hover:text-primary hover:underline",
            onClick: () => openEditModal(row.original),
          },
          row.original.title,
        ),
      ]),
  },
  // 5. 작성자
  { accessorKey: "user", header: "작성자" },
  // 6. 상태 (필터 적용)
  {
    accessorKey: "status",
    header: "상태",
    filterFn: "equals",
    cell: ({ row }) => {
      const isPending = row.original.status === "pending";
      return h(
        UBadge,
        {
          variant: "subtle",
          color: isPending ? "warning" : "success", // 주황: 대기, 초록: 완료
        },
        () => (isPending ? "답변 대기" : "답변 완료"),
      );
    },
  },
  // 7. 등록일
  {
    accessorKey: "createdAt",
    header: "등록일",
    cell: ({ row }) =>
      format(new Date(row.original.createdAt), "yyyy-MM-dd HH:mm"),
  },
  // 8. 관리
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
watch(statusFilter, (val) => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("status")
    ?.setFilterValue(val === "all" ? undefined : val);
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
        placeholder="제목 검색..."
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
          <template #trailing>
            <UKbd>{{
              table?.tableApi?.getFilteredSelectedRowModel().rows.length
            }}</UKbd>
          </template>
        </UButton>

        <USelect
          v-model="statusFilter"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '답변 대기', value: 'pending' },
            { label: '답변 완료', value: 'resolved' },
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

  <UModal v-model:open="isSlideoverOpen" title="문의 상세 및 답변">
    <template #body>
      <UForm
        :schema="formSchema"
        :state="formState"
        class="space-y-4 p-4"
        @submit="onSubmit"
      >
        <div class="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg space-y-3">
          <div class="flex justify-between">
            <UBadge
              :color="formState.status === 'pending' ? 'warning' : 'success'"
              variant="subtle"
            >
              {{ formState.status === "pending" ? "답변 대기" : "답변 완료" }}
            </UBadge>
            <span class="text-sm text-gray-500"
              >{{ formState.type }} | {{ formState.user }}</span
            >
          </div>
          <h3 class="font-bold text-lg">{{ formState.title }}</h3>
          <p class="text-sm whitespace-pre-wrap">{{ formState.content }}</p>

          <div v-if="formState.imageUrl" class="mt-2">
            <p class="text-xs text-gray-500 mb-1">첨부 이미지:</p>
            <img
              :src="formState.imageUrl"
              class="rounded border w-full max-h-48 object-cover"
            />
          </div>
        </div>

        <hr class="border-gray-200 dark:border-gray-700" />

        <UFormField label="답변 내용" name="answer" required class="w-full">
          <UTextarea
            v-model="formState.answer"
            :rows="8"
            placeholder="답변을 입력해주세요."
            autoresize
            class="w-full"
          />
        </UFormField>

        <div class="flex justify-between items-center">
          <UFormField name="status">
            <UCheckbox
              v-model="formState.status"
              :true-value="'resolved'"
              :false-value="'pending'"
              label="답변 완료 처리"
            />
          </UFormField>
        </div>

        <div
          class="flex justify-end gap-2 pt-4 border-t border-default mt-auto"
        >
          <UButton
            label="닫기"
            color="neutral"
            variant="ghost"
            @click="isSlideoverOpen = false"
          />
          <UButton type="submit" label="답변 저장" color="primary" />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
