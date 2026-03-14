<script setup lang="ts">
/* eslint-disable @typescript-eslint/no-unused-vars, vue/no-multiple-template-root */
/**
 * [?�스??> ?�러 로그 관�?
 * Base Code ?�키?�처 기반 리팩?�링
 */
import { h, ref, reactive, resolveComponent, watch, computed } from 'vue';
import type { TableColumn, FormSubmitEvent } from '@nuxt/ui';

import * as z from 'zod';
import { format, subMinutes } from 'date-fns';
import { getPaginationRowModel } from '@tanstack/table-core';
import type { Table } from '@tanstack/table-core';

// ==========================================
// 1. 컴포?�트 리졸�?
// ==========================================
const UButton = resolveComponent('UButton');
const UBadge = resolveComponent('UBadge');
const UDropdownMenu = resolveComponent('UDropdownMenu');
const UCheckbox = resolveComponent('UCheckbox');
const UIcon = resolveComponent('UIcon');

const toast = useToast();
type TableInstance<T> = { tableApi?: Table<T> } | null;
const table = ref<TableInstance<ErrorLogItem>>(null);

// ==========================================
// 2. ?�정 �??�이???�의
// ==========================================
const DATA_KEY = 'error-logs';

type ErrorLogItem = {
  id: number;
  timestamp: string;
  level: 'critical' | 'error' | 'warning';
  status: 'open' | 'in_progress' | 'resolved';
  message: string;
  source: string;
  traceId: string;
  stackTrace?: string; // ?�세 ?�택 ?�레?�스 (가??
};

// ???�키�?(?�태 변경용)
const formSchema = z.object({
  status: z.enum(['open', 'in_progress', 'resolved']),
  adminNote: z.string().optional(),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. ?�태 관�?
// ==========================================
const columnFilters = ref([{ id: 'message', value: '' }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 }); // 로그????번에 많이 �?
const sorting = ref([{ id: 'timestamp', desc: true }]);

const levelFilter = ref('all');
const statusFilter = ref('all');
const isModalOpen = ref(false);
const selectedId = ref<number | null>(null);

// ?�세 ?�보 ?�시??
const currentLog = ref<ErrorLogItem | null>(null);

// ???�태
const initialFormState: FormSchema = { status: 'open', adminNote: '' };
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. ?�이???�칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<ErrorLogItem[]>(
  DATA_KEY,
  async () => {
    return Array.from({ length: 50 }).map((_, i) => {
      const level =
        i % 10 === 0 ? 'critical' : i % 3 === 0 ? 'warning' : 'error';
      const statuses: ErrorLogItem['status'][] = ['open', 'in_progress', 'resolved'];
      const status = statuses[i % statuses.length] ?? 'open';

      return {
        id: 5000 - i,
        timestamp: subMinutes(new Date(), i * 15).toISOString(),
        level: level,
        status: status,
        message:
          i % 10 === 0 ?
            '?�이?�베?�스 ?�결 ?�패' :
            `JSON ?�치 ${i}?�서 ?�상?��? 못한 ?�큰`,
        source: i % 2 === 0 ? '/api/auth/login' : '/components/Dashboard.vue',
        traceId: `trace-${Math.random().toString(36).substring(7)}`,
        stackTrace: `Error: ${i % 10 === 0 ? '?�이?�베?�스 ?�결 ?�패' : '?�상?��? 못한 ?�큰'}\n    at /app/server/api.ts:45:12\n    at async /app/server/handler.ts:22:5`,
      };
    });
  },
);

// ==========================================
// 5. ?�션 ?�들??
// ==========================================
function openDetailModal (row: ErrorLogItem) {
  currentLog.value = row;
  selectedId.value = row.id;
  Object.assign(formState, { status: row.status, adminNote: '' });
  isModalOpen.value = true;
}

async function onSubmit (event: FormSubmitEvent<FormSchema>) {
  if (currentLog.value) {
    currentLog.value.status = formState.status;
  }
  toast.add({
    title: 'Status Updated',
    description: 'Log status has been updated.',
    color: 'success',
  });
  isModalOpen.value = false;
}

function copyTraceId (traceId: string) {
  navigator.clipboard.writeText(traceId);
  toast.add({ title: 'Copied', description: 'Trace ID copied.' });
}

function getRowItems (row: ErrorLogItem) {
  return [
    { type: 'label', label: '관리' },
    {
      label: 'View detail',
      icon: 'i-lucide-search',
      onSelect: () => openDetailModal(row),
    },
    {
      label: 'Trace ID 복사',
      icon: 'i-lucide-copy',
      onSelect: () => copyTraceId(row.traceId),
    },
    { type: 'separator' },
    {
      label: 'Mark resolved',
      icon: 'i-lucide-check-circle',
      color: 'success',
      disabled: row.status === 'resolved',
      onSelect: () => {
        row.status = 'resolved';
        toast.add({ title: 'Done', color: 'success' });
      },
    },
  ];
}

// ==========================================
// 6. ?�이�?컬럼 ?�의
// ==========================================
const columnLabels: Record<string, string> = {
  select: '?�택',
  timestamp: '발생 ?�간',
  level: '심각도',
  message: '?�러 메시지',
  source: '?�치',
  status: '?�태',
  actions: '관리',
};

const columns: TableColumn<ErrorLogItem>[] = [
  {
    id: 'select',
    header: ({ table }) =>
      h(UCheckbox, {
        'modelValue': table.getIsSomePageRowsSelected() ?
          'indeterminate' :
          table.getIsAllPageRowsSelected(),
        'onUpdate:modelValue': (v: boolean) =>
          table.toggleAllPageRowsSelected(!!v),
      }),
    cell: ({ row }) =>
      h(UCheckbox, {
        'modelValue': row.getIsSelected(),
        'onUpdate:modelValue': (v: boolean) => row.toggleSelected(!!v),
      }),
    enableSorting: false,
  },
  {
    accessorKey: 'timestamp',
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(UButton, {
        color: 'neutral',
        variant: 'ghost',
        label: '발생 ?�간',
        icon:
          isSorted === 'asc' ?
            'i-lucide-arrow-up-narrow-wide' :
            isSorted === 'desc' ?
              'i-lucide-arrow-down-wide-narrow' :
              'i-lucide-arrow-up-down',
        class: '-ml-2.5 font-bold hover:bg-gray-100 dark:hover:bg-gray-800',
        onClick: () => column.toggleSorting(column.getIsSorted() === 'asc'),
      });
    },
    cell: ({ row }) =>
      format(new Date(row.original.timestamp), 'yyyy-MM-dd HH:mm:ss'),
  },
  {
    accessorKey: 'level',
    header: '심각도',
    cell: ({ row }) => {
      const map: Record<ErrorLogItem['level'], { label: string; color: 'error' | 'warning'; variant: 'solid' | 'subtle' }> = {
        critical: { label: 'CRITICAL', color: 'error', variant: 'solid' },
        error: { label: 'ERROR', color: 'error', variant: 'subtle' },
        warning: { label: 'WARN', color: 'warning', variant: 'subtle' },
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
    accessorKey: 'status',
    header: '?�태',
    cell: ({ row }) => {
      const map: Record<ErrorLogItem['status'], { label: string; color: 'error' | 'primary' | 'success' }> = {
        open: { label: '오픈', color: 'error' },
        in_progress: { label: '진행중', color: 'primary' },
        resolved: { label: '해결', color: 'success' },
      };
      const info = map[row.original.status];
      return h(
        UBadge,
        { color: info.color, variant: 'subtle' },
        () => info.label,
      );
    },
  },
  {
    accessorKey: 'message',
    header: '?�러 메시지',
    cell: ({ row }) =>
      h('div', { class: 'flex flex-col max-w-[300px]' }, [
        h(
          'span',
          {
            class:
              'truncate font-mono text-xs cursor-pointer hover:text-primary hover:underline',
            onClick: () => openDetailModal(row.original),
          },
          row.original.message,
        ),
        h(
          'span',
          { class: 'text-xs text-gray-400 font-mono truncate' },
          row.original.traceId,
        ),
      ]),
  },
  {
    accessorKey: 'source',
    header: '?�치',
    cell: ({ row }) =>
      h(
        'code',
        { class: 'text-xs bg-gray-100 dark:bg-gray-800 px-1 py-0.5 rounded' },
        row.original.source,
      ),
  },
  {
    id: 'actions',
    cell: ({ row }) =>
      h(
        'div',
        { class: 'text-right' },
        h(
          UDropdownMenu,
          {
            content: { align: 'end' },
            items: getRowItems(row.original),
          },
          () =>
            h(UButton, {
              icon: 'i-lucide-ellipsis-vertical',
              color: 'neutral',
              variant: 'ghost',
              class: 'ml-auto',
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
  if (!table.value?.tableApi) {
    return;
  }
  table.value.tableApi
    .getColumn('level')
    ?.setFilterValue(
      levelFilter.value === 'all' ? undefined : levelFilter.value,
    );
  table.value.tableApi
    .getColumn('status')
    ?.setFilterValue(
      statusFilter.value === 'all' ? undefined : statusFilter.value,
    );
});

const messageSearch = computed({
  get: () =>
    (table.value?.tableApi?.getColumn('message')?.getFilterValue() as string) ||
    '',
  set: val =>
    table.value?.tableApi
      ?.getColumn('message')
      ?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="messageSearch"
        icon="i-lucide-search"
        placeholder="?�러 메시지 검??.."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
        <UButton
          label="로그 ?�운로드"
          icon="i-lucide-download"
          color="neutral"
          variant="outline"
        />
        <USelect
          v-model="levelFilter"
          :items="[
            { label: '?�체 ?�급', value: 'all' },
            { label: 'Critical', value: 'critical' },
            { label: 'Error', value: 'error' },
            { label: 'Warning', value: 'warning' },
          ]"
          class="min-w-32"
        />
        <USelect
          v-model="statusFilter"
          :items="[
            { label: '?�체 ?�태', value: 'all' },
            { label: '?��?(Open)', value: 'open' },
            { label: '진행중', value: 'in_progress' },
            { label: '해결', value: 'resolved' },
          ]"
          class="min-w-32"
        />

        <UDropdownMenu
          :items="
            table?.tableApi
              ?.getAllColumns()
              .filter(c => c.getCanHide())
              .map(c => ({
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
            label="컬럼 ?�정"
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
        �?{{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}�?로그
        �?
        {{ table?.tableApi?.getFilteredSelectedRowModel().rows.length || 0 }}�?
        ?�택??
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
    title="?�러 로그 ?�세"
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

        <hr class="border-gray-200 dark:border-gray-700">

        <div class="grid grid-cols-2 gap-4">
          <UFormField label="처리 상태 변경" name="status" class="w-full">
            <USelect
              v-model="formState.status"
              :items="[
                { label: '?��?(Open)', value: 'open' },
                { label: '진행중(In Progress)', value: 'in_progress' },
                { label: '해결(Resolved)', value: 'resolved' },
              ]"
              class="w-full"
            />
          </UFormField>

          <UFormField label="관리자 메모" name="adminNote" class="w-full">
            <UInput
              v-model="formState.adminNote"
              placeholder="처리 ?�역 메모"
              class="w-full"
            />
          </UFormField>
        </div>

        <div class="flex justify-end gap-2 pt-2 mt-auto">
          <UButton
            label="?�기"
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
