<script setup lang="ts">
/**
 * [시스템 > 에러 로그 관리]
 * Base Code 아키텍처 기반 리팩토링
 */
import { h, ref, reactive, resolveComponent, watch, computed } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { FormSubmitEvent } from "@nuxt/ui";
import * as z from "zod";
import { format, subMinutes } from "date-fns";
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
const PAGE_TITLE = "에러 로그 관리";
const DATA_KEY = "error-logs";

type ErrorLogItem = {
  id: number;
  timestamp: string;
  level: "critical" | "error" | "warning";
  status: "open" | "in_progress" | "resolved";
  message: string;
  source: string;
  traceId: string;
  stackTrace?: string; // 상세 스택 트레이스 (가상)
};

// 폼 스키마 (상태 변경용)
const formSchema = z.object({
  status: z.enum(["open", "in_progress", "resolved"]),
  adminNote: z.string().optional(),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. 상태 관리
// ==========================================
const columnFilters = ref([{ id: "message", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 }); // 로그는 한 번에 많이 봄
const sorting = ref([{ id: "timestamp", desc: true }]);

const levelFilter = ref("all");
const statusFilter = ref("all");
const isModalOpen = ref(false);
const selectedId = ref<number | null>(null);

// 상세 정보 표시용
const currentLog = ref<ErrorLogItem | null>(null);

// 폼 상태
const initialFormState: FormSchema = { status: "open", adminNote: "" };
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. 데이터 페칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<ErrorLogItem[]>(
  DATA_KEY,
  async () => {
    return Array.from({ length: 50 }).map((_, i) => {
      const level =
        i % 10 === 0 ? "critical" : i % 3 === 0 ? "warning" : "error";
      const status = ["open", "in_progress", "resolved"][i % 3] as any;

      return {
        id: 5000 - i,
        timestamp: subMinutes(new Date(), i * 15).toISOString(),
        level: level,
        status: status,
        message:
          i % 10 === 0
            ? "Database connection failed"
            : `Unexpected token in JSON at position ${i}`,
        source: i % 2 === 0 ? "/api/auth/login" : "/components/Dashboard.vue",
        traceId: `trace-${Math.random().toString(36).substring(7)}`,
        stackTrace: `Error: ${i % 10 === 0 ? "Database connection failed" : "Unexpected token"}\n    at /app/server/api.ts:45:12\n    at async /app/server/handler.ts:22:5`,
      };
    });
  },
);

// ==========================================
// 5. 액션 핸들러
// ==========================================
function openDetailModal(row: ErrorLogItem) {
  currentLog.value = row;
  selectedId.value = row.id;
  Object.assign(formState, { status: row.status, adminNote: "" });
  isModalOpen.value = true;
}

async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  if (currentLog.value) {
    currentLog.value.status = formState.status;
  }
  toast.add({
    title: "상태 업데이트",
    description: "로그 상태가 변경되었습니다.",
    color: "success",
  });
  isModalOpen.value = false;
}

function copyTraceId(traceId: string) {
  navigator.clipboard.writeText(traceId);
  toast.add({ title: "복사 완료", description: `Trace ID가 복사되었습니다.` });
}

function getRowItems(row: ErrorLogItem) {
  return [
    { type: "label", label: "관리" },
    {
      label: "상세 보기",
      icon: "i-lucide-search",
      onSelect: () => openDetailModal(row),
    },
    {
      label: "Trace ID 복사",
      icon: "i-lucide-copy",
      onSelect: () => copyTraceId(row.traceId),
    },
    { type: "separator" },
    {
      label: "해결 처리",
      icon: "i-lucide-check-circle",
      color: "success",
      disabled: row.status === "resolved",
      onSelect: () => {
        row.status = "resolved";
        toast.add({ title: "처리 완료", color: "success" });
      },
    },
  ];
}

// ==========================================
// 6. 테이블 컬럼 정의
// ==========================================
const columnLabels: Record<string, string> = {
  select: "선택",
  timestamp: "발생 시간",
  level: "심각도",
  message: "에러 메시지",
  source: "위치",
  status: "상태",
  actions: "관리",
};

const columns: TableColumn<ErrorLogItem>[] = [
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
    accessorKey: "timestamp",
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(UButton, {
        color: "neutral",
        variant: "ghost",
        label: "발생 시간",
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
    cell: ({ row }) =>
      format(new Date(row.original.timestamp), "yyyy-MM-dd HH:mm:ss"),
  },
  {
    accessorKey: "level",
    header: "심각도",
    cell: ({ row }) => {
      const map: Record<string, any> = {
        critical: { label: "CRITICAL", color: "error", variant: "solid" },
        error: { label: "ERROR", color: "error", variant: "subtle" },
        warning: { label: "WARN", color: "warning", variant: "subtle" },
      };
      const info = map[row.original.level];
      return h(
        UBadge,
        { color: info.color, variant: info.variant },
        () => info.label,
      );
    },
  },
  {
    accessorKey: "status",
    header: "상태",
    cell: ({ row }) => {
      const map: Record<string, any> = {
        open: { label: "대기", color: "error" },
        in_progress: { label: "진행중", color: "primary" },
        resolved: { label: "해결됨", color: "success" },
      };
      const info = map[row.original.status];
      return h(
        UBadge,
        { color: info.color, variant: "subtle" },
        () => info.label,
      );
    },
  },
  {
    accessorKey: "message",
    header: "에러 메시지",
    cell: ({ row }) =>
      h("div", { class: "flex flex-col max-w-[300px]" }, [
        h(
          "span",
          {
            class:
              "truncate font-mono text-xs cursor-pointer hover:text-primary hover:underline",
            onClick: () => openDetailModal(row.original),
          },
          row.original.message,
        ),
        h(
          "span",
          { class: "text-xs text-gray-400 font-mono truncate" },
          row.original.traceId,
        ),
      ]),
  },
  {
    accessorKey: "source",
    header: "위치",
    cell: ({ row }) =>
      h(
        "code",
        { class: "text-xs bg-gray-100 dark:bg-gray-800 px-1 py-0.5 rounded" },
        row.original.source,
      ),
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
watch([levelFilter, statusFilter], () => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("level")
    ?.setFilterValue(
      levelFilter.value === "all" ? undefined : levelFilter.value,
    );
  table.value.tableApi
    .getColumn("status")
    ?.setFilterValue(
      statusFilter.value === "all" ? undefined : statusFilter.value,
    );
});

const messageSearch = computed({
  get: () =>
    (table.value?.tableApi?.getColumn("message")?.getFilterValue() as string) ||
    "",
  set: (val) =>
    table.value?.tableApi
      ?.getColumn("message")
      ?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="messageSearch"
        icon="i-lucide-search"
        placeholder="에러 메시지 검색..."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
        <UButton
          label="로그 다운로드"
          icon="i-lucide-download"
          color="neutral"
          variant="outline"
        />
        <USelect
          v-model="levelFilter"
          :items="[
            { label: '전체 등급', value: 'all' },
            { label: 'Critical', value: 'critical' },
            { label: 'Error', value: 'error' },
            { label: 'Warning', value: 'warning' },
          ]"
          class="min-w-32"
        />
        <USelect
          v-model="statusFilter"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '대기 (Open)', value: 'open' },
            { label: '진행중', value: 'in_progress' },
            { label: '해결됨', value: 'resolved' },
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
        총 {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}개 로그
        중
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
    title="에러 로그 상세"
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
          v-if="currentLog"
          class="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg space-y-3 border border-gray-100 dark:border-gray-700"
        >
          <div class="flex justify-between items-start">
            <div class="flex gap-2 items-center">
              <UBadge
                :color="currentLog.level === 'critical' ? 'error' : 'warning'"
                variant="solid"
              >
                {{ currentLog.level.toUpperCase() }}
              </UBadge>
              <span class="text-xs text-gray-500 font-mono">{{
                currentLog.traceId
              }}</span>
            </div>
            <div class="text-xs text-gray-500">
              {{
                format(new Date(currentLog.timestamp), "yyyy-MM-dd HH:mm:ss")
              }}
            </div>
          </div>

          <div class="space-y-1">
            <p class="text-sm font-bold text-gray-900 dark:text-white">
              {{ currentLog.message }}
            </p>
            <p class="text-xs text-gray-500 font-mono">
              at {{ currentLog.source }}
            </p>
          </div>

          <div v-if="currentLog.stackTrace" class="mt-2">
            <p class="text-xs font-semibold mb-1">Stack Trace</p>
            <div
              class="bg-gray-900 text-gray-100 p-2 rounded text-xs font-mono overflow-x-auto whitespace-pre"
            >
              {{ currentLog.stackTrace }}
            </div>
          </div>
        </div>

        <hr class="border-gray-200 dark:border-gray-700" />

        <div class="grid grid-cols-2 gap-4">
          <UFormField label="처리 상태 변경" name="status" class="w-full">
            <USelect
              v-model="formState.status"
              :items="[
                { label: '대기 (Open)', value: 'open' },
                { label: '진행중 (In Progress)', value: 'in_progress' },
                { label: '해결됨 (Resolved)', value: 'resolved' },
              ]"
              class="w-full"
            />
          </UFormField>

          <UFormField label="관리자 메모" name="adminNote" class="w-full">
            <UInput
              v-model="formState.adminNote"
              placeholder="처리 내역 메모"
              class="w-full"
            />
          </UFormField>
        </div>

        <div class="flex justify-end gap-2 pt-2 mt-auto">
          <UButton
            label="닫기"
            color="neutral"
            variant="ghost"
            @click="isModalOpen = false"
          />
          <UButton type="submit" label="저장하기" color="primary" />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
