<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'
import { getPaginationRowModel } from '@tanstack/table-core'
import { format } from 'date-fns'

const CompBadge = resolveComponent('UBadge')
const CompButton = resolveComponent('UButton')

const table = useTemplateRef<any>('table')

const { data } = await useAsyncData('reports', async () => {
  return Array.from({ length: 15 }).map((_, i) => ({
    id: i + 1,
    target: i % 2 === 0 ? 'Review #1234' : 'User @bad_guy',
    reason: i % 3 === 0 ? '스팸/홍보' : '욕설/비하',
    reporter: `Reporter${i}`,
    status: i % 4 === 0 ? 'open' : 'resolved',
    date: new Date().toISOString()
  }))
})

const columns: TableColumn<any>[] = [
  { accessorKey: 'id', header: 'No.' },
  { accessorKey: 'reason', header: '신고 사유', cell: ({ row }) => h('span', { class: 'text-red-500 font-medium' }, row.original.reason) },
  { accessorKey: 'target', header: '신고 대상' },
  { accessorKey: 'reporter', header: '신고자' },
  {
    accessorKey: 'status',
    header: '처리 상태',
    cell: ({ row }) => h(CompBadge, { 
      color: row.original.status === 'open' ? 'error' : 'neutral',
      variant: row.original.status === 'open' ? 'solid' : 'subtle'
    }, () => row.original.status === 'open' ? '접수됨' : '조치 완료')
  },
  {
    id: 'actions',
    cell: ({ row }) => row.original.status === 'open' 
      ? h('div', { class: 'flex gap-2' }, [
          h(CompButton, { label: '무시', color: 'neutral', size: 'xs', variant: 'ghost' }),
          h(CompButton, { label: '제재 처리', color: 'error', size: 'xs' })
        ])
      : h('span', { class: 'text-muted text-xs' }, '처리됨')
  }
]
</script>

<template>
  <UDashboardPanel id="reports">
    <UDashboardNavbar title="신고 및 제재 관리" />
    <template #body>
      <UTable ref="table" :columns="columns" :data="data" :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }" />
    </template>
  </UDashboardPanel>
</template>