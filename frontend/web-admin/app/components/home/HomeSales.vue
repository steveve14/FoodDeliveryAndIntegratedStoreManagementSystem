<script setup lang="ts">
import { h, resolveComponent } from 'vue';
import type { TableColumn } from '@nuxt/ui';
import type { Period, Range } from '~/types';
import { useAdminHomeSource } from '~/composables/useAdminHomeSource';

const props = defineProps<{
  period: Period;
  range: Range;
}>();

const UBadge = resolveComponent('UBadge');

type AdminSaleStatus = '성공' | '실패' | '진행중';

type AdminSale = {
  id: string;
  date: string;
  status: AdminSaleStatus;
  email: string;
  amount: number;
};

const { data: source } = await useAdminHomeSource();

const statusFromApi = (status: string): AdminSaleStatus => {
  switch (status) {
    case 'DONE':
      return '성공';
    case 'CANCELLED':
      return '실패';
    default:
      return '진행중';
  }
};

const data = computed<AdminSale[]>(() => {
  const customerEmailById = Object.fromEntries((source.value?.customers ?? []).map(customer => [customer.id, customer.email]));

  return (source.value?.orders ?? [])
    .filter((order) => {
      const createdAt = new Date(order.createdAt).getTime();
      return createdAt >= props.range.start.getTime() && createdAt <= props.range.end.getTime();
    })
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5)
    .map(order => ({
      id: order.id,
      date: order.createdAt,
      status: statusFromApi(order.status),
      email: customerEmailById[order.userId] ?? order.userId,
      amount: order.totalAmount,
    }));
});

const columns: TableColumn<AdminSale>[] = [
  {
    accessorKey: 'id',
    header: 'ID',
    cell: ({ row }) => `#${row.getValue('id')}`,
  },
  {
    accessorKey: 'date',
    header: '날짜',
    cell: ({ row }) => {
      return new Date(row.getValue('date')).toLocaleString('ko-KR', {
        day: 'numeric',
        month: 'short',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false,
      });
    },
  },
  {
    accessorKey: 'status',
    header: '상태',
    cell: ({ row }) => {
      const color = {
        성공: 'success' as const,
        실패: 'error' as const,
        진행중: 'warning' as const,
      }[row.getValue('status') as string];

      return h(UBadge, { class: 'capitalize', variant: 'subtle', color }, () =>
        row.getValue('status'),
      );
    },
  },
  {
    accessorKey: 'email',
    header: '이메일',
  },
  {
    accessorKey: 'amount',
    header: () => h('div', { class: 'text-right' }, '금액'),
    cell: ({ row }) => {
      const amount = Number.parseFloat(row.getValue('amount'));

      const formatted = new Intl.NumberFormat('ko-KR', {
        style: 'currency',
        currency: 'KRW',
      }).format(amount);

      return h('div', { class: 'text-right font-medium' }, formatted);
    },
  },
];
</script>

<template>
  <UTable
    :data="data"
    :columns="columns"
    class="shrink-0"
    :ui="{
      base: 'table-fixed border-separate border-spacing-0',
      thead: '[&>tr]:bg-elevated/50 [&>tr]:after:content-none',
      tbody: '[&>tr]:last:[&>td]:border-b-0',
      th: 'first:rounded-l-lg last:rounded-r-lg border-y border-default first:border-l last:border-r',
      td: 'border-b border-default'
    }"
  />
</template>
