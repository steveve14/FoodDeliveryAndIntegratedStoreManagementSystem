<script setup lang="ts">
/* eslint-disable @typescript-eslint/no-unused-vars, vue/no-multiple-template-root, @stylistic/max-statements-per-line */
/**
 * [?�수 ?�포??
 * Vue ?�심 기능 �?UI ?�이브러�??�?? ?�이�?코어 ?�수 ?�을 불러?�니??
 */
import { h, ref, reactive, resolveComponent, watch, computed } from 'vue';
import type { TableColumn, FormSubmitEvent } from '@nuxt/ui';

import * as z from 'zod'; // ?�효??검???�이브러�?
import { format } from 'date-fns'; // ?�짜 ?�맷??

// [중요] TanStack Table???�렬 �??�이지?�이??코어 ?�수
import { getPaginationRowModel } from '@tanstack/table-core';
import type { Row, Table } from '@tanstack/table-core';

// ==========================================
// 1. 컴포?�트 리졸�?(Nuxt UI 컴포?�트)
// ==========================================
// h() ?�수 ?�에???�용?�기 ?�해 컴포?�트�?미리 ?�결(resolve)?�니??
const UButton = resolveComponent('UButton');
const UBadge = resolveComponent('UBadge');
const UDropdownMenu = resolveComponent('UDropdownMenu');
const UCheckbox = resolveComponent('UCheckbox');
const UIcon = resolveComponent('UIcon');
const UAvatar = resolveComponent('UAvatar'); // [추�?] 리스?????��?지 ?�시??

const toast = useToast();
type TableInstance<T> = { tableApi?: Table<T> } | null;
type TableRow<T> = Row<T>;
const table = ref<TableInstance<NoticeItem>>(null); // ?�이�??�스?�스 참조

// ==========================================
// 2. ?�??�??�이??모델 ?�의
// ==========================================
const PAGE_TITLE = '공지사항 관리';
const DATA_KEY = 'notices';

// 공�??�항 ?�이???�???�의
type NoticeItem = {
  id: number;
  title: string;
  content: string;
  status: 'active' | 'inactive' | 'scheduled';
  category: 'general' | 'urgent' | 'event';
  createdAt: string;
  author: string;
  isPinned: boolean;
  imageUrl?: string; // [추�?] ?��?지 URL (?�셔??
};

// Zod�??�용?????�효??검???�키�?
const formSchema = z.object({
  title: z.string().min(2, '?�목?� 2글???�상 ?�력?�주?�요.'),
  category: z.enum(['general', 'urgent', 'event']).catch('general'),
  status: z.enum(['active', 'inactive', 'scheduled']).catch('active'),
  content: z.string().min(1, '?�용???�력?�주?�요.'),
  isPinned: z.boolean().default(false),
  imageUrl: z.string().optional(), // [추�?] ?��?지 ?�드 ?�효??
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. ?�태 관�?(State Management)
// ==========================================
// ?�이�?관???�태
const columnFilters = ref([{ id: 'title', value: '' }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });

// UI ?�태
const statusFilter = ref('all');
const isSlideoverOpen = ref(false);
const isEditMode = ref(false);
const selectedId = ref<number | null>(null);

// ???�태
const fileInput = ref<HTMLInputElement | null>(null); // ?�일 ?�풋 참조
const initialFormState: FormSchema = {
  title: '',
  category: 'general',
  status: 'active',
  content: '',
  isPinned: false,
  imageUrl: '',
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. ?�이???�칭 (Mock Data ?�성)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<NoticeItem[]>(
  DATA_KEY,
  async () => {
    // ?�덤 ?�이???�성 ?�퍼 ?�수
    function getRandom<T> (arr: readonly T[]): T | undefined {
      if (arr.length === 0) {
        return undefined;
      }

      return arr[Math.floor(Math.random() * arr.length)];
    }

    const categories = ['general', 'urgent', 'event'] as const;
    const statuses = ['active', 'inactive', 'scheduled'] as const;
    const authors = ['관리자', '운영팀', '김철수 매니저', 'CS팀'];
    const titles = [
      '개인?�보 처리방침 변�??�내',
      '?�벽 ?�기 ?�버 ?��? ?�내',
      '?�규 가?�자 ?�???�컴 쿠폰 증정',
      '?��? 결제 ?�스???�류 ?�정 ?�료',
      '추석 ?�휴 고객?�터 ?�영 ?�정 ?�내',
    ];

    // 50개의 ?��? ?�이???�성
    return Array.from({ length: 50 }).map((_, i) => {
      const category = getRandom(categories) ?? 'general';
      const status = getRandom(statuses) ?? 'active';

      return {
        id: 50 - i, // ID ??�� ?�성
        title: `[${category === 'urgent' ? '긴급' : '?�내'}] ${getRandom(titles)}`,
        content: '공�??�항 ?�세 ?�용?�니??',
        status: status,
        category: category,
        createdAt: new Date(
          Date.now() - Math.floor(Math.random() * 365 * 24 * 60 * 60 * 1000),
        ).toISOString(),
        author: getRandom(authors) ?? '관리자',
        isPinned: category === 'urgent' || Math.random() < 0.1,
        // [추�?] ??30% ?�률�??��?지 ?�함
        imageUrl:
          Math.random() > 0.7 ?
            `https://picsum.photos/seed/${i}/200/200` :
            undefined,
      };
    });
  },
);

// ==========================================
// 5. 비즈?�스 로직 & ?�들??
// ==========================================

// 모달(Slideover) ?�기 - ?�성 모드
function openCreateModal () {
  isEditMode.value = false;
  selectedId.value = null;
  Object.assign(formState, initialFormState); // ??초기??
  isSlideoverOpen.value = true;
}

// 모달(Slideover) ?�기 - ?�정 모드
function openEditModal (row: NoticeItem) {
  isEditMode.value = true;
  selectedId.value = row.id;
  // ?�택???�의 ?�이?�로 ??채우�?
  Object.assign(formState, {
    title: row.title,
    category: row.category,
    status: row.status,
    content: row.content,
    isPinned: row.isPinned,
    imageUrl: row.imageUrl,
  });
  isSlideoverOpen.value = true;
}

// [추�?] ?�일 ?�택 ??미리보기 URL ?�성
function onFileSelect (event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    // 브라?��? 메모리에 ?�시 URL ?�성 (?�무?�서???�로??API ?�출 ?�요)
    const previewUrl = URL.createObjectURL(file);
    formState.imageUrl = previewUrl;
  }
}

// [추�?] ?��?지 ??�� ?�들??
function removeImage () {
  formState.imageUrl = '';
  if (fileInput.value) { fileInput.value.value = ''; } // ?�풋 초기??
}

// ???�출 ?�들??
async function onSubmit (event: FormSubmitEvent<FormSchema>) {
  const actionName = isEditMode.value ? '?�정' : '?�록';
  toast.add({
    title: `${actionName} ?�료`,
    description: `?�공?�으�?${actionName}?�었?�니??`,
    color: 'success',
  });
  isSlideoverOpen.value = false;
}

// ??�� ?�들??
function onDelete (ids: number[]) {
  toast.add({
    title: '??�� ?�료',
    description: `${ids.length}개의 ??��????��?�었?�니??`,
    color: 'error',
  });
  rowSelection.value = {}; // ?�택 초기??
}

// ??Row) ?�션 메뉴 ?�이???�성
function getRowItems (row: NoticeItem) {
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
// 6. ?�이�?컬럼 ?�의 (Custom Rendering)
// ==========================================
const columnLabels: Record<string, string> = {
  select: '?�택',
  id: 'No.',
  category: '분류',
  title: '?�목',
  status: '?�태',
  author: '작성자',
  createdAt: '등록일',
  actions: '관리',
};

const columns: TableColumn<NoticeItem>[] = [
  // 1. 체크박스 컬럼
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
    enableSorting: false, // ?�렬 불필??
  },

  // 2. ID (No.) 컬럼 - [추�?] ?�렬 기능 ?�용
  {
    accessorKey: 'id',
    // ?�더�?커스?� ?�더링하???�릭 가?�한 버튼?�로 만듦
    header: ({ column }) => {
      const isSorted = column.getIsSorted(); // 'asc' | 'desc' | false
      return h(UButton, {
        color: 'neutral',
        variant: 'ghost',
        label: 'No.',
        // ?�렬 ?�태???�라 ?�이�?변�?
        icon:
          isSorted === 'asc' ?
            'i-lucide-arrow-up-narrow-wide' :
            isSorted === 'desc' ?
              'i-lucide-arrow-down-wide-narrow' :
              'i-lucide-arrow-up-down', // 기본 ?�태
        class: '-ml-2.5 font-bold hover:bg-gray-100 dark:hover:bg-gray-800',
        // ?�릭 ???�렬 ?��? (?�름차순 <-> ?�림차순)
        onClick: () => column.toggleSorting(column.getIsSorted() === 'asc'),
      });
    },
  },

  // 3. 분류 컬럼
  {
    accessorKey: 'category',
    header: '분류',
    cell: ({ row }) => {
      const map = { general: '일반', urgent: '긴급', event: '이벤트' };
      const color = {
        general: 'neutral',
        urgent: 'error',
        event: 'primary',
      } as const;
      return h(
        UBadge,
        { variant: 'subtle', color: color[row.original.category] },
        () => map[row.original.category],
      );
    },
  },

  // 4. ?�목 컬럼 - [추�?] ?��?지(Avatar) ?�시
  {
    accessorKey: 'title',
    header: '?�목',
    cell: ({ row }) =>
      h('div', { class: 'flex items-center gap-2' }, [
        // 고정?� ?�이�?
        row.original.isPinned &&
        h(UIcon, {
          name: 'i-lucide-pin',
          class: 'text-primary w-4 h-4 shrink-0',
        }),
        // [추�?] ?��?지가 ?�으�??�바?� ?�시
        row.original.imageUrl &&
        h(UAvatar, { src: row.original.imageUrl, size: '2xs' }),
        // ?�목 ?�스??
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

  // 5. ?�태 컬럼 - [?�정] ?�터 로직 개선
  {
    accessorKey: 'status',
    header: '?�태',
    // [중요] 기본 'includes' ?�??'equals'�??�용?�여 'active' 검????'inactive'가 ?�오지 ?�게 ??
    filterFn: 'equals',
    cell: ({ row }) => {
      const map = { active: '게시', inactive: '비공개', scheduled: '예약' };
      const color = {
        active: 'success',
        inactive: 'neutral',
        scheduled: 'warning',
      } as const;
      return h(
        UBadge,
        { variant: 'subtle', color: color[row.original.status] },
        () => map[row.original.status],
      );
    },
  },

  // 6. ?�성??
  {
    accessorKey: 'author',
    header: '작성자',
  },

  // 7. ?�록??
  {
    accessorKey: 'createdAt',
    header: '등록일',
    cell: ({ row }) => format(new Date(row.original.createdAt), 'yyyy-MM-dd'),
  },

  // 8. ?�션(?�보�? 컬럼
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

// ?�태 ?�터 ?�롭?�운 변�?감�? -> ?�이�?컬럼 ?�터 ?�용
watch(statusFilter, (val) => {
  if (!table.value?.tableApi) { return; }
  table.value.tableApi
    .getColumn('status')
    ?.setFilterValue(val === 'all' ? undefined : val);
});

// ?�목 검?�바 ?�방??바인??
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
          label="공�? ?�록"
          icon="i-lucide-plus"
          color="primary"
          @click="openCreateModal"
        />
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
                .rows.map((r: TableRow<NoticeItem>) => r.original.id),
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
            { label: '게시', value: 'active' },
            { label: '비공개', value: 'inactive' },
            { label: '예약', value: 'scheduled' },
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
    v-model:open="isSlideoverOpen"
    :title="isEditMode ? '공�??�항 ?�정' : '?�규 공�? ?�록'"
    :ui="{ wrapper: 'w-full sm:max-w-2xl' }"
  >
    <template #body>
      <UForm
        :schema="formSchema"
        :state="formState"
        class="space-y-4 p-4"
        @submit="onSubmit"
      >
        <UFormField label="?�목" name="title" required class="w-full">
          <UInput
            v-model="formState.title"
            placeholder="제목을 입력하세요"
            class="w-full"
          />
        </UFormField>

        <UFormField name="isPinned">
          <UCheckbox v-model="formState.isPinned" label="?�단 고정 (중요)" />
        </UFormField>

        <UFormField label="?�???��?지" name="imageUrl" class="w-full">
          <div class="flex flex-col gap-2">
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              class="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-primary-50 file:text-primary-700 hover:file:bg-primary-100"
              @change="onFileSelect"
            >
            <div
              v-if="formState.imageUrl"
              class="relative w-full mt-2 rounded-lg overflow-hidden border border-gray-200"
            >
              <img
                :src="formState.imageUrl"
                alt="Preview"
                class="w-full h-48 object-cover"
              >
              <UButton
                icon="i-lucide-x"
                color="gray"
                variant="solid"
                size="xs"
                class="absolute top-2 right-2"
                @click="removeImage"
              />
            </div>
          </div>
        </UFormField>

        <div class="grid grid-cols-2 gap-4">
          <UFormField label="분류" name="category" required class="w-full">
            <USelect
              v-model="formState.category"
              :items="['general', 'urgent', 'event']"
              class="w-full"
            />
          </UFormField>
          <UFormField label="게시 ?�태" name="status" required class="w-full">
            <USelect
              v-model="formState.status"
              :items="['active', 'inactive', 'scheduled']"
              class="w-full"
            />
          </UFormField>
        </div>

        <UFormField label="?�용" name="content" required class="w-full">
          <UTextarea
            v-model="formState.content"
            :rows="10"
            autoresize
            class="w-full"
            placeholder="공�? ?�용???�력?�세??"
          />
        </UFormField>

        <div
          class="flex justify-end gap-2 pt-4 border-t border-default mt-auto"
        >
          <UButton
            label="취소"
            color="neutral"
            variant="ghost"
            @click="isSlideoverOpen = false"
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
