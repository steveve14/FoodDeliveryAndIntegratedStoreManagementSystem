<script setup lang="ts">
/**
 * [신고 및 제재 관리]
 * 요청하신 UModal > #body > UForm 구조를 적용한 버전입니다.
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
const URadioGroup = resolveComponent("URadioGroup"); // [추가] 상태 선택용

const toast = useToast();
const table = ref<any>(null);

// ==========================================
// 2. 설정 및 데이터 정의
// ==========================================
const PAGE_TITLE = "신고 및 제재 관리";
const DATA_KEY = "reports";

type ReportItem = {
  id: number;
  targetType: "Review" | "User" | "Post";
  targetName: string;
  reason: "spam" | "abuse" | "sexual" | "fraud";
  description: string;
  reporter: string;
  status: "open" | "resolved" | "dismissed";
  createdAt: string;
  adminNote?: string;
};

// 폼 스키마
const formSchema = z.object({
  status: z.enum(["resolved", "dismissed"]), // 제재(resolved) 또는 반려(dismissed)
  adminNote: z.string().min(1, "조치 사유를 입력해주세요."),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. 상태 관리
// ==========================================
const columnFilters = ref([{ id: "targetName", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: "id", desc: true }]);

const statusFilter = ref("all");
const isModalOpen = ref(false);
const selectedId = ref<number | null>(null);

// 상세 정보 표시용 (읽기 전용)
const currentReport = ref<ReportItem | null>(null);

// 폼 상태
const initialFormState: FormSchema = { status: "resolved", adminNote: "" };
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. 데이터 페칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<ReportItem[]>(
  DATA_KEY,
  async () => {
    const targetTypes: Array<"Review" | "User" | "Post"> = [
      "Review",
      "User",
      "Post",
    ];
    const reasons: Array<"spam" | "abuse" | "sexual" | "fraud"> = [
      "spam",
      "abuse",
      "sexual",
      "fraud",
    ];

    return Array.from({ length: 50 }).map((_, i) => {
      const isOpen = i % 3 === 0;
      const type = targetTypes[i % targetTypes.length];
      const reason = reasons[i % reasons.length];

      return {
        id: 50 - i,
        targetType: type,
        targetName: type === "User" ? `@user_${i}` : `${type} #${1000 + i}`,
        reason: reason,
        description: "부적절한 내용을 포함하고 있습니다. 확인 부탁드립니다.",
        reporter: `reporter_${i}`,
        status: isOpen ? "open" : i % 2 === 0 ? "resolved" : "dismissed",
        createdAt: subHours(subDays(new Date(), i % 5), i).toISOString(),
        adminNote: isOpen ? undefined : "운영 정책 위반 확인되어 조치함.",
      } as ReportItem;
    });
  },
);

// ==========================================
// 5. 액션 핸들러
// ==========================================
function openProcessModal(row: ReportItem) {
  currentReport.value = row;
  selectedId.value = row.id;

  if (row.status !== "open") {
    Object.assign(formState, {
      status: row.status,
      adminNote: row.adminNote || "",
    });
  } else {
    // 기본값: 제재 처리
    Object.assign(formState, { status: "resolved", adminNote: "" });
  }
  isModalOpen.value = true;
}

async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  if (currentReport.value) {
    currentReport.value.status = formState.status;
    currentReport.value.adminNote = formState.adminNote;
  }
  const actionText = formState.status === "resolved" ? "제재 처리" : "반려";
  toast.add({
    title: "처리 완료",
    description: `${actionText} 되었습니다.`,
    color: "success",
  });
  isModalOpen.value = false;
}

function onDelete(ids: number[]) {
  toast.add({
    title: "삭제 완료",
    description: `${ids.length}개의 기록이 삭제되었습니다.`,
    color: "error",
  });
  rowSelection.value = {};
}

function getRowItems(row: ReportItem) {
  const isProcessed = row.status !== "open";
  return [
    { type: "label", label: "관리" },
    {
      label: isProcessed ? "처리 내역 보기" : "신고 처리하기",
      icon: isProcessed ? "i-lucide-eye" : "i-lucide-gavel",
      onSelect: () => openProcessModal(row),
    },
    { type: "separator" },
    {
      label: "기록 삭제",
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
  reason: "신고 사유",
  targetName: "신고 대상",
  reporter: "신고자",
  status: "상태",
  createdAt: "접수일",
  actions: "관리",
};

const columns: TableColumn<ReportItem>[] = [
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
    accessorKey: "reason",
    header: "신고 사유",
    cell: ({ row }) => {
      const reasonMap: Record<string, string> = {
        spam: "스팸/홍보",
        abuse: "욕설/비방",
        sexual: "음란물",
        fraud: "사기/도배",
      };
      return h(
        "span",
        { class: "text-red-500 font-medium" },
        reasonMap[row.original.reason] || row.original.reason,
      );
    },
  },
  {
    accessorKey: "targetName",
    header: "신고 대상",
    cell: ({ row }) =>
      h("div", { class: "flex flex-col" }, [
        h("span", { class: "text-xs text-gray-500" }, row.original.targetType),
        h(
          "span",
          {
            class:
              "font-medium cursor-pointer hover:underline hover:text-primary",
            onClick: () => openProcessModal(row.original),
          },
          row.original.targetName,
        ),
      ]),
  },
  { accessorKey: "reporter", header: "신고자" },
  {
    accessorKey: "status",
    header: "처리 상태",
    filterFn: "equals",
    cell: ({ row }) => {
      const status = row.original.status;
      const config = {
        open: { label: "접수됨", color: "error", variant: "solid" },
        resolved: { label: "조치 완료", color: "success", variant: "subtle" },
        dismissed: { label: "반려됨", color: "neutral", variant: "subtle" },
      } as const;
      const current = config[status];
      return h(
        UBadge,
        { color: current.color, variant: current.variant },
        () => current.label,
      );
    },
  },
  {
    accessorKey: "createdAt",
    header: "접수일",
    cell: ({ row }) =>
      format(new Date(row.original.createdAt), "yyyy-MM-dd HH:mm"),
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

watch(statusFilter, (val) => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("status")
    ?.setFilterValue(val === "all" ? undefined : val);
});

const targetSearch = computed({
  get: () =>
    (table.value?.tableApi
      ?.getColumn("targetName")
      ?.getFilterValue() as string) || "",
  set: (val) =>
    table.value?.tableApi
      ?.getColumn("targetName")
      ?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="targetSearch"
        icon="i-lucide-search"
        placeholder="대상 검색..."
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
          v-model="statusFilter"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '접수됨', value: 'open' },
            { label: '조치 완료', value: 'resolved' },
            { label: '반려됨', value: 'dismissed' },
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
    title="신고 처리 및 상세"
    :ui="{ wrapper: 'w-full sm:max-w-2xl' }"
  >
    <template #body>
      <UForm
        :schema="formSchema"
        :state="formState"
        class="space-y-4 p-4"
        @submit="onSubmit"
      >
        <div
          v-if="currentReport"
          class="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg space-y-3 border border-gray-100 dark:border-gray-700"
        >
          <div class="flex justify-between items-start">
            <UBadge
              :color="currentReport.status === 'open' ? 'error' : 'neutral'"
              variant="subtle"
            >
              {{
                currentReport.status === "open"
                  ? "접수됨 (미처리)"
                  : currentReport.status === "resolved"
                    ? "조치 완료"
                    : "반려됨"
              }}
            </UBadge>
            <span class="text-sm text-gray-500"
              >{{ currentReport.targetType }} |
              {{ currentReport.reporter }}</span
            >
          </div>

          <h3 class="font-bold text-lg text-gray-900 dark:text-white">
            {{ currentReport.targetName }}
            <span class="text-sm font-normal text-red-500 ml-2"
              >({{ currentReport.reason }})</span
            >
          </h3>
          <p
            class="text-sm text-gray-700 dark:text-gray-300 whitespace-pre-wrap"
          >
            {{ currentReport.description }}
          </p>
        </div>

        <hr class="border-gray-200 dark:border-gray-700" />

        <UFormField
          label="관리자 조치 메모"
          name="adminNote"
          required
          class="w-full"
        >
          <UTextarea
            v-model="formState.adminNote"
            :rows="4"
            placeholder="조치 사유 및 결과를 입력해주세요."
            autoresize
            class="w-full"
          />
        </UFormField>

        <div class="flex flex-col gap-2">
          <label class="text-sm font-medium text-gray-700 dark:text-gray-200"
            >처리 결과</label
          >
          <UFormField name="status">
            <URadioGroup
              v-model="formState.status"
              :items="[
                { value: 'resolved', label: '제재 처리 (승인)' },
                { value: 'dismissed', label: '반려 (무시)' },
              ]"
              :ui="{ wrapper: 'flex items-center gap-6' }"
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
            @click="isModalOpen = false"
          />
          <UButton type="submit" label="처리 완료" color="primary" />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
