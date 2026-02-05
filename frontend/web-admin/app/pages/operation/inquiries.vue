<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'
import { getPaginationRowModel } from '@tanstack/table-core'
import { format } from 'date-fns'

const CompBadge = resolveComponent('UBadge')
const CompButton = resolveComponent('UButton')

const table = useTemplateRef<any>('table')
const statusFilter = ref('all')

const { data } = await useAsyncData('inquiries', async () => {
  return Array.from({ length: 20 }).map((_, i) => ({
    id: i + 1,
    user: `User${i + 1}`,
    type: i % 2 === 0 ? '결제/환불' : '계정 이용',
    title: i % 2 === 0 ? '결제가 중복으로 되었습니다.' : '로그인이 안돼요',
    status: i % 3 === 0 ? 'pending' : 'resolved',
    date: new Date().toISOString()
  }))
})

const columns: TableColumn<any>[] = [
  { accessorKey: 'id', header: 'No.' },
  { accessorKey: 'type', header: '유형' },
  { 
    accessorKey: 'title', 
    header: '문의 제목',
    cell: ({ row }) => h('span', { class: 'cursor-pointer hover:underline' }, row.original.title)
  },
  { accessorKey: 'user', header: '작성자' },
  {
    accessorKey: 'status',
    header: '답변 상태',
    cell: ({ row }) => h(CompBadge, { 
      color: row.original.status === 'pending' ? 'warning' : 'success', 
      variant: 'subtle' 
    }, () => row.original.status === 'pending' ? '답변 대기' : '답변 완료')
  },
  {
    accessorKey: 'date',
    header: '등록일',
    cell: ({ row }) => format(new Date(row.original.date), 'yyyy-MM-dd HH:mm')
  },
  {
    id: 'actions',
    cell: ({ row }) => h(CompButton, { 
      label: row.original.status === 'pending' ? '답변하기' : '상세보기',
      size: 'xs',
      color: 'neutral',
      variant: 'outline'
    })
  }
]

watch(statusFilter, (val) => table.value?.tableApi?.getColumn('status')?.setFilterValue(val === 'all' ? undefined : val))
</script>

<template>
  <UDashboardPanel id="inquiries">
    <UDashboardNavbar title="1:1 문의 관리" />
    <template #body>
      <div class="flex justify-end mb-4">
        <USelect v-model="statusFilter" :items="[{label:'전체', value:'all'}, {label:'답변 대기', value:'pending'}, {label:'답변 완료', value:'resolved'}]" />
      </div>
      <UTable ref="table" :columns="columns" :data="data" :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }" />
    </template>
  </UDashboardPanel>
</template>