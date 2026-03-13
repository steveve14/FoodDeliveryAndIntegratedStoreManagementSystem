<script setup lang="ts">
/**
 * [1:1 문의 관�?
 * Base Code(공�??�항)???�키?�처�?기반?�로 리팩?�링??코드?�니??
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
const UAvatar = resolveComponent('UAvatar'); // 첨�? ?��?지 ?�시??

const toast = useToast();
type TableInstance<T> = { tableApi?: Table<T> } | null;
type TableRow<T> = Row<T>;
const table = ref<TableInstance<InquiryItem>>(null);

// ==========================================
// 2. ?�정 �??�이???�의
// ==========================================
const PAGE_TITLE = '1:1 문의 관리';
const DATA_KEY = 'inquiries';

type InquiryItem = {
  id: number;
  type: string;
  title: string;
  content: string;
  user: string;
  status: 'pending' | 'resolved';
  createdAt: string;
  answer?: string; // ?��? ?�용
  imageUrl?: string; // 첨�? ?��?지
};

// ???�키�?(?��? ?�록 ?�주)
const formSchema = z.object({
  title: z.string(),
  type: z.string(),
  user: z.string(),
  status: z.enum(['pending', 'resolved']),
  content: z.string(),
  answer: z.string().optional(), // ?��??� ?�택?�항 (?�기중?????�으므�?
  imageUrl: z.string().optional(),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. ?�태 관�?
// ==========================================
const columnFilters = ref([{ id: 'title', value: '' }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: 'id', desc: true }]); // 초기 ?�렬: 최신??

const statusFilter = ref('all');
const isSlideoverOpen = ref(false);
const selectedId = ref<number | null>(null);

// ???�태
const initialFormState: FormSchema = {
  title: '',
  type: '',
  user: '',
  status: 'pending',
  content: '',
  answer: '',
  imageUrl: '',
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. ?�이???�칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<InquiryItem[]>(
  DATA_KEY,
  async () => {
    const types = ['결제/?�불', '계정 ?�용', '?�스???�애', '기�? 문의'];
    const titles = [
      '결제가 중복?�로 ?�었?�니??',
      '로그인이 안돼요',
      '?�불 규정???�떻�??�나??',
      '?�이 ?�꾸 꺼집?�다.',
      '?�원 ?�퇴???�디???�나??',
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
        content: `문의?�항 ?�세 ?�용?�니?? (${randomTitle})\n빠른 ?�인 부?�드립니??`,
        status: isPending ? 'pending' : 'resolved',
        createdAt: date,
        answer: isPending ?
          '' :
          '문의주셔??감사?�니?? ?�당 ?�용?� 처리?�었?�니??',
        imageUrl:
          Math.random() > 0.8 ?
            `https://picsum.photos/seed/${i}/200/200` :
            undefined,
      };
    });
  },
);

// ==========================================
// 5. ?�션 ?�들??
// ==========================================
function openEditModal (row: InquiryItem) {
  selectedId.value = row.id;
  Object.assign(formState, {
    title: row.title,
    type: row.type,
    user: row.user,
    status: row.status,
    content: row.content,
    answer: row.answer || '',
    imageUrl: row.imageUrl,
  });
  isSlideoverOpen.value = true;
}

// ?��? ?�록/?�정 처리
async function onSubmit (event: FormSubmitEvent<FormSchema>) {
  // ?�제 로직: API ?�출?�여 ?��? ?�??�??�태 변�?
  if (formState.answer && formState.status === 'pending') {
    formState.status = 'resolved'; // ?��????�으�??�료 처리 (?�시)
  }
  toast.add({
    title: '?�???�료',
    description: '문의 ?�용???�정?�었?�니??',
    color: 'success',
  });
  isSlideoverOpen.value = false;
}

function onDelete (ids: number[]) {
  toast.add({
    title: '??�� ?�료',
    description: `${ids.length}개의 ??��????��?�었?�니??`,
    color: 'error',
  });
  rowSelection.value = {};
}

function getRowItems (row: InquiryItem) {
  return [
    { type: 'label', label: '관리' },
    {
      label: row.status === 'pending' ? '?��??�기' : '?�세보기',
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
  type: '?�형',
  title: '?�목',
  user: '작성자',
  status: '?�태',
  createdAt: '등록일',
  actions: '관리',
};

const columns: TableColumn<InquiryItem>[] = [
  // 1. 체크박스
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
  // 2. No. (?�렬 가??
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
  // 3. ?�형
  {
    accessorKey: 'type',
    header: '?�형',
    cell: ({ row }) =>
      h(UBadge, { variant: 'subtle', color: 'gray' }, () => row.original.type),
  },
  // 4. ?�목 (?��?지 ?�으�??�이�??�시)
  {
    accessorKey: 'title',
    header: '문의 ?�목',
    cell: ({ row }) =>
      h('div', { class: 'flex items-center gap-2' }, [
        // 첨�? ?��?지 ?�네??
        row.original.imageUrl &&
        h(UAvatar, { src: row.original.imageUrl, size: '2xs' }),
        h(
          'span',
          {
            class:
              'font-medium truncate max-w-[300px] cursor-pointer hover:text-primary hover:underline',
            onClick: () => openEditModal(row.original),
          },
          row.original.title,
        ),
      ]),
  },
  // 5. ?�성??
  { accessorKey: 'user', header: '작성자' },
  // 6. ?�태 (?�터 ?�용)
  {
    accessorKey: 'status',
    header: '?�태',
    filterFn: 'equals',
    cell: ({ row }) => {
      const isPending = row.original.status === 'pending';
      return h(
        UBadge,
        {
          variant: 'subtle',
          color: isPending ? 'warning' : 'success', // 주황: ?��? 초록: ?�료
        },
        () => (isPending ? '답변 대기' : '답변 완료'),
      );
    },
  },
  // 7. ?�록??
  {
    accessorKey: 'createdAt',
    header: '등록일',
    cell: ({ row }) =>
      format(new Date(row.original.createdAt), 'yyyy-MM-dd HH:mm'),
  },
  // 8. 관�?
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
watch(statusFilter, (val) => {
  if (!table.value?.tableApi) { return; }
  table.value.tableApi
    .getColumn('status')
    ?.setFilterValue(val === 'all' ? undefined : val);
});

const titleSearch = computed({
  get: () =>
    (table.value?.tableApi?.getColumn('title')?.getFilterValue() as string) ||
    '',
  set: val =>
    table.value?.tableApi?.getColumn('title')?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="titleSearch"
        icon="i-lucide-search"
        placeholder="?�목 검??.."
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
                .rows.map((r: TableRow<InquiryItem>) => r.original.id),
            )
          "
        >
          <template #trailing>
            <UKbd>
{{
              table?.tableApi?.getFilteredSelectedRowModel().rows.length
            }}
</UKbd>
          </template>
        </UButton>

        <USelect
          v-model="statusFilter"
          :items="[
            { label: '?�체 ?�태', value: 'all' },
            { label: '답변 대기', value: 'pending' },
            { label: '?��? ?�료', value: 'resolved' },
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

  <UModal v-model:open="isSlideoverOpen" title="문의 ?�세 �??��?">
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
              {{ formState.status === 'pending' ? '답변 대기' : '답변 완료' }}
            </UBadge>
            <span class="text-sm text-gray-500"
              >{{ formState.type }} | {{ formState.user }}</span
            >
          </div>
          <h3 class="font-bold text-lg">{{ formState.title }}</h3>
          <p class="text-sm whitespace-pre-wrap">{{ formState.content }}</p>

          <div v-if="formState.imageUrl" class="mt-2">
            <p class="text-xs text-gray-500 mb-1">첨�? ?��?지:</p>
            <img
              :src="formState.imageUrl"
              class="rounded border w-full max-h-48 object-cover"
            >
          </div>
        </div>

        <hr class="border-gray-200 dark:border-gray-700">

        <UFormField label="?��? ?�용" name="answer" required class="w-full">
          <UTextarea
            v-model="formState.answer"
            :rows="8"
            placeholder="?��????�력?�주?�요."
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
              label="?��? ?�료 처리"
            />
          </UFormField>
        </div>

        <div
          class="flex justify-end gap-2 pt-4 border-t border-default mt-auto"
        >
          <UButton
            label="?�기"
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
