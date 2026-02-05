<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'
import { getPaginationRowModel } from '@tanstack/table-core'
import { format } from 'date-fns'
import * as z from 'zod'
import type { FormSubmitEvent } from '@nuxt/ui'

// ==========================================
// 1. [설정] 페이지 및 데이터 정의
// ==========================================
const PAGE_TITLE = '공지사항 관리'
const DATA_KEY = 'notices'

// 공지사항 데이터 타입
type NoticeItem = {
  id: number
  title: string
  content: string
  status: 'active' | 'inactive' | 'scheduled'
  category: 'general' | 'urgent' | 'event'
  createdAt: string
  author: string
  isPinned: boolean
}

// 폼 유효성 검사 스키마 (Zod)
// 폼 유효성 검사 스키마 (Zod)
const formSchema = z.object({
  title: z.string().min(2, '제목은 2글자 이상 입력해주세요.'),
  
  category: z.enum(['general', 'urgent', 'event']).catch('general'),
  
  status: z.enum(['active', 'inactive', 'scheduled']).catch('active'),
  
  content: z.string().min(1, '내용을 입력해주세요.'),
  isPinned: z.boolean().default(false)
})

type FormSchema = z.output<typeof formSchema>

// ==========================================
// 2. 컴포넌트 & 유틸리티
// ==========================================
const CompButton = resolveComponent('UButton')
const CompBadge = resolveComponent('UBadge')
const CompDropdownMenu = resolveComponent('UDropdownMenu')
const CompCheckbox = resolveComponent('UCheckbox')
const toast = useToast()
const table = useTemplateRef<any>('table')

// ==========================================
// 3. 상태 관리
// ==========================================
// 테이블 관련 상태
const searchFilter = ref('')
const statusFilter = ref('all')
const columnFilters = ref([{ id: 'title', value: '' }])
const columnVisibility = ref()
const rowSelection = ref({})
const pagination = ref({ pageIndex: 0, pageSize: 10 })

// 슬라이드오버(등록/수정 폼) 관련 상태
const isSlideoverOpen = ref(false)
const isEditMode = ref(false)
const selectedId = ref<number | null>(null)

// 폼 상태 초기값
const initialFormState: FormSchema = {
  title: '',
  category: 'general',
  status: 'active',
  content: '',
  isPinned: false
}
const formState = reactive<FormSchema>({ ...initialFormState })

// ==========================================
// 4. 액션 핸들러 (등록/수정/삭제)
// ==========================================

// [신규 등록] 버튼 클릭 시
function openCreateModal() {
  isEditMode.value = false
  selectedId.value = null
  Object.assign(formState, initialFormState) // 폼 초기화
  isSlideoverOpen.value = true
}

// [수정] 메뉴 클릭 시
function openEditModal(row: NoticeItem) {
  isEditMode.value = true
  selectedId.value = row.id
  // 기존 데이터 폼에 바인딩
  Object.assign(formState, {
    title: row.title,
    category: row.category,
    status: row.status,
    content: row.content,
    isPinned: row.isPinned
  })
  isSlideoverOpen.value = true
}

// [저장] 버튼 클릭 시 (Submit)
async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  // 실제 API 연동 시 여기서 호출
  const actionName = isEditMode.value ? '수정' : '등록'
  
  toast.add({
    title: `${actionName} 완료`,
    description: `공지사항이 성공적으로 ${actionName}되었습니다.`,
    color: 'success',
    icon: 'i-lucide-check-circle'
  })
  
  isSlideoverOpen.value = false
  // refresh() // 데이터 갱신 필요 시 호출
}

// [삭제] 메뉴 클릭 시
function onDelete(id: number) {
  toast.add({
    title: '삭제 완료',
    description: `공지사항 #${id} 항목이 삭제되었습니다.`,
    color: 'error'
  })
}

// ==========================================
// 5. 데이터 페칭 (Mock Data)
// ==========================================
// ==========================================
// 5. 데이터 페칭 (Mock Data - Enhanced)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<NoticeItem[]>(DATA_KEY, async () => {
  // ★ 수정됨: readonly 배열도 받을 수 있도록 <T>(arr: readonly T[]) 로 변경
  const getRandom = <T>(arr: readonly T[]) => arr.length > 0 ? arr[Math.floor(Math.random() * arr.length)] : arr[0]
  
  const categories = ['general', 'urgent', 'event'] as const
  const statuses = ['active', 'inactive', 'scheduled'] as const
  const authors = ['관리자', '운영팀', '김철수 매니저', '시스템', '마케팅팀']
  
  const titles = [
    '[안내] 개인정보 처리방침 변경 안내',
    '[점검] 새벽 정기 서버 점검 안내 (02:00 ~ 04:00)',
    '[이벤트] 신규 가입자 대상 웰컴 쿠폰 증정',
    '[긴급] 일부 결제 시스템 오류 수정 완료',
    '[업데이트] v2.3.0 기능 개선 및 버그 수정',
    '[공지] 추석 연휴 고객센터 운영 일정 안내',
    '[안내] 서비스 이용약관 개정 사전 안내',
    '[이벤트] 봄맞이 친구 초대 프로모션',
    '[점검] 데이터베이스 성능 최적화 작업',
    '[보안] 비밀번호 주기적 변경 캠페인'
  ]

  // 100개의 더미 데이터 생성
  return Array.from({ length: 100 }).map((_, i) => {
    const category = getRandom(categories) ?? 'general'
    const status = getRandom(statuses) ?? 'active'
    const author = getRandom(authors) ?? '관리자'
    const title = getRandom(titles) ?? '[안내] 공지사항'
    const isUrgent = category === 'urgent'
    
    // 날짜를 최근 1년 내 랜덤으로 생성
    const date = new Date(Date.now() - Math.floor(Math.random() * 365 * 24 * 60 * 60 * 1000))

    return {
      id: 100 - i, 
      title: title, 
      content: `안녕하세요, ${author}입니다.\n\n항상 저희 서비스를 이용해 주셔서 감사합니다.\n\n본 공지사항의 상세 내용은 아래와 같습니다.\n\n1. 변경 일시: ${format(date, 'yyyy-MM-dd')}\n2. 주요 내용: 서비스 품질 향상\n\n이용에 불편을 드려 죄송합니다.\n감사합니다.`,
      status: status,
      category: category,
      createdAt: date.toISOString(),
      author: author,
      isPinned: isUrgent || Math.random() < 0.05 
    }
  }).sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
})

// ==========================================
// 6. 테이블 컬럼 정의
// ==========================================
// 컬럼 이름 한글 매핑
const columnLabels: Record<string, string> = {
  select: '선택',
  id: 'No.',
  category: '분류',
  title: '제목',
  status: '상태',
  author: '작성자',
  createdAt: '등록일',
  actions: '관리'
}

const columns: TableColumn<NoticeItem>[] = [
  {
    id: 'select',
    header: ({ table }) => h(CompCheckbox, {
      'modelValue': table.getIsSomePageRowsSelected() ? 'indeterminate' : table.getIsAllPageRowsSelected(),
      'onUpdate:modelValue': (v: boolean) => table.toggleAllPageRowsSelected(!!v),
      'ariaLabel': 'Select all'
    }),
    cell: ({ row }) => h(CompCheckbox, {
      'modelValue': row.getIsSelected(),
      'onUpdate:modelValue': (v: boolean) => row.toggleSelected(!!v),
      'ariaLabel': 'Select row'
    })
  },
  {
    accessorKey: 'id',
    header: 'No.',
    cell: ({ row }) => row.original.id
  },
  {
    accessorKey: 'category',
    header: '분류',
    cell: ({ row }) => {
      const map = { general: '일반', urgent: '긴급', event: '이벤트' }
      const color = { general: 'neutral', urgent: 'error', event: 'primary' } as const
      return h(CompBadge, { variant: 'subtle', color: color[row.original.category] }, () => map[row.original.category])
    }
  },
  {
    accessorKey: 'title',
    header: '제목',
    cell: ({ row }) => h('div', { class: 'flex items-center gap-2' }, [
      row.original.isPinned && h('span', { class: 'i-lucide-pin text-primary w-4 h-4' }), // 고정 아이콘
      h('span', { class: 'font-medium truncate max-w-[300px]' }, row.original.title)
    ])
  },
  {
    accessorKey: 'status',
    header: '상태',
    cell: ({ row }) => {
      const map = { active: '게시중', inactive: '비공개', scheduled: '예약' }
      const color = { active: 'success', inactive: 'neutral', scheduled: 'warning' } as const
      return h(CompBadge, { color: color[row.original.status], variant: 'subtle' }, () => map[row.original.status])
    }
  },
  {
    accessorKey: 'createdAt',
    header: '등록일',
    cell: ({ row }) => format(new Date(row.original.createdAt), 'yyyy-MM-dd')
  },
  {
    id: 'actions',
    cell: ({ row }) => h('div', { class: 'text-right' }, 
      h(CompDropdownMenu, {
        content: { align: 'end' },
        items: [[
          { 
            label: '상세 보기 / 수정', 
            icon: 'i-lucide-edit', 
            onSelect: () => openEditModal(row.original) // 수정 함수 연결
          },
          { 
            label: '삭제', 
            icon: 'i-lucide-trash', 
            color: 'error',
            onSelect: () => onDelete(row.original.id)
          }
        ]]
      }, () => h(CompButton, { icon: 'i-lucide-ellipsis-vertical', color: 'neutral', variant: 'ghost' }))
    )
  }
]

// ==========================================
// 7. 필터링 로직
// ==========================================
watch(searchFilter, (val) => {
  table.value?.tableApi?.getColumn('title')?.setFilterValue(val)
})

watch(statusFilter, (val) => {
  table.value?.tableApi?.getColumn('status')?.setFilterValue(val === 'all' ? undefined : val)
})
</script>

<template>
  <UDashboardPanel :id="DATA_KEY">
    <template #header>
      <UDashboardNavbar :title="PAGE_TITLE">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
        <template #right>
          <UButton label="공지 등록" icon="i-lucide-plus" color="primary" @click="openCreateModal" />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-wrap items-center justify-between gap-1.5 mb-4">
        <UInput
          v-model="searchFilter"
          icon="i-lucide-search"
          placeholder="제목 검색..."
          class="max-w-sm"
        />

        <div class="flex items-center gap-1.5">
          <USelect
            v-model="statusFilter"
            :items="[
              { label: '전체 상태', value: 'all' },
              { label: '게시중', value: 'active' },
              { label: '비공개', value: 'inactive' },
              { label: '예약됨', value: 'scheduled' }
            ]"
            class="min-w-32"
          />
          
          <UDropdownMenu
            :items="table?.tableApi?.getAllColumns()
              .filter((c: any) => c.getCanHide())
              .map((c: any) => ({
                label: columnLabels[c.id] || c.id,
                type: 'checkbox',
                checked: c.getIsVisible(),
                onUpdateChecked: (v: boolean) => table?.tableApi?.getColumn(c.id)?.toggleVisibility(v)
              }))"
          >
            <UButton icon="i-lucide-settings-2" color="neutral" variant="outline" />
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
        :data="data"
        :columns="columns"
        :loading="loadingStatus === 'pending'"
        class="flex-1"
        :ui="{
          thead: '[&>tr]:bg-elevated/50',
          th: 'py-3',
          td: 'py-3 border-b border-default'
        }"
      />

      <div class="flex items-center justify-between border-t border-default pt-4 mt-auto">
        <span class="text-sm text-muted">
          Total {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }} items
        </span>
        <UPagination
          :default-page="(table?.tableApi?.getState().pagination.pageIndex || 0) + 1"
          :items-per-page="table?.tableApi?.getState().pagination.pageSize"
          :total="table?.tableApi?.getFilteredRowModel().rows.length"
          @update:page="(p) => table?.tableApi?.setPageIndex(p - 1)"
        />
      </div>
    </template>

    <USlideover v-model:open="isSlideoverOpen" :title="isEditMode ? '공지사항 수정' : '신규 공지 등록'">
      <template #body>
        <UForm :schema="formSchema" :state="formState" class="space-y-4" @submit="onSubmit">
          
          <UFormField label="제목" name="title" required>
            <UInput v-model="formState.title" placeholder="공지사항 제목을 입력하세요" />
          </UFormField>

          <UFormField name="isPinned">
            <UCheckbox v-model="formState.isPinned" label="상단 고정 (중요 공지)" />
          </UFormField>

          <div class="grid grid-cols-2 gap-4">
            <UFormField label="분류" name="category" required>
              <USelect 
                v-model="formState.category" 
                :items="[
                  { label: '일반', value: 'general' },
                  { label: '긴급', value: 'urgent' },
                  { label: '이벤트', value: 'event' }
                ]" 
                class="w-full"
              />
            </UFormField>

            <UFormField label="게시 상태" name="status" required>
              <USelect 
                v-model="formState.status" 
                :items="[
                  { label: '게시중', value: 'active' },
                  { label: '비공개', value: 'inactive' },
                  { label: '예약', value: 'scheduled' }
                ]"
                class="w-full"
              />
            </UFormField>
          </div>

          <UFormField label="내용" name="content" required>
            <UTextarea 
              v-model="formState.content" 
              placeholder="공지 내용을 입력하세요..." 
              :rows="12"
              autoresize
            />
          </UFormField>

          <div class="flex justify-end gap-2 pt-4 border-t border-default mt-auto">
            <UButton label="취소" color="neutral" variant="ghost" @click="isSlideoverOpen = false" />
            <UButton type="submit" :label="isEditMode ? '수정 완료' : '등록하기'" color="primary" />
          </div>

        </UForm>
      </template>
    </USlideover>
  </UDashboardPanel>
</template>