<script setup lang="ts">
/**
 * [마케팅 > 쿠폰 관리]
 * Base Code 아키텍처 기반 리팩토링
 */
import { h, ref, reactive, resolveComponent, watch, computed } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { FormSubmitEvent } from "@nuxt/ui";
import * as z from "zod";
import { format, addDays } from "date-fns";
import { getPaginationRowModel, getSortedRowModel } from "@tanstack/table-core";

// ==========================================
// 1. 컴포넌트 리졸브
// ==========================================
const UButton = resolveComponent("UButton");
const UBadge = resolveComponent("UBadge");
const UDropdownMenu = resolveComponent("UDropdownMenu");
const UCheckbox = resolveComponent("UCheckbox");
const UIcon = resolveComponent("UIcon");
const UInput = resolveComponent("UInput");

const toast = useToast();
const table = ref<any>(null);

// ==========================================
// 2. 설정 및 데이터 정의
// ==========================================
const PAGE_TITLE = "쿠폰 관리";
const DATA_KEY = "coupons";

type CouponItem = {
  id: number;
  name: string;
  code: string;
  discountType: "percent" | "amount"; // 퍼센트 할인 or 정액 할인
  discountValue: number;
  totalLimit: number | null; // null이면 무제한
  usedCount: number;
  startDate: string;
  endDate: string;
  status: "active" | "inactive" | "expired";
  createdAt: string;
};

// 폼 스키마
const formSchema = z.object({
  name: z.string().min(2, "쿠폰명을 입력해주세요."),
  code: z
    .string()
    .min(3, "쿠폰 코드를 입력해주세요.")
    .regex(/^[A-Z0-9]+$/, "영문 대문자와 숫자만 가능합니다."),
  discountType: z.enum(["percent", "amount"]),
  discountValue: z.number().min(1, "할인 값을 입력해주세요."),
  totalLimit: z.number().nullable().optional(), // 선택사항
  startDate: z.string(),
  endDate: z.string(),
  status: z.enum(["active", "inactive"]),
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. 상태 관리
// ==========================================
const columnFilters = ref([{ id: "name", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: "id", desc: true }]);

const statusFilter = ref("all");
const isModalOpen = ref(false);
const isEditMode = ref(false);
const selectedId = ref<number | null>(null);

// 폼 상태
const initialFormState: FormSchema = {
  name: "",
  code: "",
  discountType: "percent",
  discountValue: 10,
  totalLimit: 100,
  startDate: format(new Date(), "yyyy-MM-dd"),
  endDate: format(addDays(new Date(), 7), "yyyy-MM-dd"),
  status: "active",
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. 데이터 페칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<CouponItem[]>(
  DATA_KEY,
  async () => {
    return Array.from({ length: 50 }).map((_, i) => {
      const isPercent = Math.random() > 0.5;
      const limit = Math.random() > 0.8 ? null : 100 + i * 10;
      const used = limit
        ? Math.floor(Math.random() * limit)
        : Math.floor(Math.random() * 500);

      return {
        id: 50 - i,
        name: i % 2 === 0 ? `신규 가입 환영 쿠폰 ${i}` : `주말 깜짝 할인 ${i}`,
        code: `WELCOME${50 - i}${String.fromCharCode(65 + (i % 26))}`,
        discountType: isPercent ? "percent" : "amount",
        discountValue: isPercent
          ? (Math.floor(Math.random() * 5) + 1) * 10
          : (Math.floor(Math.random() * 10) + 1) * 1000,
        totalLimit: limit,
        usedCount: used,
        startDate: format(new Date(), "yyyy-MM-dd"),
        endDate: format(addDays(new Date(), 30), "yyyy-MM-dd"),
        status: i % 5 === 0 ? "inactive" : "active",
        createdAt: new Date().toISOString(),
      };
    });
  },
);

// ==========================================
// 5. 액션 핸들러
// ==========================================
function openCreateModal() {
  isEditMode.value = false;
  selectedId.value = null;
  Object.assign(formState, initialFormState);
  generateRandomCode(); // 신규 등록 시 코드 자동 생성 편의성
  isModalOpen.value = true;
}

function openEditModal(row: CouponItem) {
  isEditMode.value = true;
  selectedId.value = row.id;
  Object.assign(formState, {
    name: row.name,
    code: row.code,
    discountType: row.discountType,
    discountValue: row.discountValue,
    totalLimit: row.totalLimit,
    startDate: row.startDate,
    endDate: row.endDate,
    status: row.status === "expired" ? "inactive" : row.status,
  });
  isModalOpen.value = true;
}

async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  const action = isEditMode.value ? "수정" : "생성";
  toast.add({
    title: `${action} 완료`,
    description: `쿠폰이 성공적으로 ${action}되었습니다.`,
    color: "success",
  });
  isModalOpen.value = false;
}

function onDelete(ids: number[]) {
  toast.add({
    title: "삭제 완료",
    description: `${ids.length}개의 쿠폰이 삭제되었습니다.`,
    color: "error",
  });
  rowSelection.value = {};
}

// 랜덤 쿠폰 코드 생성기
function generateRandomCode() {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  let result = "";
  for (let i = 0; i < 8; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  formState.code = result;
}

function getRowItems(row: CouponItem) {
  return [
    { type: "label", label: "관리" },
    {
      label: "수정하기",
      icon: "i-lucide-edit",
      onSelect: () => openEditModal(row),
    },
    { type: "separator" },
    {
      label: "삭제",
      icon: "i-lucide-trash",
      color: "error",
      onSelect: () => onDelete([row.id]),
    },
  ];
}

// ==========================================
// 6. 테이블 컬럼 정의
// ==========================================
const columnLabels: Record<string, string> = {
  select: "선택",
  id: "No.",
  name: "쿠폰명",
  code: "코드",
  discount: "혜택",
  usage: "사용량",
  period: "유효기간",
  status: "상태",
  actions: "관리",
};

const columns: TableColumn<CouponItem>[] = [
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
        label: "No.",
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
    accessorKey: "name",
    header: "쿠폰명",
    cell: ({ row }) =>
      h("div", { class: "flex flex-col" }, [
        h(
          "span",
          {
            class:
              "font-medium cursor-pointer hover:underline hover:text-primary",
            onClick: () => openEditModal(row.original),
          },
          row.original.name,
        ),
        h("span", { class: "text-xs text-gray-500" }, row.original.code), // 코드 함께 표시
      ]),
  },
  {
    id: "discount",
    header: "할인 혜택",
    cell: ({ row }) => {
      const isPercent = row.original.discountType === "percent";
      const value = row.original.discountValue;
      return h(
        "span",
        { class: "font-bold text-primary-600 dark:text-primary-400" },
        isPercent ? `${value}% 할인` : `${value.toLocaleString()}원 할인`,
      );
    },
  },
  {
    id: "usage",
    header: "사용 현황",
    cell: ({ row }) => {
      const { usedCount, totalLimit } = row.original;
      const limitText = totalLimit ? `${totalLimit.toLocaleString()}` : "∞";
      const percent = totalLimit
        ? Math.round((usedCount / totalLimit) * 100)
        : 0;

      return h("div", { class: "flex items-center gap-2" }, [
        h(
          "span",
          { class: "text-sm" },
          `${usedCount.toLocaleString()} / ${limitText}`,
        ),
        totalLimit &&
          h(UBadge, { color: "gray", variant: "subtle" }, () => `${percent}%`),
      ]);
    },
  },
  {
    accessorKey: "status",
    header: "상태",
    cell: ({ row }) => {
      const map: Record<string, any> = {
        active: { label: "발급중", color: "success" },
        inactive: { label: "중지됨", color: "neutral" },
        expired: { label: "만료됨", color: "error" },
      };
      const status = map[row.original.status];
      return h(
        UBadge,
        { color: status.color, variant: "subtle" },
        () => status.label,
      );
    },
  },
  {
    id: "period",
    header: "유효 기간",
    cell: ({ row }) =>
      h("div", { class: "text-xs text-gray-500 flex flex-col" }, [
        h("span", `${row.original.startDate} ~`),
        h("span", row.original.endDate),
      ]),
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
watch(statusFilter, (val) => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("status")
    ?.setFilterValue(val === "all" ? undefined : val);
});

const nameSearch = computed({
  get: () =>
    (table.value?.tableApi?.getColumn("name")?.getFilterValue() as string) ||
    "",
  set: (val) =>
    table.value?.tableApi?.getColumn("name")?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="nameSearch"
        icon="i-lucide-search"
        placeholder="쿠폰명 검색..."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
        <UButton
          label="쿠폰 생성"
          icon="i-lucide-plus"
          color="primary"
          @click="openCreateModal"
        />
        <UButton
          v-if="table?.tableApi?.getFilteredSelectedRowModel().rows.length"
          label="선택 삭제"
          color="error"
          variant="subtle"
          icon="i-lucide-trash"
          @click="
            onDelete(
              table?.tableApi
                ?.getFilteredSelectedRowModel()
                .rows.map((r: any) => r.original.id),
            )
          "
        >
          <template #trailing
            ><UKbd>{{
              table?.tableApi?.getFilteredSelectedRowModel().rows.length
            }}</UKbd></template
          >
        </UButton>

        <USelect
          v-model="statusFilter"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '발급중', value: 'active' },
            { label: '중지됨', value: 'inactive' },
            { label: '만료됨', value: 'expired' },
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
        총 {{ table?.tableApi?.getFilteredRowModel().rows.length || 0 }}개 중
        {{ table?.tableApi?.getFilteredSelectedRowModel().rows.length || 0 }}개
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
    :title="isEditMode ? '쿠폰 수정' : '신규 쿠폰 생성'"
    :ui="{ wrapper: 'w-full sm:max-w-2xl' }"
  >
    <template #body>
      <UForm
        :schema="formSchema"
        :state="formState"
        class="space-y-4 p-4"
        @submit="onSubmit"
      >
        <UFormField label="쿠폰 이름" name="name" required class="w-full">
          <UInput
            v-model="formState.name"
            placeholder="예: 신규 가입 환영 쿠폰"
            class="w-full"
          />
        </UFormField>

        <UFormField label="쿠폰 코드" name="code" required class="w-full">
          <div class="flex gap-2">
            <UInput
              v-model="formState.code"
              placeholder="예: WELCOME2024"
              class="flex-1"
            />
            <UButton
              label="랜덤 생성"
              color="neutral"
              variant="outline"
              @click="generateRandomCode"
            />
          </div>
        </UFormField>

        <div class="grid grid-cols-2 gap-4">
          <UFormField
            label="할인 종류"
            name="discountType"
            required
            class="w-full"
          >
            <USelect
              v-model="formState.discountType"
              :items="[
                { label: '정액 할인 (원)', value: 'amount' },
                { label: '정률 할인 (%)', value: 'percent' },
              ]"
              class="w-full"
            />
          </UFormField>
          <UFormField
            label="할인 값"
            name="discountValue"
            required
            class="w-full"
          >
            <UInput
              v-model.number="formState.discountValue"
              type="number"
              :placeholder="
                formState.discountType === 'percent' ? '예: 10' : '예: 5000'
              "
              class="w-full"
            >
              <template #trailing>
                <span class="text-gray-500 text-sm">{{
                  formState.discountType === "percent" ? "%" : "원"
                }}</span>
              </template>
            </UInput>
          </UFormField>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <UFormField label="시작일" name="startDate" required class="w-full">
            <UInput v-model="formState.startDate" type="date" class="w-full" />
          </UFormField>
          <UFormField label="종료일" name="endDate" required class="w-full">
            <UInput v-model="formState.endDate" type="date" class="w-full" />
          </UFormField>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <UFormField
            label="총 발급 수량 (비워두면 무제한)"
            name="totalLimit"
            class="w-full"
          >
            <UInput
              v-model.number="formState.totalLimit"
              type="number"
              placeholder="무제한"
              class="w-full"
            />
          </UFormField>
          <UFormField label="활성 상태" name="status" required class="w-full">
            <USelect
              v-model="formState.status"
              :items="[
                { label: '활성 (발급 가능)', value: 'active' },
                { label: '비활성 (중지)', value: 'inactive' },
              ]"
              class="w-full"
            />
          </UFormField>
        </div>

        <div
          class="flex justify-end gap-2 pt-4 border-t border-default mt-auto"
        >
          <UButton
            label="취소"
            color="neutral"
            variant="ghost"
            @click="isModalOpen = false"
          />
          <UButton
            type="submit"
            :label="isEditMode ? '수정 완료' : '쿠폰 생성'"
            color="primary"
          />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
