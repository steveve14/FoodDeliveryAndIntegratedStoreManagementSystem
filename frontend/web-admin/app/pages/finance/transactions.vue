<script setup lang="ts">
import { h, ref, resolveComponent, computed } from 'vue';
import type { TableColumn } from '@nuxt/ui';
import { format } from 'date-fns';
import { getPaginationRowModel } from '@tanstack/table-core';
import type { OrderDto } from '~/types/api';
import { useAdminHomeSource } from '~/composables/useAdminHomeSource';

const UButton = resolveComponent('UButton');
const UBadge = resolveComponent('UBadge');
const UDropdownMenu = resolveComponent('UDropdownMenu');

type DashboardColumn = {
  id: string;
  getCanHide: () => boolean;
  getIsVisible: () => boolean;
  toggleVisibility: (visible: boolean) => void;
};

type DashboardTableApi = {
  getAllColumns: () => DashboardColumn[];
  getColumn?: (id: string) => DashboardColumn | undefined;
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

type TransactionType = 'payment' | 'refund' | 'settlement';
type TransactionStatus = 'success' | 'pending' | 'cancelled';

type TransactionItem = {
  id: string;
  orderId: string;
  storeId: string;
  storeName: string;
  userId: string;
  userEmail: string;
  type: TransactionType;
  amount: number;
  status: TransactionStatus;
  description: string;
  createdAt: string;
  note: string;
};

const toast = useToast();
const table = ref<DashboardTableRef | null>(null);

const { data: source, refresh, status: loadingStatus } = await useAdminHomeSource();

const searchFilter = ref('');
const typeFilter = ref<'all' | TransactionType>('all');
const statusFilter = ref<'all' | TransactionStatus>('all');
const columnVisibility = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const isModalOpen = ref(false);
const currentTx = ref<TransactionItem | null>(null);

const currencyFormatter = new Intl.NumberFormat('ko-KR', {
  style: 'currency',
  currency: 'KRW',
  maximumFractionDigits: 0,
});

const customerEmailById = computed(() => {
  return Object.fromEntries(
    (source.value?.customers ?? []).map(customer => [customer.id, customer.email]),
  );
});

const storeNameById = computed(() => {
  return Object.fromEntries(
    (source.value?.stores ?? []).map(store => [store.id, store.name]),
  );
});

function buildTransaction (order: OrderDto): TransactionItem {
  const storeName = storeNameById.value[order.storeId] ?? order.storeId;
  const userEmail = customerEmailById.value[order.userId] ?? order.userId;

  if (order.status === 'CANCELLED') {
    return {
      id: `refund-${order.id}`,
      orderId: order.id,
      storeId: order.storeId,
      storeName,
      userId: order.userId,
      userEmail,
      type: 'refund',
      amount: -Math.abs(order.totalAmount),
      status: 'cancelled',
      description: `${storeName} 주문 취소 환불`,
      createdAt: order.createdAt,
      note: '주문 서비스의 취소 상태를 기준으로 환불 후보 건으로 표시합니다.',
    };
  }

  if (order.status === 'DONE') {
    return {
      id: `settlement-${order.id}`,
      orderId: order.id,
      storeId: order.storeId,
      storeName,
      userId: order.userId,
      userEmail,
      type: 'settlement',
      amount: order.totalAmount,
      status: 'success',
      description: `${storeName} 주문 정산 완료`,
      createdAt: order.createdAt,
      note: '정산 전용 원천 API가 없어 완료 주문 금액을 기준으로 정산 완료 건으로 집계합니다.',
    };
  }

  return {
    id: `payment-${order.id}`,
    orderId: order.id,
    storeId: order.storeId,
    storeName,
    userId: order.userId,
    userEmail,
    type: 'payment',
    amount: order.totalAmount,
    status: 'pending',
    description: `${storeName} 주문 결제 진행중`,
    createdAt: order.createdAt,
    note: '완료 전 주문은 결제 진행중 건으로 표시합니다.',
  };
}

const allTransactions = computed<TransactionItem[]>(() => {
  return (source.value?.orders ?? [])
    .map(buildTransaction)
    .sort((left, right) => new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime());
});

const summary = computed(() => {
  const transactions = allTransactions.value;
  return {
    total: transactions.length,
    paymentAmount: transactions
      .filter(transaction => transaction.type === 'payment')
      .reduce((sum, transaction) => sum + transaction.amount, 0),
    refundAmount: transactions
      .filter(transaction => transaction.type === 'refund')
      .reduce((sum, transaction) => sum + Math.abs(transaction.amount), 0),
    settlementAmount: transactions
      .filter(transaction => transaction.type === 'settlement')
      .reduce((sum, transaction) => sum + transaction.amount, 0),
  };
});

const filteredTransactions = computed<TransactionItem[]>(() => {
  const keyword = searchFilter.value.trim().toLowerCase();

  return allTransactions.value.filter((transaction) => {
    if (typeFilter.value !== 'all' && transaction.type !== typeFilter.value) {
      return false;
    }

    if (statusFilter.value !== 'all' && transaction.status !== statusFilter.value) {
      return false;
    }

    if (!keyword) {
      return true;
    }

    return [
      transaction.id,
      transaction.orderId,
      transaction.storeName,
      transaction.userEmail,
      transaction.description,
    ].some(value => value.toLowerCase().includes(keyword));
  });
});

const columnLabels: Record<string, string> = {
  id: '거래번호',
  type: '구분',
  description: '내용',
  amount: '금액',
  status: '상태',
  createdAt: '일시',
  actions: '관리',
};

const columnVisibilityItems = computed(() => {
  return (table.value?.tableApi?.getAllColumns() ?? [])
    .filter(column => column.getCanHide())
    .map(column => ({
      label: columnLabels[column.id] || column.id,
      type: 'checkbox' as const,
      checked: column.getIsVisible(),
      onUpdateChecked: (checked: boolean) =>
        table.value?.tableApi?.getColumn?.(column.id)?.toggleVisibility?.(checked),
      onSelect: (event: Event) => event.preventDefault(),
    }));
});

function typeMeta (type: TransactionType) {
  return {
    payment: { label: '결제', color: 'primary' as const },
    refund: { label: '환불', color: 'error' as const },
    settlement: { label: '정산', color: 'success' as const },
  }[type];
}

function statusMeta (status: TransactionStatus) {
  return {
    success: { label: '성공', color: 'success' as const },
    pending: { label: '진행중', color: 'warning' as const },
    cancelled: { label: '취소', color: 'neutral' as const },
  }[status];
}

function openDetailModal (transaction: TransactionItem) {
  currentTx.value = transaction;
  isModalOpen.value = true;
}

function getRowItems (transaction: TransactionItem) {
  return [
    { type: 'label', label: '조회' },
    {
      label: '상세 보기',
      icon: 'i-lucide-file-text',
      onSelect: () => openDetailModal(transaction),
    },
    {
      label: '주문 관리로 이동',
      icon: 'i-lucide-arrow-right',
      onSelect: () => navigateTo('/operation/orders'),
    },
  ];
}

function escapeCsvValue (value: string | number) {
  const stringValue = String(value).replaceAll('"', '""');
  return `"${stringValue}"`;
}

function onExport () {
  if (!import.meta.client) {
    return;
  }

  const rows = filteredTransactions.value;
  if (!rows.length) {
    toast.add({
      title: '내보낼 데이터가 없습니다.',
      description: '필터 조건을 다시 확인해주세요.',
      color: 'warning',
    });
    return;
  }

  const header = ['거래번호', '주문번호', '구분', '상태', '가게', '고객', '금액', '생성일시', '메모'];
  const csv = [
    header.map(escapeCsvValue).join(','),
    ...rows.map(row => [
      row.id,
      row.orderId,
      typeMeta(row.type).label,
      statusMeta(row.status).label,
      row.storeName,
      row.userEmail,
      row.amount,
      row.createdAt,
      row.note,
    ].map(escapeCsvValue).join(',')),
  ].join('\n');

  const blob = new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = `finance-transactions-${format(new Date(), 'yyyyMMdd-HHmmss')}.csv`;
  anchor.click();
  URL.revokeObjectURL(url);
}

// Some Nuxt UI table wrappers expose optional helpers dynamically.
type DashboardTableApiExtended = DashboardTableApi & {
  getColumn?: (id: string) => {
    toggleVisibility?: (visible: boolean) => void;
  } | undefined;
};

if (table.value?.tableApi) {
  (table.value.tableApi as DashboardTableApiExtended).getColumn =
    (table.value.tableApi as DashboardTableApiExtended).getColumn;
}

const columns: TableColumn<TransactionItem>[] = [
  {
    accessorKey: 'id',
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(UButton, {
        color: 'neutral',
        variant: 'ghost',
        label: '거래번호',
        icon:
          isSorted === 'asc' ?
            'i-lucide-arrow-up-narrow-wide' :
            isSorted === 'desc' ?
              'i-lucide-arrow-down-wide-narrow' :
              'i-lucide-arrow-up-down',
        class: '-ml-2.5',
        onClick: () => column.toggleSorting(column.getIsSorted() === 'asc'),
      });
    },
    cell: ({ row }) => h('span', { class: 'font-mono text-xs' }, row.original.id),
  },
  {
    accessorKey: 'type',
    header: '구분',
    cell: ({ row }) => {
      const info = typeMeta(row.original.type);
      return h(UBadge, { color: info.color, variant: 'subtle', size: 'xs' }, () => info.label);
    },
  },
  {
    accessorKey: 'description',
    header: '내용',
    cell: ({ row }) =>
      h('div', { class: 'flex flex-col max-w-[280px]' }, [
        h(
          'button',
          {
            type: 'button',
            class: 'truncate text-left font-medium hover:underline hover:text-primary',
            onClick: () => openDetailModal(row.original),
          },
          row.original.description,
        ),
        h('span', { class: 'text-xs text-muted' }, `${row.original.storeName} · ${row.original.userEmail}`),
      ]),
  },
  {
    accessorKey: 'amount',
    header: () => h('div', { class: 'text-right' }, '금액'),
    cell: ({ row }) => {
      const colorClass =
        row.original.type === 'refund' ?
          'text-error' :
          row.original.type === 'settlement' ?
            'text-success' :
            'text-highlighted';

      return h(
        'div',
        { class: `text-right font-semibold ${colorClass}` },
        currencyFormatter.format(row.original.amount),
      );
    },
  },
  {
    accessorKey: 'status',
    header: '상태',
    cell: ({ row }) => {
      const info = statusMeta(row.original.status);
      return h(UBadge, { color: info.color, variant: 'subtle', size: 'xs' }, () => info.label);
    },
  },
  {
    accessorKey: 'createdAt',
    header: '일시',
    cell: ({ row }) => format(new Date(row.original.createdAt), 'yyyy-MM-dd HH:mm'),
  },
  {
    id: 'actions',
    header: '관리',
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
  },
];
</script>

<template>
  <div>
    <div class="flex-1 flex flex-col">
    <UAlert
      v-if="loadingStatus === 'error'"
      color="error"
      variant="subtle"
      icon="i-lucide-alert-circle"
      title="거래 집계 데이터를 불러오지 못했습니다."
      description="주문/가게 집계 중 일부 요청이 실패했을 수 있습니다. 새로고침 후 다시 확인해주세요."
      class="mb-4"
    />

    <UAlert
      color="neutral"
      variant="subtle"
      icon="i-lucide-info"
      title="주문 기반 파생 뷰"
      description="정산 전용 백엔드 API가 없어 주문 상태를 기준으로 결제·환불·정산 항목을 읽기 전용으로 구성했습니다."
      class="mb-4"
    />

    <div class="grid gap-3 md:grid-cols-3 mb-4">
      <UCard>
        <p class="text-xs text-muted mb-1">결제 진행 금액</p>
        <p class="text-2xl font-semibold">{{ currencyFormatter.format(summary.paymentAmount) }}</p>
      </UCard>
      <UCard>
        <p class="text-xs text-muted mb-1">환불 후보 금액</p>
        <p class="text-2xl font-semibold text-error">{{ currencyFormatter.format(summary.refundAmount) }}</p>
      </UCard>
      <UCard>
        <p class="text-xs text-muted mb-1">정산 완료 금액</p>
        <p class="text-2xl font-semibold text-success">{{ currencyFormatter.format(summary.settlementAmount) }}</p>
      </UCard>
    </div>

    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="searchFilter"
        icon="i-lucide-search"
        placeholder="거래번호, 주문번호, 가게명, 고객 이메일 검색"
        class="max-w-md"
      />

      <div class="flex flex-wrap items-center gap-2">
        <UButton
          label="새로고침"
          icon="i-lucide-refresh-cw"
          color="neutral"
          variant="outline"
          :loading="loadingStatus === 'pending'"
          @click="refresh"
        />
        <UButton
          label="CSV 다운로드"
          icon="i-lucide-download"
          color="neutral"
          variant="outline"
          @click="onExport"
        />
        <USelect
          v-model="typeFilter"
          :items="[
            { label: '전체 구분', value: 'all' },
            { label: '결제', value: 'payment' },
            { label: '환불', value: 'refund' },
            { label: '정산', value: 'settlement' },
          ]"
          class="min-w-32"
        />
        <USelect
          v-model="statusFilter"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '성공', value: 'success' },
            { label: '진행중', value: 'pending' },
            { label: '취소', value: 'cancelled' },
          ]"
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
        v-model:column-visibility="columnVisibility"
        v-model:pagination="pagination"
        :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }"
        :data="filteredTransactions"
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

      <div class="flex items-center justify-between gap-3 border-t border-default pt-4 mt-auto">
        <div class="text-sm text-muted">
          총 {{ filteredTransactions.length }}건의 거래 항목
        </div>
        <UPagination
          :default-page="(table?.tableApi?.getState().pagination.pageIndex || 0) + 1"
          :items-per-page="table?.tableApi?.getState().pagination.pageSize"
          :total="filteredTransactions.length"
          @update:page="(page: number) => table?.tableApi?.setPageIndex(page - 1)"
        />
      </div>
    </div>

    <UModal
      v-model:open="isModalOpen"
      title="거래 상세 정보"
      :ui="{ wrapper: 'w-full sm:max-w-2xl' }"
    >
      <template #body>
        <div v-if="currentTx" class="space-y-4 p-4 text-sm">
          <div class="flex items-start justify-between gap-4">
            <div>
              <p class="text-xs text-muted font-mono">{{ currentTx.id }}</p>
              <p class="text-lg font-semibold mt-1">{{ currentTx.description }}</p>
            </div>
            <UBadge :color="statusMeta(currentTx.status).color" variant="subtle">
              {{ statusMeta(currentTx.status).label }}
            </UBadge>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-xs text-muted">주문 ID</p>
              <p class="font-mono text-xs mt-1">{{ currentTx.orderId }}</p>
            </div>
            <div>
              <p class="text-xs text-muted">거래 구분</p>
              <p class="mt-1">{{ typeMeta(currentTx.type).label }}</p>
            </div>
            <div>
              <p class="text-xs text-muted">가게</p>
              <p class="mt-1">{{ currentTx.storeName }}</p>
            </div>
            <div>
              <p class="text-xs text-muted">고객</p>
              <p class="mt-1">{{ currentTx.userEmail }}</p>
            </div>
            <div>
              <p class="text-xs text-muted">금액</p>
              <p class="mt-1 text-base font-semibold">{{ currencyFormatter.format(currentTx.amount) }}</p>
            </div>
            <div>
              <p class="text-xs text-muted">생성 시각</p>
              <p class="mt-1">{{ format(new Date(currentTx.createdAt), 'yyyy-MM-dd HH:mm:ss') }}</p>
            </div>
          </div>

          <UAlert
            color="neutral"
            variant="subtle"
            icon="i-lucide-info"
            title="집계 기준 메모"
            :description="currentTx.note"
          />

          <div class="flex justify-end gap-2 pt-4 border-t border-default">
            <UButton
              label="주문 관리 이동"
              color="neutral"
              variant="outline"
              @click="navigateTo('/operation/orders')"
            />
            <UButton label="닫기" color="primary" @click="isModalOpen = false" />
          </div>
        </div>
      </template>
    </UModal>
  </div>
</template>
