<script setup lang="ts">
import { h, resolveComponent } from 'vue';
import type { TableColumn } from '@nuxt/ui';
import type { Period, Range } from '~/types';
import { useHomeStoreSource, type HomeOrderApiItem } from '~/composables/useHomeStoreSource';

const props = defineProps<{
  period: Period;
  range: Range;
}>();

const UBadge = resolveComponent('UBadge');

// 주문 상태 타입 정의
type OrderStatus = '접수' | '조리중' | '배달중' | '완료' | '취소';

type Order = {
  id: string;
  date: string;
  status: OrderStatus;
  menu: string;
  amount: number;
};

const { data: source } = await useHomeStoreSource();

const statusLabel = (status: string): OrderStatus => {
  switch (status) {
    case 'COOKING':
      return '조리중';
    case 'DELIVERING':
      return '배달중';
    case 'DONE':
      return '완료';
    case 'CANCELLED':
      return '취소';
    default:
      return '접수';
  }
};

const summarizeMenu = (order: HomeOrderApiItem, menuNameById: Record<string, string>) => {
  if (!order.items.length) {
    return '주문 항목 없음';
  }

  const first = order.items[0];
  const firstName = (first && menuNameById[first.productId]) || '알 수 없는 메뉴';
  if (order.items.length === 1) {
    return `${firstName} x ${first?.quantity ?? 1}`;
  }

  return `${firstName} 외 ${order.items.length - 1}건`;
};

const data = computed<Order[]>(() => {
  const menuNameById = source.value?.menuNameById ?? {};

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
      status: statusLabel(order.status),
      menu: summarizeMenu(order, menuNameById),
      amount: order.totalAmount,
    }));
});

const columns: TableColumn<Order>[] = [
  {
    accessorKey: 'id',
    header: '주문번호',
    cell: ({ row }) => `#${row.getValue('id')}`,
  },
  {
    accessorKey: 'date',
    header: '접수 시간',
    cell: ({ row }) => {
      return new Date(row.getValue('date')).toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false,
      });
    },
  },
  {
    accessorKey: 'menu',
    header: '주문 메뉴',
    // 텍스트 색상을 지정하지 않아 기본(foreground) 색상을 따름
    cell: ({ row }) =>
      h('span', { class: 'font-medium' }, row.getValue('menu')),
  },
  {
    accessorKey: 'status',
    header: '상태',
    cell: ({ row }) => {
      const status = row.getValue('status') as OrderStatus;

      // 상태에 따른 Semantic Color 매핑 (전역 테마에 정의된 키값만 사용)
      const color = {
        접수: 'error',
        조리중: 'warning',
        배달중: 'primary',
        완료: 'success',
        취소: 'neutral',
      }[status] as string;

      return h(
        UBadge,
        { class: 'font-bold', variant: 'subtle', color },
        () => status,
      );
    },
  },
  {
    accessorKey: 'amount',
    header: () => h('div', { class: 'text-right' }, '결제 금액'),
    cell: ({ row }) => {
      const amount = Number(row.getValue('amount'));
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
    label="최근 주문 내역"
    :ui="{
      base: 'table-fixed border-separate border-spacing-0',
      thead: '[&>tr]:bg-elevated/50 [&>tr]:after:content-none',
      tbody: '[&>tr]:last:[&>td]:border-b-0',
      th: 'first:rounded-l-lg last:rounded-r-lg border-y border-default first:border-l last:border-r',
      td: 'border-b border-default',
    }"
  />
</template>
