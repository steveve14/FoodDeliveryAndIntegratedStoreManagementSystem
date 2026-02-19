<script setup lang="ts">
import type { TableColumn } from "@nuxt/ui";
import { format } from "date-fns";

// 1. 데이터 타입 정의
type StockStatus = "safe" | "warning" | "danger"; // 여유 | 주의 | 부족

interface InventoryItem {
  id: number;
  name: string; // 품목명 (예: 11호 닭)
  category: string; // 분류 (주재료, 부재료, 포장재 등)
  currentStock: number; // 현재 재고
  minThreshold: number; // 최소 유지 재고 (이 이하로 떨어지면 경고)
  unit: string; // 단위 (kg, box, 개, ea)
  lastUpdated: string; // 마지막 입출고 일시
}

// 2. 더미 데이터 (치킨집 예시)
const inventory = ref<InventoryItem[]>([
  {
    id: 1,
    name: "신선육 11호 (염지)",
    category: "주재료",
    currentStock: 45,
    minThreshold: 20,
    unit: "마리",
    lastUpdated: new Date().toISOString(),
  },
  {
    id: 2,
    name: "전용 튀김유 (18L)",
    category: "주재료",
    currentStock: 3,
    minThreshold: 5,
    unit: "캔",
    lastUpdated: new Date(Date.now() - 86400000 * 2).toISOString(),
  },
  {
    id: 3,
    name: "치킨무 (박스)",
    category: "부재료",
    currentStock: 8,
    minThreshold: 10,
    unit: "박스",
    lastUpdated: new Date().toISOString(),
  },
  {
    id: 4,
    name: "양념 소스 (10kg)",
    category: "소스류",
    currentStock: 12,
    minThreshold: 3,
    unit: "통",
    lastUpdated: new Date(Date.now() - 86400000 * 5).toISOString(),
  },
  {
    id: 5,
    name: "포장 박스 (본품)",
    category: "포장재",
    currentStock: 150,
    minThreshold: 50,
    unit: "개",
    lastUpdated: new Date().toISOString(),
  },
  {
    id: 6,
    name: "콜라 1.25L",
    category: "음료",
    currentStock: 2,
    minThreshold: 12,
    unit: "ea",
    lastUpdated: new Date(Date.now() - 86400000).toISOString(),
  },
]);

// 3. 상태 계산 로직
const getStatus = (current: number, min: number): StockStatus => {
  if (current <= 0) return "danger"; // 품절
  if (current <= min) return "warning"; // 부족 (발주 필요)
  return "safe"; // 여유
};

const getStatusLabel = (status: StockStatus) => {
  switch (status) {
    case "safe":
      return "양호";
    case "warning":
      return "부족(발주필요)";
    case "danger":
      return "품절";
  }
};

const getStatusColor = (status: StockStatus) => {
  switch (status) {
    case "safe":
      return "success";
    case "warning":
      return "warning";
    case "danger":
      return "error";
  }
};

// 4. 필터 및 검색
const q = ref("");
const selectedCategory = ref("all");
const hideSafeItems = ref(false); // '부족한 재료만 보기' 필터

const categories = ["주재료", "부재료", "소스류", "포장재", "음료"];

const filteredInventory = computed(() => {
  return inventory.value.filter((item) => {
    const matchesSearch = item.name
      .toLowerCase()
      .includes(q.value.toLowerCase());
    const matchesCategory =
      selectedCategory.value === "all" ||
      item.category === selectedCategory.value;

    // 부족한 재료만 보기 필터
    const status = getStatus(item.currentStock, item.minThreshold);
    const matchesSafeFilter = hideSafeItems.value ? status !== "safe" : true;

    return matchesSearch && matchesCategory && matchesSafeFilter;
  });
});

// 5. 입출고 모달 관리
const isStockModalOpen = ref(false);
const stockForm = reactive({
  id: 0,
  name: "",
  type: "in" as "in" | "out", // 입고 | 출고
  amount: 0,
  current: 0,
});

const openStockModal = (item: InventoryItem) => {
  stockForm.id = item.id;
  stockForm.name = item.name;
  stockForm.current = item.currentStock;
  stockForm.type = "in";
  stockForm.amount = 0;
  isStockModalOpen.value = true;
};

const updateStock = () => {
  const index = inventory.value.findIndex((i) => i.id === stockForm.id);
  if (index !== -1 && inventory.value[index]) {
    const change =
      stockForm.type === "in" ? stockForm.amount : -stockForm.amount;
    inventory.value[index].currentStock += change;
    inventory.value[index].lastUpdated = new Date().toISOString();
  }
  isStockModalOpen.value = false;
};

// 6. 테이블 컬럼
const columns: TableColumn<InventoryItem>[] = [
  { accessorKey: "name", header: "품목명" },
  { accessorKey: "category", header: "분류" },
  { accessorKey: "status", header: "상태" }, // 계산된 상태
  { accessorKey: "stock", header: "재고 현황" }, // 프로그레스 바 포함
  { accessorKey: "lastUpdated", header: "최근 입출고" },
  { id: "actions", header: "" },
];

const items = (row: InventoryItem) => [
  [
    {
      label: "입/출고 등록",
      icon: "i-lucide-arrow-left-right",
      click: () => openStockModal(row),
    },
  ],
  [
    {
      label: "정보 수정",
      icon: "i-lucide-settings-2",
    },
  ],
];
</script>

<template>
  <UDashboardPanel grow>
    <UDashboardNavbar title="재고 관리" :badge="inventory.length">
      <template #right>
        <UButton
          label="신규 품목 등록"
          trailing-icon="i-lucide-plus"
          color="primary"
        />
      </template>
    </UDashboardNavbar>

    <UDashboardToolbar>
      <template #left>
        <UInput
          v-model="q"
          icon="i-lucide-search"
          placeholder="품목명 검색..."
          class="w-full sm:w-64"
        />
        <USelect
          v-model="selectedCategory"
          :items="[
            { label: '전체 분류', value: 'all' },
            ...categories.map((c) => ({ label: c, value: c })),
          ]"
          option-attribute="label"
          value-attribute="value"
          class="w-32"
        />
        <div class="flex items-center gap-2 ml-2">
          <UToggle v-model="hideSafeItems" />
          <span class="text-sm text-gray-600 dark:text-gray-400"
            >발주 필요 항목만 보기</span
          >
        </div>
      </template>
    </UDashboardToolbar>

    <UTable
      :data="filteredInventory"
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
        <div>
          <span class="font-medium text-gray-900 dark:text-white">{{
            row.original.name
          }}</span>
        </div>
      </template>

      <template #category-cell="{ row }">
        <UBadge color="neutral" variant="subtle">{{
          row.original.category
        }}</UBadge>
      </template>

      <template #status-cell="{ row }">
        <UBadge
          :color="
            getStatusColor(
              getStatus(row.original.currentStock, row.original.minThreshold),
            )
          "
          variant="subtle"
          class="font-bold"
        >
          {{
            getStatusLabel(
              getStatus(row.original.currentStock, row.original.minThreshold),
            )
          }}
        </UBadge>
      </template>

      <template #stock-cell="{ row }">
        <div class="w-full max-w-35">
          <div class="flex justify-between text-xs mb-1">
            <span class="font-semibold text-gray-900 dark:text-white">
              {{ row.original.currentStock }}{{ row.original.unit }}
            </span>
            <span class="text-gray-400"
              >최소 {{ row.original.minThreshold }}</span
            >
          </div>
          <UProgress
            :value="
              (row.original.currentStock / (row.original.minThreshold * 2)) *
              100
            "
            :color="
              getStatusColor(
                getStatus(row.original.currentStock, row.original.minThreshold),
              )
            "
            size="xs"
          />
        </div>
      </template>

      <template #lastUpdated-cell="{ row }">
        <span class="text-sm text-gray-500">
          {{ format(new Date(row.original.lastUpdated), "MM/dd HH:mm") }}
        </span>
      </template>

      <template #actions-cell="{ row }">
        <UDropdown :items="items(row.original)">
          <UButton icon="i-lucide-ellipsis" color="neutral" variant="ghost" />
        </UDropdown>
      </template>
    </UTable>

    <UModal v-model:open="isStockModalOpen" title="재고 수량 변경">
      <template #content>
        <UCard class="ring-0 divide-y divide-gray-100 dark:divide-gray-800">
          <template #header>
            <div class="flex items-center justify-between">
              <h3
                class="text-base font-semibold leading-6 text-gray-900 dark:text-white"
              >
                재고 입/출고 처리
              </h3>
              <UButton
                color="neutral"
                variant="ghost"
                icon="i-heroicons-x-mark-20-solid"
                class="-my-1"
                @click="isStockModalOpen = false"
              />
            </div>
          </template>

          <div class="p-4 space-y-4">
            <div class="text-sm text-gray-500 mb-2">
              품목:
              <strong class="text-gray-900 dark:text-white">{{
                stockForm.name
              }}</strong>
              (현재: {{ stockForm.current }})
            </div>

            <div
              class="grid grid-cols-2 gap-2 p-1 bg-gray-100 dark:bg-gray-800 rounded-lg"
            >
              <button
                type="button"
                class="py-1.5 text-sm font-medium rounded-md transition-colors"
                :class="
                  stockForm.type === 'in'
                    ? 'bg-white dark:bg-gray-700 shadow text-primary-600'
                    : 'text-gray-500 hover:text-gray-700'
                "
                @click="stockForm.type = 'in'"
              >
                입고 (+)
              </button>
              <button
                type="button"
                class="py-1.5 text-sm font-medium rounded-md transition-colors"
                :class="
                  stockForm.type === 'out'
                    ? 'bg-white dark:bg-gray-700 shadow text-red-600'
                    : 'text-gray-500 hover:text-gray-700'
                "
                @click="stockForm.type = 'out'"
              >
                출고/사용 (-)
              </button>
            </div>

            <UFormGroup
              :label="stockForm.type === 'in' ? '입고 수량' : '출고 수량'"
            >
              <UInput
                v-model="stockForm.amount"
                type="number"
                placeholder="0"
                autofocus
                :color="stockForm.type === 'in' ? 'primary' : 'warning'"
              />
            </UFormGroup>
          </div>

          <template #footer>
            <div class="flex justify-end gap-2">
              <UButton
                color="neutral"
                variant="ghost"
                label="취소"
                @click="isStockModalOpen = false"
              />
              <UButton
                :color="stockForm.type === 'in' ? 'primary' : 'error'"
                :label="stockForm.type === 'in' ? '입고 완료' : '출고 완료'"
                @click="updateStock"
              />
            </div>
          </template>
        </UCard>
      </template>
    </UModal>
  </UDashboardPanel>
</template>
