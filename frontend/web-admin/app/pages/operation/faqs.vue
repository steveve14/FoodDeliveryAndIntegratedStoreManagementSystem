<script setup lang="ts">
/* eslint-disable @typescript-eslint/no-unused-vars, vue/no-multiple-template-root, @stylistic/max-statements-per-line */
/**
 * [FAQ 관�?
 * Base Code ?�키?�처 기반 리팩?�링
 */
import { h, ref, reactive, resolveComponent, watch, computed } from 'vue';
import type { TableColumn, FormSubmitEvent } from '@nuxt/ui';

import * as z from 'zod';
import { format, subDays, subHours } from 'date-fns';
import { getPaginationRowModel } from '@tanstack/table-core';
import type { Row, Table } from '@tanstack/table-core';

// ==========================================
// 1. 컴포?�트 리졸�?
// ==========================================
const UButton = resolveComponent('UButton');
const UBadge = resolveComponent('UBadge');
const UDropdownMenu = resolveComponent('UDropdownMenu');
const UCheckbox = resolveComponent('UCheckbox');
const UIcon = resolveComponent('UIcon');
const UTextarea = resolveComponent('UTextarea');

const toast = useToast();
type TableInstance<T> = { tableApi?: Table<T> } | null;
type TableRow<T> = Row<T>;
const table = ref<TableInstance<FaqItem>>(null);

// ==========================================
// 2. ?�정 �??�이???�의
// ==========================================
const PAGE_TITLE = 'FAQ 관리';
const DATA_KEY = 'faqs';

type FaqItem = {
  id: number;
  category: 'member' | 'payment' | 'shipping' | 'etc';
  question: string;
  answer: string;
  viewCount: number;
  isPublished: boolean;
  createdAt: string;
};

// ???�키�?
const formSchema = z.object({
  category: z.enum(['member', 'payment', 'shipping', 'etc']).catch('etc'),
  question: z.string().min(2, '질문???�력?�주?�요.'),
  answer: z.string().min(5, '?��? ?�용???�력?�주?�요.'),
  isPublished: z.boolean().default(true),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. ?�태 관�?
// ==========================================
const columnFilters = ref([{ id: 'question', value: '' }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: 'id', desc: true }]);

const categoryFilter = ref('all');
const isModalOpen = ref(false);
const isEditMode = ref(false);
const selectedId = ref<number | null>(null);

// ???�태
const initialFormState: FormSchema = {
  category: 'member',
  question: '',
  answer: '',
  isPublished: true,
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. ?�이???�칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<FaqItem[]>(
  DATA_KEY,
  async () => {
    const categories = ['member', 'payment', 'shipping', 'etc'] as const;

    return Array.from({ length: 50 }).map((_, i) => {
      const category = categories[i % 4] as
        | 'member' |
        'payment' |
        'shipping' |
        'etc';

      return {
        id: 50 - i,
        category: category,
        question: `?�주 묻는 질문 ?�시?�니??(${i + 1})?`,
        answer: '?�당 질문???�???�세 ?��? ?�용?�니??\n줄바꿈이 ?�함???�시 ?�스?�입?�다.',
        viewCount: Math.floor(Math.random() * 2000),
        isPublished: i % 10 !== 0, // 10% ?�률�?비공�?
        createdAt: subDays(new Date(), i).toISOString(),
      };
    });
  },
);

// ==========================================
// 5. ?�션 ?�들??
// ==========================================
function openCreateModal () {
  isEditMode.value = false;
  selectedId.value = null;
  Object.assign(formState, initialFormState);
  isModalOpen.value = true;
}

function openEditModal (row: FaqItem) {
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

async function onSubmit (event: FormSubmitEvent<FormSchema>) {
  const action = isEditMode.value ? '?�정' : '?�록';
  toast.add({
    title: `${action} ?�료`,
    description: `FAQ가 ?�공?�으�?${action}?�었?�니??`,
    color: 'success',
  });
  isModalOpen.value = false;
}

function onDelete (ids: number[]) {
  toast.add({
    title: '??�� ?�료',
    description: `${ids.length}개의 ??��????��?�었?�니??`,
    color: 'error',
  });
  rowSelection.value = {};
}

function getRowItems (row: FaqItem) {
  return [
    { type: 'label', label: '관리' },
    {
      label: '?�정?�기',
      icon: 'i-lucide-edit',
      onSelect: () => openEditModal(row),
    },
    { type: 'separator' },
    {
      label: '??��',
      icon: 'i-lucide-trash',
      color: 'error',
      onSelect: () => onDelete([row.id]),
    },
  ];
}

// ==========================================
// 6. ?�이�?컬럼 ?�의
// ==========================================
const columnLabels: Record<string, string> = {
  select: '?�택',
  id: 'No.',
  category: '카테고리',
  question: '질문',
  viewCount: '조회수',
  isPublished: '?�태',
  createdAt: '등록일',
  actions: '관리',
};

const columns: TableColumn<FaqItem>[] = [
  {
    id: 'select',
    header: ({ table }) =>
      h(UCheckbox, {
        'modelValue': table.getIsSomePageRowsSelected() ?
          'indeterminate' :
          table.getIsAllPageRowsSelected(),
        'onUpdate:modelValue': (v: boolean) =>
          table.toggleAllPageRowsSelected(!!v),
        'ariaLabel': '?�체 ?�택',
      }),
    cell: ({ row }) =>
      h(UCheckbox, {
        'modelValue': row.getIsSelected(),
        'onUpdate:modelValue': (v: boolean) => row.toggleSelected(!!v),
        'ariaLabel': '???�택',
      }),
    enableSorting: false,
  },
  {
    accessorKey: 'id',
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(UButton, {
        color: 'neutral',
        variant: 'ghost',
        label: 'No.',
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
  },
  {
    accessorKey: 'category',
    header: '카테고리',
    cell: ({ row }) => {
      const map: Record<string, string> = {
        member: '?�원/계정',
        payment: '결제/?�불',
        shipping: '배송/?�배',
        etc: '기�?',
      };
      return h(
        UBadge,
        { variant: 'subtle', color: 'neutral' },
        () => map[row.original.category],
      );
    },
  },
  {
    accessorKey: 'question',
    header: '질문',
    cell: ({ row }) =>
      h(
        'span',
        {
          class:
            'font-medium cursor-pointer hover:underline hover:text-primary',
          onClick: () => openEditModal(row.original),
        },
        row.original.question,
      ),
  },
  {
    accessorKey: 'viewCount',
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(UButton, {
        color: 'neutral',
        variant: 'ghost',
        label: '조회수',
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
    cell: ({ row }) => row.original.viewCount.toLocaleString(),
  },
  {
    accessorKey: 'isPublished',
    header: '?�태',
    cell: ({ row }) =>
      h(
        UBadge,
        {
          color: row.original.isPublished ? 'success' : 'neutral',
          variant: 'subtle',
          size: 'xs',
        },
        () => (row.original.isPublished ? '게시' : '비공개'),
      ),
  },
  {
    accessorKey: 'createdAt',
    header: '등록일',
    cell: ({ row }) => format(new Date(row.original.createdAt), 'yyyy-MM-dd'),
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
watch(categoryFilter, (val) => {
  if (!table.value?.tableApi) { return; }
  table.value.tableApi
    .getColumn('category')
    ?.setFilterValue(val === 'all' ? undefined : val);
});

const questionSearch = computed({
  get: () =>
    (table.value?.tableApi
      ?.getColumn('question')
      ?.getFilterValue() as string) || '',
  set: val =>
    table.value?.tableApi
      ?.getColumn('question')
      ?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="questionSearch"
        icon="i-lucide-search"
        placeholder="질문 검??.."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
        <UButton
          v-if="table?.tableApi?.getFilteredSelectedRowModel().rows.length"
          label="?�택 ??��"
          color="error"
          variant="subtle"
          icon="i-lucide-trash"
          @click="
            onDelete(
              table?.tableApi
                ?.getFilteredSelectedRowModel()
                .rows.map((r: TableRow<FaqItem>) => r.original.id),
            )
          "
        >
          <template #trailing
            >
<UKbd>
{{
              table?.tableApi?.getFilteredSelectedRowModel().rows.length
            }}
</UKbd>
</template
          >
        </UButton>

        <USelect
          v-model="categoryFilter"
          :items="[
            { label: '?�체 카테고리', value: 'all' },
            { label: '?�원/계정', value: 'member' },
            { label: '결제/?�불', value: 'payment' },
            { label: '배송/?�배', value: 'shipping' },
            { label: '기�?', value: 'etc' },
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
        �?{{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}�?�?
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
    :title="isEditMode ? 'FAQ ?�정' : 'FAQ ?�록'"
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
                { label: '?�원/계정', value: 'member' },
                { label: '결제/?�불', value: 'payment' },
                { label: '배송/?�배', value: 'shipping' },
                { label: '기�?', value: 'etc' },
              ]"
              class="w-full"
            />
          </UFormField>

          <div class="flex items-end pb-2">
            <UCheckbox
              v-model="formState.isPublished"
              label="공개 ?�태�??�록"
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
            placeholder="?�주 묻는 질문???�력?�세??"
            class="w-full"
          />
        </UFormField>

        <UFormField label="?��? (Answer)" name="answer" required class="w-full">
          <UTextarea
            v-model="formState.answer"
            :rows="8"
            placeholder="?��? ?�용???�력?�세??"
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
            :label="isEditMode ? '?�정 ?�료' : '?�록?�기'"
            color="primary"
          />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
