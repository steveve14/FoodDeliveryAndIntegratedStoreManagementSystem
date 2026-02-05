<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'
import { getPaginationRowModel } from '@tanstack/table-core'

const CompIcon = resolveComponent('UIcon')
const CompButton = resolveComponent('UButton')

const table = useTemplateRef<any>('table')

const { data } = await useAsyncData('reviews', async () => {
  return Array.from({ length: 15 }).map((_, i) => ({
    id: i + 1,
    store: `가게 이름 ${i + 1}`,
    user: `Customer${i}`,
    rating: Math.floor(Math.random() * 5) + 1,
    content: '음식이 정말 맛있어요! 배달도 빨랐습니다.',
    date: new Date().toISOString()
  }))
})

const columns: TableColumn<any>[] = [
  { accessorKey: 'store', header: '매장명' },
  {
    accessorKey: 'rating',
    header: '평점',
    cell: ({ row }) => h('div', { class: 'flex items-center text-yellow-500' }, 
      Array(row.original.rating).fill(0).map(() => h(CompIcon, { name: 'i-lucide-star', class: 'w-4 h-4 fill-current' }))
    )
  },
  { accessorKey: 'content', header: '내용', cell: ({ row }) => h('span', { class: 'line-clamp-1' }, row.original.content) },
  { accessorKey: 'user', header: '작성자' },
  {
    id: 'actions',
    cell: () => h('div', { class: 'flex gap-2 justify-end' }, [
      h(CompButton, { label: '답글', color: 'neutral', variant: 'ghost', size: 'xs' }),
      h(CompButton, { label: '숨김', color: 'error', variant: 'ghost', size: 'xs' })
    ])
  }
]
</script>

<template>
  <UDashboardPanel id="reviews">
    <UDashboardNavbar title="리뷰 관리" />
    <template #body>
      <UTable ref="table" :columns="columns" :data="data" :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }" />
    </template>
  </UDashboardPanel>
</template>