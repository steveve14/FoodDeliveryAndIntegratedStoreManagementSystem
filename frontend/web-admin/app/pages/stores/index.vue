<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'
import { upperFirst } from 'scule'
import { getPaginationRowModel } from '@tanstack/table-core'
import type { Row } from '@tanstack/table-core'
import { format } from 'date-fns'

// 1. 데이터 타입 정의
type Store = {
  id: number
  name: string
  owner: string
  category: string
  location: string
  status: 'active' | 'pending' | 'closed' | 'suspended'
  revenue: number
  joinedAt: string
}

// 2. 컴포넌트 수동 리졸브
const CompButton = resolveComponent('UButton')
const CompBadge = resolveComponent('UBadge')
const CompDropdownMenu = resolveComponent('UDropdownMenu')
const CompCheckbox = resolveComponent('UCheckbox')
const CompAvatar = resolveComponent('UAvatar')

const toast = useToast()
const table = useTemplateRef<any>('table') // 순환 참조 방지용 any 타입

// 3. 상태 관리
const searchFilter = ref('')
const statusFilter = ref('all')
const locationFilter = ref('all')

const columnFilters = ref([{ id: 'name', value: '' }])
const columnVisibility = ref()
const rowSelection = ref({})
const pagination = ref({ pageIndex: 0, pageSize: 10 })

// 4. 모의 데이터 생성
const { data, status: loadingStatus } = await useAsyncData<Store[]>('stores-data', async () => {
  const statuses = ['active', 'active', 'active', 'pending', 'closed', 'suspended'] as const
  const locations = ['서울', '경기', '부산', '인천', '대구', '강원', '제주']
  const categories = ['한식', '중식', '일식', '양식', '카페/디저트', '패스트푸드']

  return Array.from({ length: 45 }).map((_, i) => ({
    id: i + 1,
    name: `맛있는 가게 ${i + 1}호점`,
    owner: `점주 ${i + 1}`,
    category: categories[i % categories.length]!,
    location: locations[i % locations.length]!,
    status: statuses[Math.floor(Math.random() * statuses.length)]!,
    revenue: Math.floor(Math.random() * 100000000), // 0 ~ 1억
    joinedAt: new Date(Date.now() - Math.floor(Math.random() * 10000000000)).toISOString()
  }))
})

// 5. 유틸리티 함수
function formatCurrency(value: number) {
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value)
}

// 컬럼 한글 매핑 (컬럼 설정 메뉴용)
const columnLabels: Record<string, string> = {
  select: '선택',
  id: 'ID',
  name: '매장명',
  owner: '점주',
  category: '업종',
  location: '지역',
  revenue: '총 매출',
  status: '상태',
  joinedAt: '입점일',
  actions: '관리'
}

// 6. 행(Row) 액션 메뉴
function getRowItems(row: Row<Store>) {
  return [
    { type: 'label', label: '매장 관리' },
    {
      label: '매장 상세 정보',
      icon: 'i-lucide-store',
      onSelect: () => console.log('View', row.original.id)
    },
    {
      label: '정산 내역 보기',
      icon: 'i-lucide-receipt-text',
    },
    { type: 'separator' },
    {
      label: '영업 상태 변경',
      children: [
        { label: '영업중 (Active)', click: () => toast.add({ title: '상태 변경', description: '영업중으로 변경됨' }) },
        { label: '휴업 (Closed)', click: () => {} },
        { label: '승인 대기 (Pending)', click: () => {} }
      ]
    },
    { type: 'separator' },
    {
      label: '매장 삭제',
      icon: 'i-lucide-trash',
      color: 'error',
      onSelect: () => toast.add({ title: '삭제 완료', color: 'error' })
    }
  ]
}

// 7. 테이블 컬럼 정의
const columns: TableColumn<Store>[] = [
  {
    id: 'select',
    header: ({ table }) => h(CompCheckbox, {
      'modelValue': table.getIsSomePageRowsSelected() ? 'indeterminate' : table.getIsAllPageRowsSelected(),
      'onUpdate:modelValue': (v: boolean) => table.toggleAllPageRowsSelected(!!v),
      'ariaLabel': '전체 선택'
    }),
    cell: ({ row }) => h(CompCheckbox, {
      'modelValue': row.getIsSelected(),
      'onUpdate:modelValue': (v: boolean) => row.toggleSelected(!!v),
      'ariaLabel': '행 선택'
    })
  },
  {
    accessorKey: 'name',
    header: '매장 정보',
    cell: ({ row }) => h('div', { class: 'flex items-center gap-3' }, [
      h(CompAvatar, { src: undefined, alt: row.original.name, size: 'sm' }), // 매장 로고 등
      h('div', { class: 'flex flex-col' }, [
        h('span', { class: 'font-medium text-highlighted' }, row.original.name),
        h('span', { class: 'text-xs text-muted' }, row.original.category)
      ])
    ])
  },
  {
    accessorKey: 'owner',
    header: '점주',
    cell: ({ row }) => row.original.owner
  },
  {
    accessorKey: 'location',
    header: '지역',
    filterFn: 'equals', // 정확한 일치 필터
    cell: ({ row }) => h('span', { class: 'text-sm' }, row.original.location)
  },
  {
    accessorKey: 'revenue',
    header: ({ column }) => {
      const isSorted = column.getIsSorted()
      return h(CompButton, {
        color: 'neutral',
        variant: 'ghost',
        label: '총 매출',
        icon: isSorted ? (isSorted === 'asc' ? 'i-lucide-arrow-up' : 'i-lucide-arrow-down') : 'i-lucide-arrow-up-down',
        class: '-mx-2.5',
        onClick: () => column.toggleSorting(column.getIsSorted() === 'asc')
      })
    },
    cell: ({ row }) => h('span', { class: 'font-mono' }, formatCurrency(row.original.revenue))
  },
  {
    accessorKey: 'status',
    header: '상태',
    filterFn: 'equals',
    cell: ({ row }) => {
      const config: Record<string, any> = {
        active: { color: 'success', label: '영업중' },
        pending: { color: 'warning', label: '승인 대기' },
        closed: { color: 'neutral', label: '휴업/폐업' },
        suspended: { color: 'error', label: '정지됨' }
      }
      const status = config[row.original.status] || { color: 'gray', label: row.original.status }
      
      return h(CompBadge, { variant: 'subtle', color: status.color, class: 'capitalize' }, () => status.label)
    }
  },
  {
    accessorKey: 'joinedAt',
    header: '입점일',
    cell: ({ row }) => format(new Date(row.original.joinedAt), 'yyyy-MM-dd')
  },
  {
    id: 'actions',
    cell: ({ row }) => h('div', { class: 'text-right' }, 
      h(CompDropdownMenu, { content: { align: 'end' }, items: getRowItems(row) }, () =>
        h(CompButton, { icon: 'i-lucide-ellipsis-vertical', color: 'neutral', variant: 'ghost' })
      )
    )
  }
]

// 8. 필터 로직 연결
watch(searchFilter, (val) => {
  // 매장명 검색
  table.value?.tableApi?.getColumn('name')?.setFilterValue(val)
})

watch(statusFilter, (val) => {
  table.value?.tableApi?.getColumn('status')?.setFilterValue(val === 'all' ? undefined : val)
})

watch(locationFilter, (val) => {
  table.value?.tableApi?.getColumn('location')?.setFilterValue(val === 'all' ? undefined : val)
})
</script>

<template>
  <UDashboardPanel id="stores">
    <template #header>
      <UDashboardNavbar title="매장 관리">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
        <template #right>
           <UButton label="매장 등록" icon="i-lucide-plus" color="primary" />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-wrap items-center justify-between gap-1.5 mb-4">
        <UInput
          v-model="searchFilter"
          class="max-w-sm"
          icon="i-lucide-search"
          placeholder="매장명 검색..."
        />

        <div class="flex flex-wrap items-center gap-1.5">
          <USelect
            v-model="locationFilter"
            :items="[
              { label: '전체 지역', value: 'all' },
              { label: '서울', value: '서울' },
              { label: '경기', value: '경기' },
              { label: '부산', value: '부산' },
              { label: '제주', value: '제주' }
            ]"
            class="min-w-32"
          />

          <USelect
            v-model="statusFilter"
            :items="[
              { label: '전체 상태', value: 'all' },
              { label: '영업중', value: 'active' },
              { label: '승인 대기', value: 'pending' },
              { label: '휴업/폐업', value: 'closed' },
              { label: '정지됨', value: 'suspended' }
            ]"
            class="min-w-32"
          />
          
          <UDropdownMenu
            :items="
              table?.tableApi
                ?.getAllColumns()
                .filter((column: any) => column.getCanHide())
                .map((column: any) => ({
                  label: columnLabels[column.id] || column.id, // 한글 매핑 적용
                  type: 'checkbox' as const,
                  checked: column.getIsVisible(),
                  onUpdateChecked(checked: boolean) {
                    table?.tableApi?.getColumn(column.id)?.toggleVisibility(!!checked)
                  }
                }))
            "
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
          separator: 'h-0'
        }"
      />

      <div class="flex items-center justify-between gap-3 border-t border-default pt-4 mt-auto">
        <div class="text-sm text-muted">
          총 {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}개 매장 조회됨
        </div>
        <UPagination
          :default-page="(table?.tableApi?.getState().pagination.pageIndex || 0) + 1"
          :items-per-page="table?.tableApi?.getState().pagination.pageSize"
          :total="table?.tableApi?.getFilteredRowModel().rows.length"
          @update:page="(p: number) => table?.tableApi?.setPageIndex(p - 1)"
        />
      </div>
    </template>
  </UDashboardPanel>
</template>