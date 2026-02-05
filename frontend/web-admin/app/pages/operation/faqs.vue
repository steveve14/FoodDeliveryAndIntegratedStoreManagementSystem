<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'
import { getPaginationRowModel } from '@tanstack/table-core'

const CompBadge = resolveComponent('UBadge')
const CompButton = resolveComponent('UButton')
const table = useTemplateRef<any>('table')

const { data } = await useAsyncData('faq', async () => {
  const categories = ['회원가입', '결제/환불', '배송', '기타']
  return Array.from({ length: 10 }).map((_, i) => ({
    id: i + 1,
    category: categories[i % 4],
    question: `자주 묻는 질문 예시입니다 ${i + 1}?`,
    viewCount: Math.floor(Math.random() * 1000)
  }))
})

const columns: TableColumn<any>[] = [
  { accessorKey: 'id', header: 'No.' },
  { accessorKey: 'category', header: '카테고리', cell: ({ row }) => h(CompBadge, { variant: 'subtle', color: 'neutral' }, () => row.original.category) },
  { accessorKey: 'question', header: '질문', cell: ({ row }) => h('span', { class: 'font-medium' }, row.original.question) },
  { accessorKey: 'viewCount', header: '조회수', cell: ({ row }) => row.original.viewCount.toLocaleString() },
  {
    id: 'actions',
    cell: () => h('div', { class: 'text-right' }, h(CompButton, { icon: 'i-lucide-edit', variant: 'ghost', color: 'neutral' }))
  }
]
</script>

<template>
  <UDashboardPanel id="faq">
    <UDashboardNavbar title="FAQ 관리">
      <template #right>
        <UButton label="FAQ 등록" icon="i-lucide-plus" color="neutral" variant="outline" />
      </template>
    </UDashboardNavbar>
    <template #body>
      <UTable ref="table" :columns="columns" :data="data" :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }" />
    </template>
  </UDashboardPanel>
</template>