<script setup lang="ts">
import type { TableColumn } from "@nuxt/ui";
import { upperFirst } from "scule";
import { getPaginationRowModel } from "@tanstack/table-core";
import type { Row } from "@tanstack/table-core";
import { format } from "date-fns";
import {
  STORE_CATEGORY_FILTER_OPTIONS,
  getStoreCategoryLabel,
} from "~/utils/storeCategories";

// 1. 데이터 타입 정의 — Backend StoreDto 기반
type Store = {
  id: string;
  name: string;
  ownerId: string;
  category: string;
  address: string;
  status: string;
  minOrderAmount: number;
  ratingAvg: number;
  phone: string;
  description: string;
  openingHours: string;
};

// 2. 컴포넌트 수동 리졸브
const CompButton = resolveComponent("UButton");
const CompBadge = resolveComponent("UBadge");
const CompDropdownMenu = resolveComponent("UDropdownMenu");
const CompCheckbox = resolveComponent("UCheckbox");
const CompAvatar = resolveComponent("UAvatar");

const toast = useToast();
const table = useTemplateRef<any>("table"); // 순환 참조 방지용 any 타입

// 3. 상태 관리
const searchFilter = ref("");
const statusFilter = ref("all");
const locationFilter = ref("all");
const categoryFilter = ref("all");

const columnFilters = ref([{ id: "name", value: "" }]);
const columnVisibility = ref();
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });

// 4. Backend API에서 매장 목록 조회
const { $api } = useApi();
const loadingStatus = ref<"idle" | "pending" | "success" | "error">("idle");
const data = ref<Store[]>([]);

async function fetchStores() {
  loadingStatus.value = "pending";
  try {
    const res = await $api<Store[]>("/api/v1/stores");
    data.value = res.success ? res.data : [];
    loadingStatus.value = "success";
  } catch {
    loadingStatus.value = "error";
    data.value = [];
  }
}
await fetchStores();

// 5. 유틸리티 함수
function formatCurrency(value: number) {
  return new Intl.NumberFormat("ko-KR", {
    style: "currency",
    currency: "KRW",
  }).format(value);
}

// 컬럼 한글 매핑 (컬럼 설정 메뉴용)
const columnLabels: Record<string, string> = {
  select: "선택",
  id: "ID",
  name: "매장명",
  ownerId: "점주",
  category: "업종",
  address: "주소",
  minOrderAmount: "최소주문",
  status: "상태",
  ratingAvg: "평점",
  actions: "관리",
};

// 6. 행(Row) 액션 메뉴
function getRowItems(row: Row<Store>) {
  return [
    { type: "label", label: "매장 관리" },
    {
      label: "메뉴 관리",
      icon: "i-lucide-utensils",
      onSelect: () => navigateTo(`/stores/${row.original.id}/menus`),
    },
    {
      label: "매장 상세 정보",
      icon: "i-lucide-store",
      onSelect: () => console.log("View", row.original.id),
    },
    {
      label: "정산 내역 보기",
      icon: "i-lucide-receipt-text",
    },
    { type: "separator" },
    {
      label: "영업 상태 변경",
      children: [
        {
          label: "영업중",
          click: () =>
            toast.add({ title: "상태 변경", description: "영업중으로 변경됨" }),
        },
        { label: "휴업", click: () => {} },
        { label: "승인 대기", click: () => {} },
      ],
    },
    { type: "separator" },
    {
      label: "매장 삭제",
      icon: "i-lucide-trash",
      color: "error",
      onSelect: () => toast.add({ title: "삭제 완료", color: "error" }),
    },
  ];
}

// 7. 테이블 컬럼 정의
const columns: TableColumn<Store>[] = [
  {
    id: "select",
    header: ({ table }) =>
      h(CompCheckbox, {
        modelValue: table.getIsSomePageRowsSelected()
          ? "indeterminate"
          : table.getIsAllPageRowsSelected(),
        "onUpdate:modelValue": (v: boolean) =>
          table.toggleAllPageRowsSelected(!!v),
        ariaLabel: "전체 선택",
      }),
    cell: ({ row }) =>
      h(CompCheckbox, {
        modelValue: row.getIsSelected(),
        "onUpdate:modelValue": (v: boolean) => row.toggleSelected(!!v),
        ariaLabel: "행 선택",
      }),
  },
  {
    accessorKey: "name",
    header: "매장 정보",
    cell: ({ row }) =>
      h("div", { class: "flex items-center gap-3" }, [
        h(CompAvatar, { src: undefined, alt: row.original.name, size: "sm" }),
        h("div", { class: "flex flex-col" }, [
          h(
            "span",
            { class: "font-medium text-highlighted" },
            row.original.name,
          ),
          h(
            "span",
            { class: "text-xs text-muted" },
            getStoreCategoryLabel(row.original.category),
          ),
        ]),
      ]),
  },
  {
    accessorKey: "ownerId",
    header: "점주",
    cell: ({ row }) => row.original.ownerId || "-",
  },
  {
    accessorKey: "category",
    header: "업종",
    filterFn: "equals",
    cell: ({ row }) =>
      h(
        CompBadge,
        { variant: "subtle", color: "neutral" },
        () => getStoreCategoryLabel(row.original.category),
      ),
  },
  {
    accessorKey: "address",
    header: "주소",
    filterFn: "includesString",
    cell: ({ row }) =>
      h("span", { class: "text-sm" }, row.original.address || "-"),
  },
  {
    accessorKey: "minOrderAmount",
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(CompButton, {
        color: "neutral",
        variant: "ghost",
        label: "최소주문",
        icon: isSorted
          ? isSorted === "asc"
            ? "i-lucide-arrow-up"
            : "i-lucide-arrow-down"
          : "i-lucide-arrow-up-down",
        class: "-mx-2.5",
        onClick: () => column.toggleSorting(column.getIsSorted() === "asc"),
      });
    },
    cell: ({ row }) =>
      h(
        "span",
        { class: "font-mono" },
        formatCurrency(row.original.minOrderAmount || 0),
      ),
  },
  {
    accessorKey: "status",
    header: "상태",
    filterFn: "equals",
    cell: ({ row }) => {
      const config: Record<string, any> = {
        active: { color: "success", label: "영업중" },
        pending: { color: "warning", label: "승인 대기" },
        closed: { color: "neutral", label: "휴업/폐업" },
        suspended: { color: "error", label: "정지됨" },
      };
      const status = config[row.original.status] || {
        color: "gray",
        label: row.original.status,
      };

      return h(
        CompBadge,
        { variant: "subtle", color: status.color, class: "capitalize" },
        () => status.label,
      );
    },
  },
  {
    accessorKey: "ratingAvg",
    header: "평점",
    cell: ({ row }) =>
      h(
        "span",
        {},
        row.original.ratingAvg
          ? `⭐ ${row.original.ratingAvg.toFixed(1)}`
          : "-",
      ),
  },
  {
    id: "actions",
    cell: ({ row }) =>
      h(
        "div",
        { class: "text-right" },
        h(
          CompDropdownMenu,
          { content: { align: "end" }, items: getRowItems(row) },
          () =>
            h(CompButton, {
              icon: "i-lucide-ellipsis-vertical",
              color: "neutral",
              variant: "ghost",
            }),
        ),
      ),
  },
];

// 8. 필터 로직 연결
watch(searchFilter, (val) => {
  // 매장명 검색
  table.value?.tableApi?.getColumn("name")?.setFilterValue(val);
});

watch(statusFilter, (val) => {
  table.value?.tableApi
    ?.getColumn("status")
    ?.setFilterValue(val === "all" ? undefined : val);
});

watch(locationFilter, (val) => {
  table.value?.tableApi
    ?.getColumn("address")
    ?.setFilterValue(val === "all" ? undefined : val);
});

watch(categoryFilter, (val) => {
  table.value?.tableApi
    ?.getColumn("category")
    ?.setFilterValue(val === "all" ? undefined : val);
});
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
            v-model="categoryFilter"
            :items="STORE_CATEGORY_FILTER_OPTIONS"
            class="min-w-36"
          />

          <USelect
            v-model="locationFilter"
            :items="[
              { label: '전체 지역', value: 'all' },
              { label: '서울', value: '서울' },
              { label: '경기', value: '경기' },
              { label: '부산', value: '부산' },
              { label: '제주', value: '제주' },
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
              { label: '정지됨', value: 'suspended' },
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
                    table?.tableApi
                      ?.getColumn(column.id)
                      ?.toggleVisibility(!!checked);
                  },
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
          separator: 'h-0',
        }"
      />

      <div
        class="flex items-center justify-between gap-3 border-t border-default pt-4 mt-auto"
      >
        <div class="text-sm text-muted">
          총 {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}개
          매장 조회됨
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
    </template>
  </UDashboardPanel>
</template>
