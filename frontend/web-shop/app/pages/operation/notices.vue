<script setup lang="ts">
/**
 * [필수 임포트]
 * Vue 핵심 기능 및 UI 라이브러리 타입, 테이블 코어 함수 등을 불러옵니다.
 */
import { h, ref, reactive, resolveComponent, watch, computed } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { FormSubmitEvent } from "@nuxt/ui";
import * as z from "zod"; // 유효성 검사 라이브러리
import { format } from "date-fns"; // 날짜 포맷팅

// [중요] TanStack Table의 정렬 및 페이지네이션 코어 함수
import { getPaginationRowModel, getSortedRowModel } from "@tanstack/table-core";

// ==========================================
// 1. 컴포넌트 리졸브 (Nuxt UI 컴포넌트)
// ==========================================
// h() 함수 내에서 사용하기 위해 컴포넌트를 미리 해결(resolve)합니다.
const UButton = resolveComponent("UButton");
const UBadge = resolveComponent("UBadge");
const UDropdownMenu = resolveComponent("UDropdownMenu");
const UCheckbox = resolveComponent("UCheckbox");
const UIcon = resolveComponent("UIcon");
const UAvatar = resolveComponent("UAvatar"); // [추가] 리스트 내 이미지 표시용

const toast = useToast();
const table = ref<any>(null); // 테이블 인스턴스 참조

// ==========================================
// 2. 타입 및 데이터 모델 정의
// ==========================================
const PAGE_TITLE = "공지사항 관리";
const DATA_KEY = "notices";

// 공지사항 데이터 타입 정의
type NoticeItem = {
  id: number;
  title: string;
  content: string;
  status: "active" | "inactive" | "scheduled";
  category: "general" | "urgent" | "event";
  createdAt: string;
  author: string;
  isPinned: boolean;
  imageUrl?: string; // [추가] 이미지 URL (옵셔널)
};

// Zod를 이용한 폼 유효성 검사 스키마
const formSchema = z.object({
  title: z.string().min(2, "제목은 2글자 이상 입력해주세요."),
  category: z.enum(["general", "urgent", "event"]).catch("general"),
  status: z.enum(["active", "inactive", "scheduled"]).catch("active"),
  content: z.string().min(1, "내용을 입력해주세요."),
  isPinned: z.boolean().default(false),
  imageUrl: z.string().optional(), // [추가] 이미지 필드 유효성
});

type FormSchema = z.output<typeof formSchema>;

// ==========================================
// 3. 상태 관리 (State Management)
// ==========================================
// 테이블 관련 상태
const columnFilters = ref([{ id: "title", value: "" }]);
const columnVisibility = ref({});
const rowSelection = ref({});
const pagination = ref({ pageIndex: 0, pageSize: 10 });

// UI 상태
const statusFilter = ref("all");
const isSlideoverOpen = ref(false);
const isEditMode = ref(false);
const selectedId = ref<number | null>(null);

// 폼 상태
const fileInput = ref<HTMLInputElement | null>(null); // 파일 인풋 참조
const initialFormState: FormSchema = {
  title: "",
  category: "general",
  status: "active",
  content: "",
  isPinned: false,
  imageUrl: "",
};
const formState = reactive<FormSchema>({ ...initialFormState });

// ==========================================
// 4. 데이터 페칭 (Mock Data 생성)
// ==========================================
const { data, status: loadingStatus } = await useAsyncData<NoticeItem[]>(
  DATA_KEY,
  async () => {
    // 랜덤 데이터 생성 헬퍼 함수
    const getRandom = <T,>(arr: readonly T[]) =>
      arr.length > 0 ? arr[Math.floor(Math.random() * arr.length)] : arr[0];

    const categories = ["general", "urgent", "event"] as const;
    const statuses = ["active", "inactive", "scheduled"] as const;
    const authors = ["관리자", "운영팀", "김철수 매니저", "시스템"];
    const titles = [
      "개인정보 처리방침 변경 안내",
      "새벽 정기 서버 점검 안내",
      "신규 가입자 대상 웰컴 쿠폰 증정",
      "일부 결제 시스템 오류 수정 완료",
      "추석 연휴 고객센터 운영 일정 안내",
    ];

    // 50개의 더미 데이터 생성
    return Array.from({ length: 50 }).map((_, i) => {
      const category = getRandom(categories) ?? "general";
      const status = getRandom(statuses) ?? "active";

      return {
        id: 50 - i, // ID 역순 생성
        title: `[${category === "urgent" ? "긴급" : "안내"}] ${getRandom(titles)}`,
        content: "공지사항 상세 내용입니다.",
        status: status,
        category: category,
        createdAt: new Date(
          Date.now() - Math.floor(Math.random() * 365 * 24 * 60 * 60 * 1000),
        ).toISOString(),
        author: getRandom(authors) ?? "관리자",
        isPinned: category === "urgent" || Math.random() < 0.1,
        // [추가] 약 30% 확률로 이미지 포함
        imageUrl:
          Math.random() > 0.7
            ? `https://picsum.photos/seed/${i}/200/200`
            : undefined,
      };
    });
  },
);

// ==========================================
// 5. 비즈니스 로직 & 핸들러
// ==========================================

// 모달(Slideover) 열기 - 생성 모드
function openCreateModal() {
  isEditMode.value = false;
  selectedId.value = null;
  Object.assign(formState, initialFormState); // 폼 초기화
  isSlideoverOpen.value = true;
}

// 모달(Slideover) 열기 - 수정 모드
function openEditModal(row: NoticeItem) {
  isEditMode.value = true;
  selectedId.value = row.id;
  // 선택된 행의 데이터로 폼 채우기
  Object.assign(formState, {
    title: row.title,
    category: row.category,
    status: row.status,
    content: row.content,
    isPinned: row.isPinned,
    imageUrl: row.imageUrl,
  });
  isSlideoverOpen.value = true;
}

// [추가] 파일 선택 시 미리보기 URL 생성
function onFileSelect(event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    // 브라우저 메모리에 임시 URL 생성 (실무에서는 업로드 API 호출 필요)
    const previewUrl = URL.createObjectURL(file);
    formState.imageUrl = previewUrl;
  }
}

// [추가] 이미지 삭제 핸들러
function removeImage() {
  formState.imageUrl = "";
  if (fileInput.value) fileInput.value.value = ""; // 인풋 초기화
}

// 폼 제출 핸들러
async function onSubmit(event: FormSubmitEvent<FormSchema>) {
  const actionName = isEditMode.value ? "수정" : "등록";
  toast.add({
    title: `${actionName} 완료`,
    description: `성공적으로 ${actionName}되었습니다.`,
    color: "success",
  });
  isSlideoverOpen.value = false;
}

// 삭제 핸들러
function onDelete(ids: number[]) {
  toast.add({
    title: "삭제 완료",
    description: `${ids.length}개의 항목이 삭제되었습니다.`,
    color: "error",
  });
  rowSelection.value = {}; // 선택 초기화
}

// 행(Row) 액션 메뉴 아이템 생성
function getRowItems(row: NoticeItem) {
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
// 6. 테이블 컬럼 정의 (Custom Rendering)
// ==========================================
const columnLabels: Record<string, string> = {
  select: "선택",
  id: "No.",
  category: "분류",
  title: "제목",
  status: "상태",
  author: "작성자",
  createdAt: "등록일",
  actions: "관리",
};

const columns: TableColumn<NoticeItem>[] = [
  // 1. 체크박스 컬럼
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
    enableSorting: false, // 정렬 불필요
  },

  // 2. ID (No.) 컬럼 - [추가] 정렬 기능 적용
  {
    accessorKey: "id",
    // 헤더를 커스텀 렌더링하여 클릭 가능한 버튼으로 만듦
    header: ({ column }) => {
      const isSorted = column.getIsSorted(); // 'asc' | 'desc' | false
      return h(UButton, {
        color: "neutral",
        variant: "ghost",
        label: "No.",
        // 정렬 상태에 따라 아이콘 변경
        icon:
          isSorted === "asc"
            ? "i-lucide-arrow-up-narrow-wide"
            : isSorted === "desc"
              ? "i-lucide-arrow-down-wide-narrow"
              : "i-lucide-arrow-up-down", // 기본 상태
        class: "-ml-2.5 font-bold hover:bg-gray-100 dark:hover:bg-gray-800",
        // 클릭 시 정렬 토글 (오름차순 <-> 내림차순)
        onClick: () => column.toggleSorting(column.getIsSorted() === "asc"),
      });
    },
  },

  // 3. 분류 컬럼
  {
    accessorKey: "category",
    header: "분류",
    cell: ({ row }) => {
      const map = { general: "일반", urgent: "긴급", event: "이벤트" };
      const color = {
        general: "neutral",
        urgent: "error",
        event: "primary",
      } as const;
      return h(
        UBadge,
        { variant: "subtle", color: color[row.original.category] },
        () => map[row.original.category],
      );
    },
  },

  // 4. 제목 컬럼 - [추가] 이미지(Avatar) 표시
  {
    accessorKey: "title",
    header: "제목",
    cell: ({ row }) =>
      h("div", { class: "flex items-center gap-2" }, [
        // 고정핀 아이콘
        row.original.isPinned &&
          h(UIcon, {
            name: "i-lucide-pin",
            class: "text-primary w-4 h-4 shrink-0",
          }),
        // [추가] 이미지가 있으면 아바타 표시
        row.original.imageUrl &&
          h(UAvatar, { src: row.original.imageUrl, size: "2xs" }),
        // 제목 텍스트
        h(
          "span",
          {
            class:
              "font-medium truncate max-w-[300px] cursor-pointer hover:text-primary hover:underline",
            onClick: () => openEditModal(row.original),
          },
          row.original.title,
        ),
      ]),
  },

  // 5. 상태 컬럼 - [수정] 필터 로직 개선
  {
    accessorKey: "status",
    header: "상태",
    // [중요] 기본 'includes' 대신 'equals'를 사용하여 'active' 검색 시 'inactive'가 나오지 않게 함
    filterFn: "equals",
    cell: ({ row }) => {
      const map = { active: "게시중", inactive: "비공개", scheduled: "예약" };
      const color = {
        active: "success",
        inactive: "neutral",
        scheduled: "warning",
      } as const;
      return h(
        UBadge,
        { variant: "subtle", color: color[row.original.status] },
        () => map[row.original.status],
      );
    },
  },

  // 6. 작성자
  {
    accessorKey: "author",
    header: "작성자",
  },

  // 7. 등록일
  {
    accessorKey: "createdAt",
    header: "등록일",
    cell: ({ row }) => format(new Date(row.original.createdAt), "yyyy-MM-dd"),
  },

  // 8. 액션(더보기) 컬럼
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

// 상태 필터 드롭다운 변경 감지 -> 테이블 컬럼 필터 적용
watch(statusFilter, (val) => {
  if (!table.value?.tableApi) return;
  table.value.tableApi
    .getColumn("status")
    ?.setFilterValue(val === "all" ? undefined : val);
});

// 제목 검색바 양방향 바인딩
const titleSearch = computed({
  get: () =>
    (table.value?.tableApi?.getColumn("title")?.getFilterValue() as string) ||
    "",
  set: (val) =>
    table.value?.tableApi?.getColumn("title")?.setFilterValue(val || undefined),
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
      <UInput
        v-model="titleSearch"
        icon="i-lucide-search"
        placeholder="제목 검색..."
        class="max-w-sm"
      />

      <div class="flex items-center gap-2">
        <UButton
          label="공지 등록"
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
          <template #trailing>
            <UKbd>{{
              table?.tableApi?.getFilteredSelectedRowModel().rows.length
            }}</UKbd>
          </template>
        </UButton>

        <USelect
          v-model="statusFilter"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '게시중', value: 'active' },
            { label: '비공개', value: 'inactive' },
            { label: '예약됨', value: 'scheduled' },
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
    v-model:open="isSlideoverOpen"
    :title="isEditMode ? '공지사항 수정' : '신규 공지 등록'"
    :ui="{ wrapper: 'w-full sm:max-w-2xl' }"
  >
    <template #body>
      <UForm
        :schema="formSchema"
        :state="formState"
        class="space-y-4 p-4"
        @submit="onSubmit"
      >
        <UFormField label="제목" name="title" required class="w-full">
          <UInput
            v-model="formState.title"
            placeholder="제목을 입력하세요"
            class="w-full"
          />
        </UFormField>

        <UFormField name="isPinned">
          <UCheckbox v-model="formState.isPinned" label="상단 고정 (중요)" />
        </UFormField>

        <UFormField label="대표 이미지" name="imageUrl" class="w-full">
          <div class="flex flex-col gap-2">
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              class="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-primary-50 file:text-primary-700 hover:file:bg-primary-100"
              @change="onFileSelect"
            />
            <div
              v-if="formState.imageUrl"
              class="relative w-full mt-2 rounded-lg overflow-hidden border border-gray-200"
            >
              <img
                :src="formState.imageUrl"
                alt="Preview"
                class="w-full h-48 object-cover"
              />
              <UButton
                icon="i-lucide-x"
                color="gray"
                variant="solid"
                size="xs"
                class="absolute top-2 right-2"
                @click="removeImage"
              />
            </div>
          </div>
        </UFormField>

        <div class="grid grid-cols-2 gap-4">
          <UFormField label="분류" name="category" required class="w-full">
            <USelect
              v-model="formState.category"
              :items="['general', 'urgent', 'event']"
              class="w-full"
            />
          </UFormField>
          <UFormField label="게시 상태" name="status" required class="w-full">
            <USelect
              v-model="formState.status"
              :items="['active', 'inactive', 'scheduled']"
              class="w-full"
            />
          </UFormField>
        </div>

        <UFormField label="내용" name="content" required class="w-full">
          <UTextarea
            v-model="formState.content"
            :rows="10"
            autoresize
            class="w-full"
            placeholder="공지 내용을 입력하세요."
          />
        </UFormField>

        <div
          class="flex justify-end gap-2 pt-4 border-t border-default mt-auto"
        >
          <UButton
            label="취소"
            color="neutral"
            variant="ghost"
            @click="isSlideoverOpen = false"
          />
          <UButton
            type="submit"
            :label="isEditMode ? '수정 완료' : '등록하기'"
            color="primary"
          />
        </div>
      </UForm>
    </template>
  </UModal>
</template>
