<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'
import { upperFirst } from 'scule'
import { getPaginationRowModel } from '@tanstack/table-core'
import type { Row } from '@tanstack/table-core'
import { format } from 'date-fns'

// 1. 데이터 타입 정의
type ErrorLog = {
  id: number
  timestamp: string
  level: 'critical' | 'error' | 'warning'
  status: 'open' | 'in_progress' | 'resolved'
  message: string
  source: string
  traceId: string
}

// 2. 컴포넌트 수동 해석 (변수명 충돌 방지)
const CompButton = resolveComponent('UButton')
const CompBadge = resolveComponent('UBadge')
const CompDropdownMenu = resolveComponent('UDropdownMenu')
const CompCheckbox = resolveComponent('UCheckbox')

const toast = useToast()

// ★ 수정됨: <any> 타입을 명시하여 순환 참조 오류 해결 ('table' implicitly has an 'any' type 해결)
const table = useTemplateRef<any>('table')

// 3. 테이블 상태 관리
const columnFilters = ref([{
  id: 'message',
  value: ''
}])
const columnVisibility = ref()
const rowSelection = ref({})
const pagination = ref({ pageIndex: 0, pageSize: 10 })

// 필터용 상태 변수
const levelFilter = ref('all')
const processStatusFilter = ref('all')

// 4. 모의 데이터 생성
const { data, status: loadingStatus } = await useAsyncData<ErrorLog[]>('error-logs', async () => {
  const statuses = ['open', 'in_progress', 'resolved'] as const
  
  return Array.from({ length: 50 }).map((_, i) => ({
    id: i + 1,
    timestamp: new Date(Date.now() - Math.floor(Math.random() * 1000000000)).toISOString(),
    level: i % 10 === 0 ? 'critical' : (i % 3 === 0 ? 'warning' : 'error'),
    status: statuses[Math.floor(Math.random() * statuses.length)]!, 
    message: i % 10 === 0 ? 'Database connection failed' : `Unexpected token in JSON at position ${i}`,
    source: i % 2 === 0 ? '/api/auth/login' : '/components/Dashboard.vue',
    traceId: `trace-${Math.random().toString(36).substring(7)}`
  }))
})

// 5. 상태 변경 더미 함수
function updateStatus(id: number, newStatus: string) {
  toast.add({ 
    title: '상태 업데이트', 
    description: `로그 #${id} 상태가 '${newStatus}'(으)로 변경되었습니다.` 
  })
}

// 6. 행(Row) 액션 메뉴 정의
function getRowItems(row: Row<ErrorLog>) {
  return [
    { type: 'label', label: '상태 변경' },
    {
      label: '처리 대기 (Open)',
      icon: 'i-lucide-circle',
      disabled: row.original.status === 'open',
      onSelect() { updateStatus(row.original.id, 'open') }
    },
    {
      label: '처리 중 (In Progress)',
      icon: 'i-lucide-clock',
      disabled: row.original.status === 'in_progress',
      onSelect() { updateStatus(row.original.id, 'in_progress') }
    },
    {
      label: '해결됨 (Resolved)',
      icon: 'i-lucide-check-circle',
      color: 'success',
      disabled: row.original.status === 'resolved',
      onSelect() { updateStatus(row.original.id, 'resolved') }
    },
    { type: 'separator' },
    {
      label: 'Trace ID 복사',
      icon: 'i-lucide-copy',
      onSelect() {
        navigator.clipboard.writeText(row.original.traceId)
        toast.add({ title: '복사 완료', description: `Trace ID가 클립보드에 복사되었습니다.` })
      }
    }
  ]
}

// 7. 테이블 컬럼 정의
const columns: TableColumn<ErrorLog>[] = [
  {
    id: 'select',
    header: ({ table }) =>
      h(CompCheckbox, {
        'modelValue': table.getIsSomePageRowsSelected() ? 'indeterminate' : table.getIsAllPageRowsSelected(),
        'onUpdate:modelValue': (v: boolean | 'indeterminate') => table.toggleAllPageRowsSelected(!!v),
        'ariaLabel': 'Select all'
      }),
    cell: ({ row }) =>
      h(CompCheckbox, {
        'modelValue': row.getIsSelected(),
        'onUpdate:modelValue': (v: boolean | 'indeterminate') => row.toggleSelected(!!v),
        'ariaLabel': 'Select row'
      })
  },
  {
    accessorKey: 'timestamp',
    header: ({ column }) => {
      const isSorted = column.getIsSorted()
      return h(CompButton, {
        color: 'neutral',
        variant: 'ghost',
        label: '발생 시간',
        icon: isSorted ? (isSorted === 'asc' ? 'i-lucide-arrow-up' : 'i-lucide-arrow-down') : 'i-lucide-calendar',
        class: '-mx-2.5',
        onClick: () => column.toggleSorting(column.getIsSorted() === 'asc')
      })
    },
    cell: ({ row }) => format(new Date(row.original.timestamp), 'yyyy-MM-dd HH:mm:ss')
  },
  {
    accessorKey: 'level',
    header: '심각도',
    filterFn: 'equals',
    cell: ({ row }) => {
      const color = { critical: 'error', error: 'error', warning: 'warning' }[row.original.level] as any
      const variant = row.original.level === 'critical' ? 'solid' : 'subtle'
      return h(CompBadge, { class: 'capitalize', variant, color }, () => row.original.level)
    }
  },
  {
    accessorKey: 'status',
    header: '처리 상태',
    filterFn: 'equals',
    cell: ({ row }) => {
      const config = {
        open: { color: 'neutral', label: '대기', icon: 'i-lucide-circle' },
        in_progress: { color: 'primary', label: '진행중', icon: 'i-lucide-loader' },
        resolved: { color: 'success', label: '해결됨', icon: 'i-lucide-check' }
      }[row.original.status] as any

      return h(CompBadge, { variant: 'subtle', color: config.color,}, () => [
        h('span', { class: config.icon }),
        config.label
      ])
    }
  },
  {
    accessorKey: 'message',
    header: '에러 메시지',
    cell: ({ row }) => h('span', { class: 'font-mono text-sm' }, row.original.message)
  },
  {
    accessorKey: 'source',
    header: '발생 위치',
    cell: ({ row }) => h('code', { class: 'text-xs text-muted bg-neutral-100 dark:bg-neutral-800 px-1 py-0.5 rounded' }, row.original.source)
  },
  {
    id: 'actions',
    cell: ({ row }) => h(
      'div', { class: 'text-right' },
      h(CompDropdownMenu, { content: { align: 'end' }, items: getRowItems(row) }, () =>
        h(CompButton, { icon: 'i-lucide-ellipsis-vertical', color: 'neutral', variant: 'ghost' })
      )
    )
  }
]

// 8. 필터 및 검색 로직 연결
watch(() => levelFilter.value, (newVal) => {
  if (!table?.value?.tableApi) return
  table.value.tableApi.getColumn('level')?.setFilterValue(newVal === 'all' ? undefined : newVal)
})

watch(() => processStatusFilter.value, (newVal) => {
  if (!table?.value?.tableApi) return
  table.value.tableApi.getColumn('status')?.setFilterValue(newVal === 'all' ? undefined : newVal)
})

// ★ 수정됨: table.value? 접근으로 안전하게 처리하여 'messageFilter' 오류 해결
const messageFilter = computed({
  get: () => {
    return (table.value?.tableApi?.getColumn('message')?.getFilterValue() as string) || ''
  },
  set: (val) => {
    table.value?.tableApi?.getColumn('message')?.setFilterValue(val || undefined)
  }
})
</script>

<template>
  <UDashboardPanel id="error-logs">
    <template #header>
      <UDashboardNavbar title="에러 로그 관리">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
        <template #right>
           <UButton label="로그 다운로드" icon="i-lucide-download" color="neutral" variant="outline" />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-wrap items-center justify-between gap-1.5">
        <UInput
          v-model="messageFilter"
          class="max-w-sm"
          icon="i-lucide-search"
          placeholder="에러 메시지 검색..."
        />

        <div class="flex flex-wrap items-center gap-1.5">
          <USelect
            v-model="processStatusFilter"
            :items="[
              { label: '모든 상태', value: 'all' },
              { label: '대기 (Open)', value: 'open' },
              { label: '진행중 (In Progress)', value: 'in_progress' },
              { label: '해결됨 (Resolved)', value: 'resolved' }
            ]"
            placeholder="처리 상태"
            class="min-w-36"
          />

          <USelect
            v-model="levelFilter"
            :items="[
              { label: '모든 등급', value: 'all' },
              { label: 'Critical', value: 'critical' },
              { label: 'Error', value: 'error' },
              { label: 'Warning', value: 'warning' }
            ]"
            placeholder="심각도"
            class="min-w-32"
          />
          
          <UDropdownMenu
            :items="
              table?.tableApi
                ?.getAllColumns()
                .filter((column: any) => column.getCanHide())
                .map((column: any) => ({
                  label: upperFirst(column.id),
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
        class="shrink-0"
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
          총 {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }} 건 조회됨
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