<script setup lang="ts">
/**
 * [리뷰 관리]
 * Base Code 아키텍처 기반 리팩토링
 */
import { h, ref, reactive, resolveComponent, watch, computed } from 'vue';
import type { TableColumn, FormSubmitEvent } from '@nuxt/ui';

import * as z from 'zod';
import { format, subDays, subHours } from 'date-fns';
import { getPaginationRowModel } from '@tanstack/table-core';
import type { Row, Table } from '@tanstack/table-core';

// ==========================================
// 1. 컴포넌트 리졸브
// ==========================================
const UButton = resolveComponent('UButton');
const UBadge = resolveComponent('UBadge');
const UDropdownMenu = resolveComponent('UDropdownMenu');
const UCheckbox = resolveComponent('UCheckbox');
const UIcon = resolveComponent('UIcon');
const UAvatar = resolveComponent('UAvatar');

const toast = useToast();
type TableInstance<T> = { tableApi?: Table<T> } | null;
type TableRow<T> = Row<T>;
const table = ref<TableInstance<ReviewItem>>(null);

// ==========================================
// 2. ?�정 �??�이???�의
// ==========================================
const DATA_KEY = 'reviews';

type ReviewItem = {
  id: number;
  storeName: string;
  userName: string;
  rating: number; // 1 ~ 5
  content: string;
  reply?: string; // 사장님 답글
  status: 'active' | 'hidden'; // 게시중 / 숨김
  createdAt: string;
  imageUrl?: string; // 리뷰 ?�진
};

// 폼 스키마 (답글 및 상태 변경)
const formSchema = z.object({
  reply: z.string().optional(),
  status: z.enum(['active', 'hidden']),
  isHidden: z.boolean().default(false), // 체크박스 바인딩용
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. ?�태 관�?
// ==========================================
const columnFilters = ref([{ id: 'storeName', value: '' }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: 'id', desc: true }]);

const statusFilter = ref('all');
const isModalOpen = ref(false);
const selectedId = ref<number | null>(null);

// ?�세 ?�보 ?�시??(?�기 ?�용)
const currentReview = ref<ReviewItem | null>(null);

// ???�태
const initialFormState: FormSchema = {
  reply: '',
  status: 'active',
  isHidden: false,
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. ?�이???�칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<ReviewItem[]>(
  DATA_KEY,
  async () => {
    return Array.from({ length: 50 }).map((_, i) => {
      const rating = Math.floor(Math.random() * 5) + 1;
      const hasReply = i % 3 === 0;
      const isHidden = i % 10 === 0; // 10% ?�률�??��? 처리??

      return {
        id: 50 - i,
        storeName: `맛있??가�?${(i % 5) + 1}?�점`,
        userName: `Customer_${i}`,
        rating: rating,
        content:
          rating > 3 ?
            '?�말 맛있?�요! 배달??빠르�?최고?�니?? ?�주�??�사 100%!' :
            '?�식?� 괜찮?�??배달??조금 ??��?�요. ?�어???�습?�다.',
        status: isHidden ? 'hidden' : 'active',
        createdAt: subHours(subDays(new Date(), i % 7), i * 3).toISOString(),
        reply: hasReply ?
          '?�중??리뷰 감사?�니?? ???�력?�는 가게�? ?�겠?�니??' :
          undefined,
        imageUrl:
          Math.random() > 0.7 ?
            `https://picsum.photos/seed/${i}/200/200` :
            undefined,
      };
    });
  },
);

// ==========================================
// 5. ?�션 ?�들??
// ==========================================
function openDetailModal (row: ReviewItem) {
  currentReview.value = row;
  selectedId.value = row.id;

  Object.assign(formState, {
    reply: row.reply || '',
    status: row.status,
    isHidden: row.status === 'hidden',
  });
  isModalOpen.value = true;
}

async function onSubmit (_event: FormSubmitEvent<FormSchema>) {
  if (currentReview.value) {
    // Mock Data ?�데?�트
    currentReview.value.reply = formState.reply;
    currentReview.value.status = formState.isHidden ? 'hidden' : 'active';
  }

  toast.add({
    title: '저장 완료',
    description: '리뷰 ?�태 �??��????�?�되?�습?�다.',
    color: 'success',
  });
  isModalOpen.value = false;
}

function onDelete (ids: number[]) {
  toast.add({
    title: '??�� ?�료',
    description: `${ids.length}개의 리뷰가 ??��?�었?�니??`,
    color: 'error',
  });
  rowSelection.value = {};
}

function getRowItems (row: ReviewItem) {
  return [
    { type: 'label', label: '관리' },
    {
      label: '?�세 �??��?',
      icon: 'i-lucide-message-square',
      onSelect: () => openDetailModal(row),
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

// 별점 ?�더�??�퍼
function renderStars (rating: number) {
  return h('div', { class: 'flex items-center gap-0.5' }, [
    ...Array.from({ length: 5 }).map((_, i) =>
      h(UIcon, {
        name: 'i-lucide-star',
        class:
          i < rating ?
            'w-4 h-4 text-yellow-500 fill-current' :
            'w-4 h-4 text-gray-300',
      }),
    ),
  ]);
}

// ==========================================
// 6. ?�이�?컬럼 ?�의
// ==========================================
const columnLabels: Record<string, string> = {
  select: '?�택',
  id: 'No.',
  storeName: '매장명',
  rating: '?�점',
  content: '?�용',
  userName: '작성자',
  status: '?�태',
  createdAt: '생성일',
  actions: '관리',
};

const columns: TableColumn<ReviewItem>[] = [
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
    accessorKey: 'storeName',
    header: '매장명',
  },
  {
    accessorKey: 'rating',
    header: '?�점',
    cell: ({ row }) => renderStars(row.original.rating),
  },
  {
    accessorKey: 'content',
    header: '?�용',
    cell: ({ row }) =>
      h('div', { class: 'flex items-center gap-2 max-w-[300px]' }, [
        row.original.imageUrl &&
        h(UAvatar, { src: row.original.imageUrl, size: '2xs' }),
        h(
          'span',
          {
            class: 'truncate cursor-pointer hover:underline hover:text-primary',
            onClick: () => openDetailModal(row.original),
          },
          row.original.content,
        ),
      ]),
  },
  { accessorKey: 'userName', header: '작성자' },
  {
    accessorKey: 'status',
    header: '?�태',
    filterFn: (row, columnId, filterValue) => {
      if (filterValue === 'replied') {
        return !!row.original.reply;
      }
      if (filterValue === 'waiting') {
        return !row.original.reply;
      }
      if (filterValue === 'hidden') {
        return row.original.status === 'hidden';
      }
      return true;
    },
    cell: ({ row }) => {
      if (row.original.status === 'hidden') {
        return h(
          UBadge,
          { color: 'error', variant: 'subtle' },
          () => '블라인드 처리',
        );
      }
      return row.original.reply ?
        h(UBadge, { color: 'success', variant: 'subtle' }, () => '답변 완료') :
        h(UBadge, { color: 'warning', variant: 'subtle' }, () => '답변 대기');
    },
  },
  {
    accessorKey: 'createdAt',
    header: '생성일',
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
watch(statusFilter, (val) => {
  if (!table.value?.tableApi) {
    return;
  }
  // 커스?� ?�터 로직 ?�용???�해 status 컬럼??�??�달
  table.value.tableApi
    .getColumn('status')
    ?.setFilterValue(val === 'all' ? undefined : val);
});

const storeSearch = computed({
  get: () =>
    (table.value?.tableApi
      ?.getColumn('storeName')
      ?.getFilterValue() as string) || '',
  set: val =>
    table.value?.tableApi
      ?.getColumn('storeName')
      ?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="storeSearch"
        icon="i-lucide-search"
        placeholder="매장�?검??.."
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
                .rows.map((r: TableRow<ReviewItem>) => r.original.id),
            )
          "
        >
          <template #trailing>
            <UKbd>
              {{ table?.tableApi?.getFilteredSelectedRowModel().rows.length }}
            </UKbd>
          </template>
        </UButton>

        <USelect
          v-model="statusFilter"
          :items="[
            { label: '?�체 보기', value: 'all' },
            { label: '답변 대기', value: 'waiting' },
            { label: '답변 완료', value: 'replied' },
            { label: '블라인드 리뷰', value: 'hidden' },
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
    title="리뷰 상세 및 답변 관리"
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
          v-if="currentReview"
          class="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg space-y-3 border border-gray-100 dark:border-gray-700"
        >
          <div class="flex justify-between items-start">
            <div class="flex flex-col">
              <div class="flex items-center gap-2 mb-1">
                <span class="font-bold text-gray-900 dark:text-white">{{
                  currentReview.storeName
                }}</span>
                <span class="text-xs text-gray-500"
                  >| {{ currentReview.userName }}</span
                >
              </div>
              <div class="flex items-center gap-1">
                <UIcon
                  v-for="i in 5"
                  :key="i"
                  name="i-lucide-star"
                  :class="
                    i <= currentReview.rating
                      ? 'text-yellow-500 fill-current'
                      : 'text-gray-300'
                  "
                  class="w-4 h-4"
                />
                <span class="text-sm font-bold ml-1"
                  >{{ currentReview.rating }}.0</span
                >
              </div>
            </div>
            <div class="text-xs text-gray-500">
              {{
                format(new Date(currentReview.createdAt), "yyyy-MM-dd HH:mm")
              }}
            </div>
          </div>

          <hr class="border-gray-200 dark:border-gray-700/50">

          <p
            class="text-sm text-gray-700 dark:text-gray-300 whitespace-pre-wrap"
          >
            {{ currentReview.content }}
          </p>

          <div v-if="currentReview.imageUrl" class="mt-2">
            <img
              :src="currentReview.imageUrl"
              class="rounded-md border w-full max-h-60 object-cover"
            >
          </div>
        </div>

        <UFormField label="?�장???��?" name="reply" class="w-full">
          <UTextarea
            v-model="formState.reply"
            :rows="4"
            placeholder="고객?�께 감사???��????�겨주세??"
            autoresize
            class="w-full"
          />
        </UFormField>

        <div
          class="flex items-center justify-between border-t border-gray-100 dark:border-gray-800 pt-4"
        >
          <div class="flex flex-col">
            <span class="text-sm font-medium text-gray-900 dark:text-white"
              >리뷰 ?��?(Blind) 처리</span
            >
            <span class="text-xs text-gray-500"
              >부?�절??리뷰??경우 체크?�주?�요.</span
            >
          </div>
          <UCheckbox
            v-model="formState.isHidden"
            label="??리뷰�??��? 처리?�니??"
            color="error"
          />
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
  </div>
</template>
