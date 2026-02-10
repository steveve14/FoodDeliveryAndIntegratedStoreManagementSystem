<script setup lang="ts">
/**
 * [리뷰 관리]
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
const UIcon = resolveComponent("UIcon");
const UAvatar = resolveComponent("UAvatar");

const toast = useToast();
const table = ref<any>(null);

// ==========================================
// 2. 설정 및 데이터 정의
// ==========================================
const PAGE_TITLE = "리뷰 관리";
const DATA_KEY = "reviews";

type ReviewItem = {
  id: number;
  storeName: string;
  userName: string;
  rating: number; // 1 ~ 5
  content: string;
  reply?: string; // 사장님 답글
  status: "active" | "hidden"; // 게시중 / 숨김
  createdAt: string;
  imageUrl?: string; // 리뷰 사진
};

// 폼 스키마 (답글 및 상태 변경)
const formSchema = z.object({
  reply: z.string().optional(),
  status: z.enum(["active", "hidden"]),
  isHidden: z.boolean().default(false), // 체크박스 바인딩용
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. 상태 관리
// ==========================================
const columnFilters = ref([{ id: "storeName", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref([{ id: "id", desc: true }]);

const statusFilter = ref("all");
const isModalOpen = ref(false);
const selectedId = ref<number | null>(null);

// 상세 정보 표시용 (읽기 전용)
const currentReview = ref<ReviewItem | null>(null);

// 폼 상태
const initialFormState: FormSchema = {
  reply: "",
  status: "active",
  isHidden: false,
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. 데이터 페칭 (Mock Data)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<ReviewItem[]>(
  DATA_KEY,
  async () => {
    return Array.from({ length: 50 }).map((_, i) => {
      const rating = Math.floor(Math.random() * 5) + 1;
      const hasReply = i % 3 === 0;
      const isHidden = i % 10 === 0; // 10% 확률로 숨김 처리됨

      return {
        id: 50 - i,
        storeName: `맛있는 가게 ${(i % 5) + 1}호점`,
        userName: `Customer_${i}`,
        rating: rating,
        content:
          rating > 3
            ? "정말 맛있어요! 배달도 빠르고 최고입니다. 재주문 의사 100%!"
            : "음식은 괜찮은데 배달이 조금 늦었네요. 식어서 왔습니다.",
        status: isHidden ? "hidden" : "active",
        createdAt: subHours(subDays(new Date(), i % 7), i * 3).toISOString(),
        reply: hasReply
          ? "소중한 리뷰 감사합니다! 더 노력하는 가게가 되겠습니다."
          : undefined,
        imageUrl:
          Math.random() > 0.7
            ? `https://picsum.photos/seed/${i}/200/200`
            : undefined,
      };
    });
  },
);

// ==========================================
// 5. 액션 핸들러
// ==========================================
function openDetailModal(row: ReviewItem) {
  currentReview.value = row;
  selectedId.value = row.id;

  Object.assign(formState, {
    reply: row.reply || "",
    status: row.status,
    isHidden: row.status === "hidden",
  });
  isModalOpen.value = true;
}

async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  if (currentReview.value) {
    // Mock Data 업데이트
    currentReview.value.reply = formState.reply;
    currentReview.value.status = formState.isHidden ? "hidden" : "active";
  }

  toast.add({
    title: "저장 완료",
    description: "리뷰 상태 및 답글이 저장되었습니다.",
    color: "success",
  });
  isModalOpen.value = false;
}

function onDelete(ids: number[]) {
  toast.add({
    title: "삭제 완료",
    description: `${ids.length}개의 리뷰가 삭제되었습니다.`,
    color: "error",
  });
  rowSelection.value = {};
}

function getRowItems(row: ReviewItem) {
  return [
    { type: "label", label: "관리" },
    {
      label: "상세 및 답글",
      icon: "i-lucide-message-square",
      onSelect: () => openDetailModal(row),
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

// 별점 렌더링 헬퍼
function renderStars(rating: number) {
  return h("div", { class: "flex items-center gap-0.5" }, [
    ...Array.from({ length: 5 }).map((_, i) =>
      h(UIcon, {
        name: "i-lucide-star",
        class:
          i < rating
            ? "w-4 h-4 text-yellow-500 fill-current"
            : "w-4 h-4 text-gray-300",
      }),
    ),
  ]);
}

// ==========================================
// 6. 테이블 컬럼 정의
// ==========================================
const columnLabels: Record<string, string> = {
  select: "선택",
  id: "No.",
  storeName: "매장명",
  rating: "평점",
  content: "내용",
  userName: "작성자",
  status: "상태",
  createdAt: "작성일",
  actions: "관리",
};

const columns: TableColumn<ReviewItem>[] = [
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
    accessorKey: "storeName",
    header: "매장명",
  },
  {
    accessorKey: "rating",
    header: "평점",
    cell: ({ row }) => renderStars(row.original.rating),
  },
  {
    accessorKey: "content",
    header: "내용",
    cell: ({ row }) =>
      h("div", { class: "flex items-center gap-2 max-w-[300px]" }, [
        row.original.imageUrl &&
          h(UAvatar, { src: row.original.imageUrl, size: "2xs" }),
        h(
          "span",
          {
            class: "truncate cursor-pointer hover:underline hover:text-primary",
            onClick: () => openDetailModal(row.original),
          },
          row.original.content,
        ),
      ]),
  },
  { accessorKey: "userName", header: "작성자" },
  {
    accessorKey: "status",
    header: "상태",
    filterFn: (row, columnId, filterValue) => {
      if (filterValue === "replied") return !!row.original.reply;
      if (filterValue === "waiting") return !row.original.reply;
      if (filterValue === "hidden") return row.original.status === "hidden";
      return true;
    },
    cell: ({ row }) => {
      if (row.original.status === "hidden") {
        return h(
          UBadge,
          { color: "error", variant: "subtle" },
          () => "숨김 처리됨",
        );
      }
      return row.original.reply
        ? h(UBadge, { color: "success", variant: "subtle" }, () => "답변 완료")
        : h(UBadge, { color: "warning", variant: "subtle" }, () => "답변 대기");
    },
  },
  {
    accessorKey: "createdAt",
    header: "작성일",
    cell: ({ row }) => format(new Date(row.original.createdAt), "yyyy-MM-dd"),
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
  // 커스텀 필터 로직 적용을 위해 status 컬럼에 값 전달
  table.value.tableApi
    .getColumn("status")
    ?.setFilterValue(val === "all" ? undefined : val);
});

const storeSearch = computed({
  get: () =>
    (table.value?.tableApi
      ?.getColumn("storeName")
      ?.getFilterValue() as string) || "",
  set: (val) =>
    table.value?.tableApi
      ?.getColumn("storeName")
      ?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="storeSearch"
        icon="i-lucide-search"
        placeholder="매장명 검색..."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
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
            { label: '전체 보기', value: 'all' },
            { label: '답변 대기', value: 'waiting' },
            { label: '답변 완료', value: 'replied' },
            { label: '숨김 리뷰', value: 'hidden' },
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
    title="리뷰 상세 및 답글 관리"
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
          v-if="currentReview"
          class="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg space-y-3 border border-gray-100 dark:border-gray-700"
        >
          <div class="flex justify-between items-start">
            <div class="flex flex-col">
              <div class="flex items-center gap-2 mb-1">
                <span class="font-bold text-gray-900 dark:text-white">{{
                  currentReview.storeName
                }}</span>
                <span class="text-xs text-gray-500"
                  >| {{ currentReview.userName }}</span
                >
              </div>
              <div class="flex items-center gap-1">
                <UIcon
                  v-for="i in 5"
                  :key="i"
                  name="i-lucide-star"
                  :class="
                    i <= currentReview.rating
                      ? 'text-yellow-500 fill-current'
                      : 'text-gray-300'
                  "
                  class="w-4 h-4"
                />
                <span class="text-sm font-bold ml-1"
                  >{{ currentReview.rating }}.0</span
                >
              </div>
            </div>
            <div class="text-xs text-gray-500">
              {{
                format(new Date(currentReview.createdAt), "yyyy-MM-dd HH:mm")
              }}
            </div>
          </div>

          <hr class="border-gray-200 dark:border-gray-700/50" />

          <p
            class="text-sm text-gray-700 dark:text-gray-300 whitespace-pre-wrap"
          >
            {{ currentReview.content }}
          </p>

          <div v-if="currentReview.imageUrl" class="mt-2">
            <img
              :src="currentReview.imageUrl"
              class="rounded-md border w-full max-h-60 object-cover"
            />
          </div>
        </div>

        <UFormField label="사장님 답글" name="reply" class="w-full">
          <UTextarea
            v-model="formState.reply"
            :rows="4"
            placeholder="고객님께 감사의 댓글을 남겨주세요."
            autoresize
            class="w-full"
          />
        </UFormField>

        <div
          class="flex items-center justify-between border-t border-gray-100 dark:border-gray-800 pt-4"
        >
          <div class="flex flex-col">
            <span class="text-sm font-medium text-gray-900 dark:text-white"
              >리뷰 숨김(Blind) 처리</span
            >
            <span class="text-xs text-gray-500"
              >부적절한 리뷰일 경우 체크해주세요.</span
            >
          </div>
          <UCheckbox
            v-model="formState.isHidden"
            label="이 리뷰를 숨김 처리합니다."
            color="error"
          />
        </div>

        <div class="flex justify-end gap-2 pt-2 mt-auto">
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
