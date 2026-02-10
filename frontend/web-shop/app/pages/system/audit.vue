<script setup lang="ts">
/**
 * [시스템 > 관리자 활동 로그]
 * - 읽기 전용 (수정/삭제 불가)
 * - 상세 JSON 보기 기능 포함
 */
import { h, ref, resolveComponent, watch, computed } from "vue";
import type { TableColumn } from "@nuxt/ui";
import { format, subMinutes, subDays } from "date-fns";
import { getPaginationRowModel, getSortedRowModel } from "@tanstack/table-core";

// 1. 컴포넌트 리졸브
const UButton = resolveComponent("UButton");
const UBadge = resolveComponent("UBadge");
const UDropdownMenu = resolveComponent("UDropdownMenu");
const UCheckbox = resolveComponent("UCheckbox");
const UIcon = resolveComponent("UIcon");

const toast = useToast();
const table = ref<any>(null);

// 2. 설정 및 데이터 정의
const PAGE_TITLE = "관리자 활동 로그";
const DATA_KEY = "audit_logs";

type AuditLogItem = {
  id: number;
  adminName: string;
  adminIp: string;
  action: "create" | "update" | "delete" | "login" | "export";
  targetModule: string; // 예: User, Product, Banner
  targetId?: string; // 예: #123
  details: string; // 상세 변경 내용 (JSON string)
  createdAt: string;
};

// 3. 상태 관리
const columnFilters = ref([{ id: "adminName", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 }); // 로그는 많이 보므로 20개
const sorting = ref([{ id: "id", desc: true }]);

const actionFilter = ref("all");
const isModalOpen = ref(false);
const currentLog = ref<AuditLogItem | null>(null);

// 4. 데이터 페칭 (Mock Data)
const { data, status: loadingStatus } = await useAsyncData<AuditLogItem[]>(
  DATA_KEY,
  async () => {
    const actions: AuditLogItem["action"][] = [
      "create",
      "update",
      "delete",
      "login",
      "export",
    ];
    const modules = ["User", "Product", "Order", "Banner", "System"];

    return Array.from({ length: 50 }).map((_, i) => {
      const action = actions[i % actions.length];
      return {
        id: 5000 - i,
        adminName: `admin_${(i % 5) + 1}`,
        adminIp: `192.168.0.${i + 10}`,
        action: action,
        targetModule: modules[i % modules.length],
        targetId: action === "login" ? undefined : `${100 + i}`,
        details: JSON.stringify(
          { before: { status: "active" }, after: { status: "inactive" } },
          null,
          2,
        ),
        createdAt: subMinutes(new Date(), i * 45).toISOString(),
      } as AuditLogItem;
    });
  },
);

// 5. 액션 핸들러
function openDetailModal(row: AuditLogItem) {
  currentLog.value = row;
  isModalOpen.value = true;
}

function onExport() {
  toast.add({
    title: "다운로드 시작",
    description: "전체 로그를 엑셀 파일로 변환합니다.",
    color: "primary",
  });
}

// 6. 테이블 컬럼 정의
const columnLabels: Record<string, string> = {
  id: "Log ID",
  adminName: "관리자",
  action: "활동",
  target: "대상",
  adminIp: "IP 주소",
  createdAt: "일시",
  actions: "상세",
};

const columns: TableColumn<AuditLogItem>[] = [
  {
    accessorKey: "id",
    header: "ID",
    cell: ({ row }) =>
      h("span", { class: "text-xs text-gray-500 font-mono" }, row.original.id),
  },
  {
    accessorKey: "adminName",
    header: "관리자",
    cell: ({ row }) =>
      h("div", { class: "flex flex-col" }, [
        h("span", { class: "font-medium" }, row.original.adminName),
        h("span", { class: "text-xs text-gray-400" }, row.original.adminIp),
      ]),
  },
  {
    accessorKey: "action",
    header: "활동 유형",
    cell: ({ row }) => {
      const map: Record<string, any> = {
        create: { label: "생성", color: "success" },
        update: { label: "수정", color: "primary" },
        delete: { label: "삭제", color: "error" },
        login: { label: "로그인", color: "neutral" },
        export: { label: "다운로드", color: "warning" },
      };
      const info = map[row.original.action];
      return h(
        UBadge,
        { color: info.color, variant: "subtle" },
        () => info.label,
      );
    },
  },
  {
    id: "target",
    header: "작업 대상",
    cell: ({ row }) =>
      row.original.targetId
        ? h("span", `${row.original.targetModule} (#${row.original.targetId})`)
        : h("span", { class: "text-gray-400" }, "-"),
  },
  {
    accessorKey: "createdAt",
    header: "일시",
    cell: ({ row }) =>
      format(new Date(row.original.createdAt), "yyyy-MM-dd HH:mm:ss"),
  },
  {
    id: "actions",
    cell: ({ row }) =>
      h(UButton, {
        icon: "i-lucide-search",
        color: "neutral",
        variant: "ghost",
        onClick: () => openDetailModal(row.original),
      }),
  },
];

// 7. Watchers & Computeds
watch(actionFilter, (val) => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("action")
    ?.setFilterValue(val === "all" ? undefined : val);
});

const searchInput = computed({
  get: () =>
    (table.value?.tableApi
      ?.getColumn("adminName")
      ?.getFilterValue() as string) || "",
  set: (val) =>
    table.value?.tableApi
      ?.getColumn("adminName")
      ?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="searchInput"
        icon="i-lucide-search"
        placeholder="관리자명 검색..."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
        <UButton
          label="로그 다운로드"
          icon="i-lucide-download"
          color="neutral"
          variant="outline"
          @click="onExport"
        />
        <USelect
          v-model="actionFilter"
          :items="[
            { label: '전체 활동', value: 'all' },
            { label: '생성 (Create)', value: 'create' },
            { label: '수정 (Update)', value: 'update' },
            { label: '삭제 (Delete)', value: 'delete' },
            { label: '로그인 (Login)', value: 'login' },
          ]"
          class="min-w-40"
        />

        <UDropdownMenu
          :items="
            table?.tableApi
              ?.getAllColumns()
              .filter((c: any) => c.getCanHide())
              .map((c: any) => ({
                label: columnLabels[c.id] || c.id,
                type: 'checkbox',
                checked: c.getIsVisible(),
                onUpdateChecked: (v: boolean) =>
                  table?.tableApi?.getColumn(c.id)?.toggleVisibility(v),
                onSelect: (e: Event) => e.preventDefault(),
              })) || []
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
      v-model:pagination="pagination"
      v-model:sorting="sorting"
      :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }"
      :sorting-options="{ getSortedRowModel: getSortedRowModel() } as any"
      :data="data || []"
      :columns="columns"
      :loading="loadingStatus === 'pending'"
      class="shrink-0"
      :ui="{
        base: 'table-fixed border-separate border-spacing-0',
        thead: '[&>tr]:bg-elevated/50 [&>tr]:after:content-none',
        tbody: '[&>tr]:last:[&>td]:border-b-0',
        th: 'py-2 first:rounded-l-lg last:rounded-r-lg border-y border-default first:border-l last:border-r',
        td: 'border-b border-default',
        separator: 'h-0',
      }"
    />

    <div
      class="flex items-center justify-between gap-3 border-t border-default pt-4 mt-auto"
    >
      <div class="text-sm text-muted">
        총 {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}건의
        로그가 검색됨.
      </div>
      <UPagination
        :default-page="
          (table?.tableApi?.getState().pagination.pageIndex || 0) + 1
        "
        :items-per-page="table?.tableApi?.getState().pagination.pageSize"
        :total="table?.tableApi?.getFilteredRowModel().rows.length"
        @update:page="(p: number) => table?.tableApi?.setPageIndex(p - 1)"
      />
    </div>
  </div>

  <UModal
    v-model:open="isModalOpen"
    title="로그 상세 정보"
    :ui="{ wrapper: 'w-full sm:max-w-2xl' }"
  >
    <template #body>
      <div v-if="currentLog" class="p-4 space-y-4">
        <div
          class="grid grid-cols-2 gap-4 text-sm bg-gray-50 dark:bg-gray-800 p-4 rounded-lg"
        >
          <div>
            <span class="text-gray-500">관리자:</span>
            <span class="font-medium">{{ currentLog.adminName }}</span>
          </div>
          <div>
            <span class="text-gray-500">IP:</span>
            <span class="font-medium">{{ currentLog.adminIp }}</span>
          </div>
          <div>
            <span class="text-gray-500">활동:</span>
            <span class="uppercase font-bold">{{ currentLog.action }}</span>
          </div>
          <div>
            <span class="text-gray-500">일시:</span>
            <span>{{
              format(new Date(currentLog.createdAt), "yyyy-MM-dd HH:mm:ss")
            }}</span>
          </div>
        </div>

        <div>
          <p class="text-sm font-medium mb-2">변경 내역 / 상세 데이터</p>
          <div
            class="bg-gray-900 text-gray-100 p-4 rounded-lg font-mono text-xs overflow-auto max-h-60"
          >
            <pre>{{ currentLog.details }}</pre>
          </div>
        </div>

        <div class="flex justify-end pt-2">
          <UButton
            label="닫기"
            color="neutral"
            variant="ghost"
            @click="isModalOpen = false"
          />
        </div>
      </div>
    </template>
  </UModal>
</template>
