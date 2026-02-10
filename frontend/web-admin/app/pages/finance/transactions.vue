<script setup lang="ts">
/**
 * [재무 > 결제/정산 관리]
 * Base Code 아키텍처 기반 리팩토링
 */
import { h, ref, reactive, resolveComponent, watch, computed } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { FormSubmitEvent } from "@nuxt/ui";
import * as z from "zod";
import { format, subDays, subHours } from "date-fns";
import { getPaginationRowModel, getSortedRowModel } from "@tanstack/table-core";

// ==========================================
// 1. 컴포넌트 리졸브
// ==========================================
const UButton = resolveComponent("UButton");
const UBadge = resolveComponent("UBadge");
const UDropdownMenu = resolveComponent("UDropdownMenu");
const UCheckbox = resolveComponent("UCheckbox");
const URadioGroup = resolveComponent("URadioGroup");

const toast = useToast();
const table = ref<any>(null);

// ==========================================
// 2. 설정 및 데이터 정의
// ==========================================
const PAGE_TITLE = "결제/정산 관리";
const DATA_KEY = "transactions";

type TransactionItem = {
  id: string; // 주문번호/거래ID (String)
  type: "payment" | "refund" | "settlement";
  amount: number;
  method: "card" | "bank" | "point";
  status: "success" | "pending" | "failed" | "cancelled";
  user: string;
  description: string;
  createdAt: string;
  note?: string; // 관리자 비고
};

// 폼 스키마 (상태 변경 및 비고 입력)
const formSchema = z.object({
  action: z.enum(["refund", "approve", "reject", "none"]),
  note: z.string().optional(),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. 상태 관리
// ==========================================
const columnFilters = ref([{ id: "id", value: "" }]); // ID 검색
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: "createdAt", desc: true }]);

const typeFilter = ref("all");
const statusFilter = ref("all");
const isModalOpen = ref(false);
const selectedId = ref<string | null>(null);

// 상세 정보 표시용 (읽기 전용)
const currentTx = ref<TransactionItem | null>(null);

// 폼 상태
const initialFormState: FormSchema = { action: "none", note: "" };
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. 데이터 페칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<TransactionItem[]>(
  DATA_KEY,
  async () => {
    // 1. 배열 정의를 map 함수 밖으로 이동 (성능 및 가독성 최적화)
    // 2. 'as const'를 사용하여 리터럴 타입으로 고정
    const types = [
      "payment",
      "payment",
      "payment",
      "refund",
      "settlement",
    ] as const;
    const methods = ["card", "bank", "point"] as const;
    // TransactionItem 타입에 'cancelled'가 있다면 배열에도 포함하거나, Mock 데이터에서 제외해도 됩니다.
    const statuses = [
      "success",
      "success",
      "pending",
      "failed",
      "cancelled",
    ] as const;

    return Array.from({ length: 50 }).map((_, i) => {
      // 인덱스 순환을 통해 값 선택
      const type = types[i % types.length] as
        | "payment"
        | "refund"
        | "settlement";
      const method = methods[i % methods.length] as "card" | "bank" | "point";
      const status = statuses[i % statuses.length] as
        | "success"
        | "pending"
        | "failed"
        | "cancelled";

      // 금액 랜덤 생성
      const amount = (Math.floor(Math.random() * 100) + 1) * 1000;

      return {
        id: `ORD-${20240000 + i}-${String.fromCharCode(65 + (i % 26))}`,
        type: type,
        amount: type === "refund" ? -amount : amount, // 환불은 음수 처리
        method: method,
        status: status,
        user: `User_${i}`,
        description:
          type === "settlement" ? "10월 판매 수익 정산" : `상품 구매 #${i}`,
        // date-fns 함수 사용 (import 필요)
        createdAt: subHours(subDays(new Date(), i % 10), i).toISOString(),
        note: "",
      };
    });
  },
);

// ==========================================
// 5. 액션 핸들러
// ==========================================
function openDetailModal(row: TransactionItem) {
  currentTx.value = row;
  selectedId.value = row.id;

  // 폼 초기화
  Object.assign(formState, {
    action: "none",
    note: row.note || "",
  });
  isModalOpen.value = true;
}

async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  if (currentTx.value) {
    // Mock Update Logic
    if (formState.action === "refund") {
      currentTx.value.status = "cancelled";
      currentTx.value.amount = -Math.abs(currentTx.value.amount); // 환불 처리 시 음수 전환 등 로직
      currentTx.value.type = "refund";
    } else if (formState.action === "approve") {
      currentTx.value.status = "success";
    } else if (formState.action === "reject") {
      currentTx.value.status = "failed";
    }
    currentTx.value.note = formState.note;
  }

  toast.add({
    title: "처리 완료",
    description: "거래 상태가 업데이트되었습니다.",
    color: "success",
  });
  isModalOpen.value = false;
}

function onExport() {
  toast.add({
    title: "엑셀 다운로드",
    description: "요청하신 기간의 정산 내역을 다운로드합니다.",
    color: "primary",
  });
}

// 금액 포맷터
const currencyFormatter = new Intl.NumberFormat("ko-KR", {
  style: "currency",
  currency: "KRW",
});

function getRowItems(row: TransactionItem) {
  return [
    { type: "label", label: "관리" },
    {
      label: "상세 및 처리",
      icon: "i-lucide-file-text",
      onSelect: () => openDetailModal(row),
    },
    { type: "separator" },
    {
      label: "영수증 출력",
      icon: "i-lucide-printer",
      onSelect: () =>
        toast.add({ title: "출력", description: "영수증 팝업을 엽니다." }),
    },
  ];
}

// ==========================================
// 6. 테이블 컬럼 정의
// ==========================================
const columnLabels: Record<string, string> = {
  select: "선택",
  id: "거래번호",
  type: "구분",
  description: "내용",
  amount: "금액",
  method: "수단",
  status: "상태",
  createdAt: "일시",
  actions: "관리",
};

const columns: TableColumn<TransactionItem>[] = [
  {
    id: "select",
    header: ({ table }) =>
      h(UCheckbox, {
        modelValue: table.getIsSomePageRowsSelected()
          ? "indeterminate"
          : table.getIsAllPageRowsSelected(),
        "onUpdate:modelValue": (v: boolean) =>
          table.toggleAllPageRowsSelected(!!v),
        ariaLabel: "전체 선택",
      }),
    cell: ({ row }) =>
      h(UCheckbox, {
        modelValue: row.getIsSelected(),
        "onUpdate:modelValue": (v: boolean) => row.toggleSelected(!!v),
        ariaLabel: "행 선택",
      }),
    enableSorting: false,
  },
  {
    accessorKey: "id",
    header: ({ column }) => {
      const isSorted = column.getIsSorted();
      return h(UButton, {
        color: "neutral",
        variant: "ghost",
        label: "거래번호",
        icon:
          isSorted === "asc"
            ? "i-lucide-arrow-up-narrow-wide"
            : isSorted === "desc"
              ? "i-lucide-arrow-down-wide-narrow"
              : "i-lucide-arrow-up-down",
        class: "-ml-2.5 font-bold hover:bg-gray-100 dark:hover:bg-gray-800",
        onClick: () => column.toggleSorting(column.getIsSorted() === "asc"),
      });
    },
  },
  {
    accessorKey: "type",
    header: "구분",
    cell: ({ row }) => {
      const map: Record<string, any> = {
        payment: { label: "결제", color: "primary" },
        refund: { label: "환불", color: "error" },
        settlement: { label: "정산", color: "orange" },
      };
      const info = map[row.original.type];
      return h(
        UBadge,
        { color: info.color, variant: "subtle", size: "xs" },
        () => info.label,
      );
    },
  },
  {
    accessorKey: "description",
    header: "내용",
    cell: ({ row }) =>
      h("div", { class: "flex flex-col max-w-[200px]" }, [
        h(
          "span",
          {
            class:
              "truncate font-medium cursor-pointer hover:underline hover:text-primary",
            onClick: () => openDetailModal(row.original),
          },
          row.original.description,
        ),
        h("span", { class: "text-xs text-gray-500" }, row.original.user),
      ]),
  },
  {
    accessorKey: "amount",
    header: "금액",
    cell: ({ row }) => {
      const amt = row.original.amount;
      let colorClass = "text-gray-900 dark:text-white";
      if (row.original.type === "refund")
        colorClass = "text-red-600 dark:text-red-400";
      if (row.original.type === "settlement")
        colorClass = "text-blue-600 dark:text-blue-400";

      return h(
        "span",
        { class: `font-bold ${colorClass}` },
        currencyFormatter.format(amt),
      );
    },
  },
  {
    accessorKey: "status",
    header: "상태",
    cell: ({ row }) => {
      const map: Record<string, any> = {
        success: { label: "성공", color: "success" },
        pending: { label: "대기", color: "warning" },
        failed: { label: "실패", color: "error" },
        cancelled: { label: "취소됨", color: "neutral" },
      };
      const info = map[row.original.status];
      return h(
        UBadge,
        { color: info.color, variant: "subtle", size: "xs" },
        () => info.label,
      );
    },
  },
  {
    accessorKey: "method",
    header: "수단",
    cell: ({ row }) => {
      const map: Record<string, string> = {
        card: "카드",
        bank: "계좌",
        point: "포인트",
      };
      return map[row.original.method];
    },
  },
  {
    accessorKey: "createdAt",
    header: "일시",
    cell: ({ row }) =>
      format(new Date(row.original.createdAt), "yyyy-MM-dd HH:mm"),
  },
  {
    id: "actions",
    cell: ({ row }) =>
      h(
        "div",
        { class: "text-right" },
        h(
          UDropdownMenu,
          {
            content: { align: "end" },
            items: getRowItems(row.original),
          },
          () =>
            h(UButton, {
              icon: "i-lucide-ellipsis-vertical",
              color: "neutral",
              variant: "ghost",
              class: "ml-auto",
            }),
        ),
      ),
    enableSorting: false,
  },
];

// ==========================================
// 7. Watchers & Computeds
// ==========================================
watch([typeFilter, statusFilter], () => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("type")
    ?.setFilterValue(typeFilter.value === "all" ? undefined : typeFilter.value);
  table.value.tableApi
    .getColumn("status")
    ?.setFilterValue(
      statusFilter.value === "all" ? undefined : statusFilter.value,
    );
});

const searchInput = computed({
  get: () =>
    (table.value?.tableApi?.getColumn("id")?.getFilterValue() as string) || "",
  set: (val) =>
    table.value?.tableApi?.getColumn("id")?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="searchInput"
        icon="i-lucide-search"
        placeholder="거래번호 검색..."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
        <UButton
          label="엑셀 다운로드"
          icon="i-lucide-download"
          color="neutral"
          variant="outline"
          @click="onExport"
        />
        <USelect
          v-model="typeFilter"
          :items="[
            { label: '전체 구분', value: 'all' },
            { label: '결제', value: 'payment' },
            { label: '환불', value: 'refund' },
            { label: '정산', value: 'settlement' },
          ]"
          class="min-w-32"
        />
        <USelect
          v-model="statusFilter"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '성공', value: 'success' },
            { label: '대기', value: 'pending' },
            { label: '실패', value: 'failed' },
          ]"
          class="min-w-32"
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
      v-model:row-selection="rowSelection"
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
        총 {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}건 중
        {{ table?.tableApi?.getFilteredSelectedRowModel().rows.length || 0 }}건
        선택됨.
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
    title="거래 상세 정보"
    :ui="{ wrapper: 'w-full sm:max-w-2xl' }"
  >
    <template #body>
      <UForm
        :schema="formSchema"
        :state="formState"
        class="space-y-4 p-4"
        @submit="onSubmit"
      >
        <div
          v-if="currentTx"
          class="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg space-y-3 border border-gray-100 dark:border-gray-700"
        >
          <div class="flex justify-between items-start">
            <div class="flex flex-col">
              <span class="text-xs text-gray-500 font-mono">{{
                currentTx.id
              }}</span>
              <span
                class="font-bold text-lg text-gray-900 dark:text-white mt-1"
                >{{ currentTx.description }}</span
              >
            </div>
            <div class="text-right">
              <UBadge
                :color="
                  currentTx.status === 'success'
                    ? 'success'
                    : currentTx.status === 'pending'
                      ? 'warning'
                      : 'error'
                "
                variant="solid"
              >
                {{ currentTx.status.toUpperCase() }}
              </UBadge>
            </div>
          </div>

          <hr class="border-gray-200 dark:border-gray-700/50" />

          <div class="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p class="text-gray-500 text-xs">결제 금액</p>
              <p
                class="font-bold text-xl mt-1"
                :class="
                  currentTx.type === 'refund'
                    ? 'text-red-500'
                    : 'text-gray-900 dark:text-white'
                "
              >
                {{ currencyFormatter.format(currentTx.amount) }}
              </p>
            </div>
            <div>
              <p class="text-gray-500 text-xs">결제 수단</p>
              <p class="font-medium mt-1">
                {{
                  currentTx.method === "card"
                    ? "신용카드"
                    : currentTx.method === "bank"
                      ? "계좌이체"
                      : "포인트"
                }}
              </p>
            </div>
            <div>
              <p class="text-gray-500 text-xs">거래 일시</p>
              <p class="font-medium mt-1">
                {{
                  format(new Date(currentTx.createdAt), "yyyy-MM-dd HH:mm:ss")
                }}
              </p>
            </div>
            <div>
              <p class="text-gray-500 text-xs">사용자</p>
              <p class="font-medium mt-1">{{ currentTx.user }}</p>
            </div>
          </div>
        </div>

        <div v-if="currentTx" class="pt-2">
          <div
            v-if="
              currentTx.status === 'success' && currentTx.type === 'payment'
            "
            class="bg-red-50 dark:bg-red-900/10 p-4 rounded border border-red-100 dark:border-red-900/30 mb-4"
          >
            <p class="text-sm font-bold text-red-600 mb-2">환불 처리</p>
            <UCheckbox
              v-model="formState.action"
              :true-value="'refund'"
              :false-value="'none'"
              label="이 결제 건을 취소하고 환불 처리합니다."
              color="error"
            />
          </div>

          <div
            v-if="
              currentTx.status === 'pending' && currentTx.type === 'settlement'
            "
            class="bg-blue-50 dark:bg-blue-900/10 p-4 rounded border border-blue-100 dark:border-blue-900/30 mb-4"
          >
            <p class="text-sm font-bold text-blue-600 mb-2">정산 승인/반려</p>
            <URadioGroup
              v-model="formState.action"
              :items="[
                { value: 'approve', label: '정산 승인' },
                { value: 'reject', label: '정산 반려' },
              ]"
              class="flex gap-4"
            />
          </div>

          <UFormField label="관리자 메모" name="note" class="w-full">
            <UTextarea
              v-model="formState.note"
              :rows="3"
              placeholder="특이사항이나 처리 사유를 입력하세요."
              autoresize
              class="w-full"
            />
          </UFormField>
        </div>

        <div
          class="flex justify-end gap-2 pt-4 border-t border-default mt-auto"
        >
          <UButton
            label="닫기"
            color="neutral"
            variant="ghost"
            @click="isModalOpen = false"
          />
          <UButton type="submit" label="저장하기" color="primary" />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
