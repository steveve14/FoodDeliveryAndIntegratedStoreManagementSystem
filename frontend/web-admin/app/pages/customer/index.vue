<script setup lang="ts">
import { h, ref, resolveComponent, computed } from 'vue';
import type { TableColumn } from '@nuxt/ui';
import { getPaginationRowModel } from '@tanstack/table-core';
import type { Row } from '@tanstack/table-core';
import type { UserDto, UserProfileDto } from '~/types/api';
import { useUserApi } from '~/composables/api/useUserApi';

// ── 컴포넌트 ─────────────────────────────────────────────────
const UButton = resolveComponent('UButton');
const UBadge = resolveComponent('UBadge');
const UDropdownMenu = resolveComponent('UDropdownMenu');
const UCheckbox = resolveComponent('UCheckbox');
const UAvatar = resolveComponent('UAvatar');

type DashboardColumn = {
  id: string;
  getCanHide: () => boolean;
  getIsVisible: () => boolean;
  toggleVisibility: (visible: boolean) => void;
};

type DashboardTableApi = {
  getAllColumns: () => DashboardColumn[];
  getColumn: (id: string) => (DashboardColumn & {
    setFilterValue?: (value: unknown) => void;
  }) | undefined;
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

const toast = useToast();
const table = useTemplateRef<DashboardTableRef>('table');
const { getUserByEmail, getProfile, deleteUser } = useUserApi();

// ── 상태 ─────────────────────────────────────────────────────
const searchEmail = ref('');
const searchLoading = ref(false);
const data = ref<UserDto[]>([]);
const columnFilters = ref([{ id: 'email', value: '' }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const profileModal = ref(false);
const selectedProfile = ref<UserProfileDto | null>(null);
const profileLoading = ref(false);
const deletingId = ref<string | null>(null);

const columnLabels: Record<string, string> = {
  select: '선택',
  name: '이름',
  email: '이메일',
  roles: '권한',
  createdAt: '가입일',
  actions: '관리',
};

const columnVisibilityItems = computed(() => {
  return (table.value?.tableApi?.getAllColumns() ?? [])
    .filter(col => col.getCanHide())
    .map(col => ({
      label: columnLabels[col.id] || col.id,
      type: 'checkbox' as const,
      checked: col.getIsVisible(),
      onUpdateChecked (checked: boolean) {
        table.value?.tableApi?.getColumn(col.id)?.toggleVisibility(!!checked);
      },
      onSelect (e?: Event) { e?.preventDefault(); },
    }));
});

// ── 이메일 검색 ───────────────────────────────────────────────
async function searchUser () {
  if (!searchEmail.value.trim()) {
    toast.add({ title: '이메일을 입력해주세요.', color: 'warning' });
    return;
  }
  searchLoading.value = true;
  try {
    const res = await getUserByEmail(searchEmail.value.trim());
    if (res.success && res.data) {
      const exists = data.value.find(u => u.id === res.data.id);
      if (!exists) {
        data.value = [res.data, ...data.value];
        toast.add({ title: '고객 추가됨', description: res.data.email, color: 'success' });
      } else {
        toast.add({ title: '이미 목록에 있습니다.', color: 'neutral' });
      }
    } else {
      toast.add({ title: '해당 이메일의 고객을 찾을 수 없습니다.', color: 'error' });
    }
  } catch {
    toast.add({ title: '조회 실패', color: 'error' });
  } finally {
    searchLoading.value = false;
    searchEmail.value = '';
  }
}

// ── 프로필 보기 ───────────────────────────────────────────────
async function viewProfile (userId: string) {
  profileLoading.value = true;
  profileModal.value = true;
  selectedProfile.value = null;
  try {
    const res = await getProfile(userId);
    if (res.success) {
      selectedProfile.value = res.data;
    }
  } finally {
    profileLoading.value = false;
  }
}

// ── 고객 삭제 ─────────────────────────────────────────────────
async function onDelete (userId: string, name: string) {
  if (!confirm(`"${name}" 고객을 삭제하시겠습니까?`)) {
    return;
  }
  deletingId.value = userId;
  try {
    await deleteUser(userId);
    data.value = data.value.filter(u => u.id !== userId);
    toast.add({ title: '삭제 완료', description: `${name} 님이 삭제되었습니다.`, color: 'success' });
  } catch {
    toast.add({ title: '삭제 실패', color: 'error' });
  } finally {
    deletingId.value = null;
  }
}

// ── 행 액션 ───────────────────────────────────────────────────
function getRowItems (row: Row<UserDto>) {
  return [
    { type: 'label', label: '고객 관리' },
    {
      label: 'ID 복사',
      icon: 'i-lucide-copy',
      onSelect () {
        navigator.clipboard.writeText(row.original.id);
        toast.add({ title: '복사 완료', description: '고객 ID가 복사되었습니다.' });
      },
    },
    { type: 'separator' },
    {
      label: '프로필 상세 보기',
      icon: 'i-lucide-user',
      onSelect () { viewProfile(row.original.id); },
    },
    { type: 'separator' },
    {
      label: '고객 삭제',
      icon: 'i-lucide-trash',
      color: 'error',
      onSelect () { onDelete(row.original.id, row.original.name); },
    },
  ];
}

// ── 테이블 컬럼 ───────────────────────────────────────────────
const columns: TableColumn<UserDto>[] = [
  {
    id: 'select',
    header: ({ table }) =>
      h(UCheckbox, {
        'modelValue': table.getIsSomePageRowsSelected() ? 'indeterminate' : table.getIsAllPageRowsSelected(),
        'onUpdate:modelValue': (v: boolean) => table.toggleAllPageRowsSelected(!!v),
        'ariaLabel': '전체 선택',
      }),
    cell: ({ row }) =>
      h(UCheckbox, {
        'modelValue': row.getIsSelected(),
        'onUpdate:modelValue': (v: boolean) => row.toggleSelected(!!v),
        'ariaLabel': '행 선택',
      }),
  },
  {
    accessorKey: 'name',
    header: '이름',
    cell: ({ row }) =>
      h('div', { class: 'flex items-center gap-3' }, [
        h(UAvatar, { alt: row.original.name, size: 'sm' }),
        h('div', {}, [
          h('p', { class: 'font-medium text-highlighted' }, row.original.name),
          h('p', { class: 'text-xs text-muted' }, row.original.id.slice(0, 8) + '...'),
        ]),
      ]),
  },
  {
    accessorKey: 'email',
    header: '이메일',
    filterFn: 'includesString',
  },
  {
    accessorKey: 'roles',
    header: '권한',
    cell: ({ row }) => {
      const role = row.original.roles;
      const color = role?.includes('ADMIN') ? 'error' : role?.includes('STORE') ? 'warning' : 'neutral';
      return h(UBadge, { color, variant: 'subtle' }, () => role || 'USER');
    },
  },
  {
    accessorKey: 'createdAt',
    header: '가입일',
    cell: ({ row }) =>
      row.original.createdAt ?
        new Date(row.original.createdAt).toLocaleDateString('ko-KR') :
        '-',
  },
  {
    id: 'actions',
    cell: ({ row }) =>
      h(
        'div',
        { class: 'text-right' },
        h(UDropdownMenu, {
          content: { align: 'end' },
          items: getRowItems(row),
        }, () =>
          h(UButton, {
            icon: 'i-lucide-ellipsis-vertical',
            color: 'neutral',
            variant: 'ghost',
          }),
        ),
      ),
  },
];
</script>

<template>
  <UDashboardPanel id="customers">
    <template #header>
      <UDashboardNavbar title="고객 목록">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
        <template #right>
          <UsersAddModal />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <!-- 이메일 검색 -->
      <div class="flex flex-wrap items-center justify-between gap-1.5">
        <div class="flex gap-2">
          <UInput
            v-model="searchEmail"
            placeholder="이메일로 고객 검색..."
            icon="i-lucide-search"
            class="w-72"
            @keydown.enter="searchUser"
          />
          <UButton
            label="검색"
            :loading="searchLoading"
            @click="searchUser"
          />
        </div>

        <div class="flex flex-wrap items-center gap-1.5">
          <UDropdownMenu
            :items="columnVisibilityItems"
            :content="{ align: 'end' }"
          >
            <UButton label="컬럼 설정" color="neutral" variant="outline" trailing-icon="i-lucide-settings-2" />
          </UDropdownMenu>
        </div>
      </div>

      <!-- 검색 안내 -->
      <div v-if="data.length === 0" class="text-center text-muted text-sm py-16">
        <UIcon name="i-lucide-users" class="text-4xl mb-3 block mx-auto" />
        이메일로 고객을 검색하면 목록에 추가됩니다.
      </div>

      <!-- 테이블 -->
      <UTable
        v-else
        ref="table"
        v-model:column-filters="columnFilters"
        v-model:column-visibility="columnVisibility"
        v-model:row-selection="rowSelection"
        v-model:pagination="pagination"
        :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }"
        class="shrink-0"
        :data="data"
        :columns="columns"
        :ui="{
          base: 'table-fixed border-separate border-spacing-0',
          thead: '[&>tr]:bg-elevated/50 [&>tr]:after:content-none',
          tbody: '[&>tr]:last:[&>td]:border-b-0',
          th: 'py-2 first:rounded-l-lg last:rounded-r-lg border-y border-default first:border-l last:border-r',
          td: 'border-b border-default',
          separator: 'h-0'
        }"
      />

      <!-- 페이지네이션 -->
      <div v-if="data.length > 0" class="flex items-center justify-between gap-3 border-t border-default pt-4 mt-auto">
        <div class="text-sm text-muted">
          총 {{ data.length }}명
        </div>
        <UPagination
          :default-page="(table?.tableApi?.getState().pagination.pageIndex || 0) + 1"
          :items-per-page="table?.tableApi?.getState().pagination.pageSize"
          :total="data.length"
          @update:page="(p: number) => table?.tableApi?.setPageIndex(p - 1)"
        />
      </div>

      <!-- 프로필 상세 모달 -->
      <UModal v-model:open="profileModal" title="고객 프로필" description="고객의 상세 프로필 정보입니다.">
        <template #body>
          <div v-if="profileLoading" class="flex justify-center py-8">
            <UIcon name="i-lucide-loader-circle" class="text-2xl animate-spin text-muted" />
          </div>
          <div v-else-if="selectedProfile" class="space-y-3 text-sm">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-muted mb-0.5">이름</p>
                <p class="font-medium">{{ selectedProfile.name }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">이메일</p>
                <p>{{ selectedProfile.email }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">전화번호</p>
                <p>{{ selectedProfile.phone || '-' }}</p>
              </div>
              <div>
                <p class="text-muted mb-0.5">ID</p>
                <p class="font-mono text-xs">{{ selectedProfile.id }}</p>
              </div>
            </div>
          </div>
          <div v-else class="text-center text-muted py-6">프로필 정보를 불러올 수 없습니다.</div>
        </template>
      </UModal>
    </template>
  </UDashboardPanel>
</template>
