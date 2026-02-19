<script setup lang="ts">
import type { TableColumn } from "@nuxt/ui";
import { z } from "zod";

// 1. 데이터 타입 정의
type ProductStatus = "sale" | "soldout" | "hidden";

interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  status: ProductStatus;
  image?: string;
}

// 2. 더미 데이터 (초기 메뉴 목록)
const products = ref<Product[]>([
  {
    id: 1,
    name: "황금 올리브 치킨",
    category: "치킨",
    price: 24000,
    status: "sale",
    image: "https://placehold.co/100x100/orange/white?text=Chicken",
  },
  {
    id: 2,
    name: "양념 반 후라이드 반",
    category: "치킨",
    price: 25000,
    status: "sale",
    image: "https://placehold.co/100x100/red/white?text=BanBan",
  },
  {
    id: 3,
    name: "치즈볼 (5개)",
    category: "사이드",
    price: 5000,
    status: "sale",
  },
  {
    id: 4,
    name: "콜라 1.25L",
    category: "음료",
    price: 3000,
    status: "soldout", // 품절 상태 예시
  },
]);

// 3. 검색 및 필터 상태
const q = ref("");
const selectedStatus = ref<ProductStatus | "all">("all");

// 필터링된 데이터 계산
const filteredProducts = computed(() => {
  return products.value.filter((product) => {
    const matchesSearch = product.name
      .toLowerCase()
      .includes(q.value.toLowerCase());
    const matchesStatus =
      selectedStatus.value === "all" || product.status === selectedStatus.value;
    return matchesSearch && matchesStatus;
  });
});

// 4. 모달 및 폼 관련 상태
const isModalOpen = ref(false);
const isEditMode = ref(false);
const state = reactive({
  id: 0,
  name: "",
  category: "치킨",
  price: 0,
  status: "sale" as ProductStatus,
});

// 카테고리 옵션
const categories = ["치킨", "사이드", "음료", "소스/기타"];
const statuses = [
  { label: "판매중", value: "sale" },
  { label: "품절", value: "soldout" },
  { label: "숨김", value: "hidden" },
];

// 5. 액션 핸들러

// 모달 열기 (등록)
const openCreateModal = () => {
  isEditMode.value = false;
  state.id = 0;
  state.name = "";
  state.category = "치킨";
  state.price = 0;
  state.status = "sale";
  isModalOpen.value = true;
};

// 모달 열기 (수정)
const openEditModal = (product: Product) => {
  isEditMode.value = true;
  state.id = product.id;
  state.name = product.name;
  state.category = product.category;
  state.price = product.price;
  state.status = product.status;
  isModalOpen.value = true;
};

// 저장 (등록/수정 처리)
const saveProduct = () => {
  if (!state.name || state.price < 0) return; // 간단 유효성 검사

  if (isEditMode.value) {
    // 수정 로직
    const index = products.value.findIndex((p) => p.id === state.id);
    if (index !== -1) {
      products.value[index] = { ...products.value[index], ...state };
    }
  } else {
    // 등록 로직
    const newId = Math.max(...products.value.map((p) => p.id), 0) + 1;
    products.value.push({
      ...state,
      id: newId,
      image: "https://placehold.co/100x100/gray/white?text=New",
    });
  }
  isModalOpen.value = false;
};

// 삭제 로직
const deleteProduct = (id: number) => {
  if (confirm("정말 이 메뉴를 삭제하시겠습니까?")) {
    products.value = products.value.filter((p) => p.id !== id);
  }
};

// 가격 포맷터
const formatPrice = (price: number) =>
  new Intl.NumberFormat("ko-KR", {
    style: "currency",
    currency: "KRW",
  }).format(price);

// 6. 테이블 컬럼 정의
const columns: TableColumn<Product>[] = [
  {
    accessorKey: "name",
    header: "메뉴명",
  },
  {
    accessorKey: "category",
    header: "카테고리",
  },
  {
    accessorKey: "status",
    header: "상태",
  },
  {
    accessorKey: "price",
    header: "가격",
  },
  {
    id: "actions",
    header: "",
  },
];

// 드롭다운 메뉴 아이템 생성 함수
const getRowItems = (row: Product) => [
  [
    {
      label: "수정하기",
      icon: "i-lucide-edit",
      click: () => openEditModal(row),
    },
    {
      label: "복제하기",
      icon: "i-lucide-copy",
      click: () => console.log("복제", row.id),
    },
  ],
  [
    {
      label: "삭제하기",
      icon: "i-lucide-trash-2",
      color: "error" as const,
      click: () => deleteProduct(row.id),
    },
  ],
];
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="메뉴/상품 관리" :badge="products.length">
      <template #right>
        <UButton
          label="메뉴 등록"
          trailing-icon="i-lucide-plus"
          color="primary"
          @click="openCreateModal"
        />
      </template>
    </UDashboardNavbar>

    <UDashboardToolbar>
      <template #left>
        <UInput
          v-model="q"
          icon="i-lucide-search"
          placeholder="메뉴명 검색..."
          class="w-full sm:w-64"
        />
        <USelect
          v-model="selectedStatus"
          :items="[
            { label: '전체 상태', value: 'all' },
            { label: '판매중', value: 'sale' },
            { label: '품절', value: 'soldout' },
            { label: '숨김', value: 'hidden' },
          ]"
          option-attribute="label"
          value-attribute="value"
          class="w-32"
        />
      </template>
    </UDashboardToolbar>

    <UTable
      :data="filteredProducts"
      :columns="columns"
      class="flex-1"
      :ui="{
        base: 'table-fixed border-separate border-spacing-0',
        thead: '[&>tr]:bg-elevated/50 [&>tr]:after:content-none',
        tbody: '[&>tr]:last:[&>td]:border-b-0',
        th: 'py-2 first:rounded-l-lg last:rounded-r-lg border-y border-default first:border-l last:border-r',
        td: 'border-b border-default',
        separator: 'h-0',
      }"
    >
      <template #name-cell="{ row }">
        <div class="flex items-center gap-3">
          <UAvatar
            :src="row.original.image"
            :alt="row.original.name"
            size="md"
            icon="i-lucide-image"
            class="bg-gray-100 dark:bg-gray-800"
          />
          <div class="flex flex-col">
            <span class="font-medium text-gray-900 dark:text-white">
              {{ row.original.name }}
            </span>
            <span class="text-xs text-gray-500 hidden sm:inline-block">
              ID: {{ row.original.id }}
            </span>
          </div>
        </div>
      </template>

      <template #category-cell="{ row }">
        <UBadge color="neutral" variant="subtle">
          {{ row.original.category }}
        </UBadge>
      </template>

      <template #status-cell="{ row }">
        <UBadge
          :color="
            row.original.status === 'sale'
              ? 'success'
              : row.original.status === 'soldout'
                ? 'error'
                : 'neutral'
          "
          variant="subtle"
          class="capitalize"
        >
          {{
            row.original.status === "sale"
              ? "판매중"
              : row.original.status === "soldout"
                ? "품절"
                : "숨김"
          }}
        </UBadge>
      </template>

      <template #price-cell="{ row }">
        <span class="font-medium">
          {{ formatPrice(row.original.price) }}
        </span>
      </template>

      <template #actions-cell="{ row }">
        <UDropdown :items="getRowItems(row.original)">
          <UButton color="neutral" variant="ghost" icon="i-lucide-ellipsis" />
        </UDropdown>
      </template>
    </UTable>

    <UModal
      v-model:open="isModalOpen"
      :title="isEditMode ? '메뉴 수정' : '새 메뉴 등록'"
    >
      <template #content>
        <UCard class="ring-0 divide-y divide-gray-100 dark:divide-gray-800">
          <template #header>
            <div class="flex items-center justify-between">
              <h3
                class="text-base font-semibold leading-6 text-gray-900 dark:text-white"
              >
                {{ isEditMode ? "메뉴 정보 수정" : "새 메뉴 등록" }}
              </h3>
              <UButton
                color="neutral"
                variant="ghost"
                icon="i-heroicons-x-mark-20-solid"
                class="-my-1"
                @click="isModalOpen = false"
              />
            </div>
          </template>

          <form @submit.prevent="saveProduct" class="p-4 space-y-4">
            <UFormGroup label="메뉴명" required>
              <UInput
                v-model="state.name"
                placeholder="예: 황금 올리브 치킨"
                autofocus
              />
            </UFormGroup>

            <div class="grid grid-cols-2 gap-4">
              <UFormGroup label="카테고리" required>
                <USelect v-model="state.category" :items="categories" />
              </UFormGroup>

              <UFormGroup label="가격" required>
                <UInput v-model="state.price" type="number" placeholder="0">
                  <template #trailing>
                    <span class="text-gray-500 text-xs">원</span>
                  </template>
                </UInput>
              </UFormGroup>
            </div>

            <UFormGroup label="판매 상태">
              <div class="flex gap-4 mt-2">
                <URadio
                  v-for="status in statuses"
                  :key="status.value"
                  v-model="state.status"
                  :value="status.value"
                  :label="status.label"
                />
              </div>
            </UFormGroup>
          </form>

          <template #footer>
            <div class="flex justify-end gap-2">
              <UButton
                color="neutral"
                variant="ghost"
                @click="isModalOpen = false"
              >
                취소
              </UButton>
              <UButton color="primary" @click="saveProduct">
                {{ isEditMode ? "수정 완료" : "메뉴 등록" }}
              </UButton>
            </div>
          </template>
        </UCard>
      </template>
    </UModal>
  </UDashboardPanel>
</template>
