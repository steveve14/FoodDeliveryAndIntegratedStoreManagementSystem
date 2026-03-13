<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui';
import { upperFirst } from 'scule';
import { getPaginationRowModel } from '@tanstack/table-core';
import type { Row } from '@tanstack/table-core';
import type { StoreDto, StoreStatus } from '~/types/api';
import {
  STORE_CATEGORY_FILTER_OPTIONS,
  getStoreCategoryLabel,
} from '~/utils/storeCategories';
import { useStoreApi } from '~/composables/api/useStoreApi';

type Store = StoreDto;
type LoadingStatus = 'idle' | 'pending' | 'success' | 'error';

// 2. 컴포넌트 수동 리졸브
const CompButton = resolveComponent('UButton');
const CompBadge = resolveComponent('UBadge');
const CompDropdownMenu = resolveComponent('UDropdownMenu');
const CompCheckbox = resolveComponent('UCheckbox');
const CompAvatar = resolveComponent('UAvatar');

type DashboardColumn = {
  id: string;
  getCanHide: () => boolean;
  getIsVisible: () => boolean;
  toggleVisibility: (visible: boolean) => void;
  setFilterValue?: (value: unknown) => void;
};

type DashboardTableApi = {
  getAllColumns: () => DashboardColumn[];
  getColumn?: (id: string) => DashboardColumn | undefined;
  getFilteredRowModel: () => { rows: unknown[] };
  getState: () => {
    pagination: {
      pageIndex: number;
      pageSize: number;
    };
  };
  setPageIndex: (pageIndex: number) => void;
};

type DashboardTableRef = {
  tableApi?: DashboardTableApi;
};

const toast = useToast();
const table = useTemplateRef<DashboardTableRef>('table');
const storeDetailOpen = ref(false);
const storeDetailLoading = ref(false);
const selectedStore = ref<Store | null>(null);

// 3. 상태 관리
const searchFilter = ref('');
const statusFilter = ref<'all' | StoreStatus | undefined>('all');
const locationFilter = ref('all');
const categoryFilter = ref('all');

const columnFilters = ref([{ id: 'name', value: '' }]);
const columnVisibility = ref();
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });

// 4. Backend API에서 매장 목록 조회
const { getStore, getStores } = useStoreApi();
const loadingStatus = ref<LoadingStatus>('idle');
const data = ref<Store[]>([]);

const STATUS_OPTIONS: { label: string; value: string }[] = [
  { label: '전체 상태', value: 'all' },
  { label: '영업중', value: 'OPEN' },
  { label: '영업종료', value: 'CLOSED' },
];

const statusMeta = (status: string | null | undefined) => {
  switch ((status ?? '').toUpperCase()) {
    case 'OPEN':
      return { color: 'success' as const, label: '영업중' };
    case 'CLOSED':
      return { color: 'neutral' as const, label: '영업종료' };
    default:
      return {
        color: 'warning' as const,
        label: upperFirst((status ?? '미정').toLowerCase()),
      };
  }
};

const locationOptions = computed(() => {
  const regions = Array.from(
    new Set(
      data.value
        .map(store => store.address?.trim().split(/\s+/)[0])
        .filter((region): region is string => Boolean(region)),
    ),
  ).sort((left, right) => left.localeCompare(right, 'ko'));

  return [
    { label: '전체 지역', value: 'all' },
    ...regions.map(region => ({ label: region, value: region })),
  ];
});

async function fetchStores () {
  loadingStatus.value = 'pending';
  try {
    const params = {
      category: categoryFilter.value === 'all' ? undefined : categoryFilter.value,
      status: statusFilter.value === 'all' ? undefined : statusFilter.value as StoreStatus,
    };
    const res = await getStores(params);
    data.value = res.success && res.data ? res.data : [];
    loadingStatus.value = 'success';
  } catch {
    loadingStatus.value = 'error';
    data.value = [];
  }
}
await fetchStores();

async function openStoreDetail (storeId: string) {
  storeDetailOpen.value = true;
  storeDetailLoading.value = true;
  selectedStore.value = null;

  try {
    const res = await getStore(storeId);
    if (!res.success || !res.data) {
      throw new Error();
    }
    selectedStore.value = res.data;
  } catch {
    toast.add({
      title: '매장 상세 조회 실패',
      description: '매장 정보를 불러오지 못했습니다.',
      color: 'error',
    });
    storeDetailOpen.value = false;
  } finally {
    storeDetailLoading.value = false;
  }
}

// 5. 유틸리티 함수
function formatCurrency (value: number) {
  return new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
  }).format(value);
}

// 컬럼 한글 매핑 (컬럼 설정 메뉴용)
const columnLabels: Record<string, string> = {
  select: '선택',
  id: 'ID',
  name: '매장명',
  ownerId: '점주',
  category: '업종',
  address: '주소',
  minOrderAmount: '최소주문',
  status: '상태',
  ratingAvg: '평점',
  actions: '관리',
};

const columnVisibilityItems = computed(() => {
  return (table.value?.tableApi?.getAllColumns() ?? [])
    .filter(column => column.getCanHide())
    .map(column => ({
      label: columnLabels[column.id] || column.id,
      type: 'checkbox' as const,
      checked: column.getIsVisible(),
      onUpdateChecked (checked: boolean) {
        table.value?.tableApi?.getColumn?.(column.id)?.toggleVisibility?.(!!checked);
      },
    }));
});

// 6. 행(Row) 액션 메뉴
function getRowItems (row: Row<Store>) {
  return [
    { type: 'label', label: '매장 관리' },
    {
      label: '매장 상세 정보',
      icon: 'i-lucide-store',
      onSelect: () => openStoreDetail(row.original.id),
    },
    {
      label: '메뉴 관리',
      icon: 'i-lucide-utensils',
      onSelect: () => navigateTo(`/stores/${row.original.id}/menus`),
    },
    {
      label: '새로고침',
      icon: 'i-lucide-refresh-cw',
      onSelect: () => fetchStores(),
    },
  ];
}

// 7. 테이블 컬럼 정의
const columns: TableColumn<Store>[] = [
  {
    id: 'select',
    header: ({ table }) =>
      h(CompCheckbox, {
        'modelValue': table.getIsSomePageRowsSelected() ?
          'indeterminate' :
          table.getIsAllPageRowsSelected(),
        'onUpdate:modelValue': (v: boolean) =>
          table.toggleAllPageRowsSelected(!!v),
        'ariaLabel': '전체 선택',
      }),
    cell: ({ row }) =>
      h(CompCheckbox, {
        'modelValue': row.getIsSelected(),
        'onUpdate:modelValue': (v: boolean) => row.toggleSelected(!!v),
        'ariaLabel': '행 선택',
      }),
  },
  {
    accessorKey: 'name',
    header: '매장 정보',
    cell: ({ row }) =>
      h('div', { class: 'flex items-center gap-3' }, [
        h(CompAvatar, { src: undefined, alt: row.original.name, size: 'sm' }),
        h('div', { class: 'flex flex-col' }, [
          h(
            'span',
            { class: 'font-medium text-highlighted' },
            row.original.name,
          ),
          h(
            'span',
            { class: 'text-xs text-muted' },
            getStoreCategoryLabel(row.original.category),
          ),
        ]),
      ]),
  },
  {
    accessorKey: 'ownerId',
    header: '점주',
    cell: ({ row }) => row.original.ownerId || '-',
  },
  {
    accessorKey: 'category',
    header: '업종',
    filterFn: 'equals',
    cell: ({ row }) =>
      h(
        CompBadge,
        { variant: 'subtle', color: 'neutral' },
        () => getStoreCategoryLabel(row.original.category),
      ),
  },
  {
    accessorKey: 'address',
    header: '주소',
    filterFn: 'includesString',
    cell: ({ row }) =>
      h('span', { class: 'text-sm' }, row.original.address || '-'),
  },
  {
    accessorKey: 'minOrderAmount',
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(CompButton, {
        color: 'neutral',
        variant: 'ghost',
        label: '최소주문',
        icon: isSorted ?
          isSorted === 'asc' ?
            'i-lucide-arrow-up' :
            'i-lucide-arrow-down' :
          'i-lucide-arrow-up-down',
        class: '-mx-2.5',
        onClick: () => column.toggleSorting(column.getIsSorted() === 'asc'),
      });
    },
    cell: ({ row }) =>
      h(
        'span',
        { class: 'font-mono' },
        formatCurrency(row.original.minOrderAmount || 0),
      ),
  },
  {
    accessorKey: 'status',
    header: '상태',
    filterFn: 'equals',
    cell: ({ row }) => {
      const status = statusMeta(row.original.status);

      return h(
        CompBadge,
        { variant: 'subtle', color: status.color, class: 'capitalize' },
        () => status.label,
      );
    },
  },
  {
    accessorKey: 'ratingAvg',
    header: '평점',
    cell: ({ row }) =>
      h(
        'span',
        {},
        row.original.ratingAvg ?
          `⭐ ${row.original.ratingAvg.toFixed(1)}` :
          '-',
      ),
  },
  {
    id: 'actions',
    cell: ({ row }) =>
      h(
        'div',
        { class: 'text-right' },
        h(
          CompDropdownMenu,
          { content: { align: 'end' }, items: getRowItems(row) },
          () =>
            h(CompButton, {
              icon: 'i-lucide-ellipsis-vertical',
              color: 'neutral',
              variant: 'ghost',
            }),
        ),
      ),
  },
];

// 8. 필터 로직 연결
watch(searchFilter, (val) => {
  // 매장명 검색
  const nameColumn = table.value?.tableApi?.getColumn?.('name');
  if (nameColumn?.setFilterValue) {
    nameColumn.setFilterValue(val);
  }
});

watch([statusFilter, categoryFilter], () => {
  fetchStores();
});

watch(locationFilter, (val) => {
  const addressColumn = table.value?.tableApi?.getColumn?.('address');
  if (addressColumn?.setFilterValue) {
    addressColumn.setFilterValue(val === 'all' ? undefined : val);
  }
});

watch(data, () => {
  const availableValues = new Set(locationOptions.value.map(option => option.value));
  if (!availableValues.has(locationFilter.value)) {
    locationFilter.value = 'all';
  }
}, { immediate: true });
</script>

<template>
  <UDashboardPanel id="stores">
    <template #header>
      <UDashboardNavbar title="매장 관리">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
        <template #right>
          <div class="flex items-center gap-2">
            <UButton
              label="새로고침"
              icon="i-lucide-refresh-cw"
              color="neutral"
              variant="outline"
              :loading="loadingStatus === 'pending'"
              @click="fetchStores"
            />
            <UButton label="매장 등록" icon="i-lucide-plus" color="primary" />
          </div>
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <UAlert
        v-if="loadingStatus === 'error'"
        color="error"
        variant="subtle"
        icon="i-lucide-alert-circle"
        title="매장 목록을 불러오지 못했습니다."
        description="잠시 후 다시 시도해주세요."
        class="mb-4"
      />

      <div class="flex flex-wrap items-center justify-between gap-1.5 mb-4">
        <UInput
          v-model="searchFilter"
          class="max-w-sm"
          icon="i-lucide-search"
          placeholder="매장명 검색..."
        />

        <div class="flex flex-wrap items-center gap-1.5">
          <USelect
            v-model="categoryFilter"
            :items="STORE_CATEGORY_FILTER_OPTIONS"
            class="min-w-36"
          />

          <USelect
            v-model="locationFilter"
            :items="locationOptions"
            class="min-w-32"
          />

          <USelect
            v-model="statusFilter"
            :items="STATUS_OPTIONS"
            class="min-w-32"
          />

          <UDropdownMenu
            :items="columnVisibilityItems"
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
        :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }"
        class="flex-1"
        :data="data"
        :columns="columns"
        :loading="loadingStatus === 'pending'"
        :ui="{
          base: 'table-fixed border-separate border-spacing-0',
          thead: '[&>tr]:bg-elevated/50',
          th: 'py-2 first:rounded-l-lg last:rounded-r-lg border-y border-default first:border-l last:border-r',
          td: 'border-b border-default',
          separator: 'h-0',
        }"
      />

      <div
        class="flex items-center justify-between gap-3 border-t border-default pt-4 mt-auto"
      >
        <div class="text-sm text-muted">
          총 {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}개
          매장 조회됨
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

      <UModal
        v-model:open="storeDetailOpen"
        title="매장 상세 정보"
        description="service-store 단건 조회 결과를 표시합니다."
      >
        <template #body>
          <div v-if="storeDetailLoading" class="flex justify-center py-8">
            <UIcon name="i-lucide-loader-circle" class="text-2xl animate-spin text-muted" />
          </div>

          <div v-else-if="selectedStore" class="space-y-4 text-sm">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-muted mb-0.5">매장명</p>
                <p class="font-medium">{{ selectedStore.name }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">상태</p>
                <UBadge :color="statusMeta(selectedStore.status).color" variant="subtle">
                  {{ statusMeta(selectedStore.status).label }}
                </UBadge>
              </div>
              <div>
                <p class="text-muted mb-0.5">업종</p>
                <p>{{ getStoreCategoryLabel(selectedStore.category) }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">최소주문금액</p>
                <p>{{ formatCurrency(selectedStore.minOrderAmount || 0) }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">점주 ID</p>
                <p class="font-mono text-xs">{{ selectedStore.ownerId || '-' }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">평점</p>
                <p>{{ selectedStore.ratingAvg ? `⭐ ${selectedStore.ratingAvg.toFixed(1)}` : '-' }}</p>
              </div>
            </div>

            <div>
              <p class="text-muted mb-0.5">주소</p>
              <p>{{ selectedStore.address || '-' }}</p>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-muted mb-0.5">전화번호</p>
                <p>{{ selectedStore.phone || '-' }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">영업시간</p>
                <p>{{ selectedStore.openingHours || '-' }}</p>
              </div>
            </div>

            <div>
              <p class="text-muted mb-0.5">설명</p>
              <p>{{ selectedStore.description || '-' }}</p>
            </div>
          </div>

          <div v-else class="text-center text-muted py-6">
            매장 정보를 불러올 수 없습니다.
          </div>
        </template>
      </UModal>
    </template>
  </UDashboardPanel>
</template>
