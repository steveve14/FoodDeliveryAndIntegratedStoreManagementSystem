<script setup lang="ts">
// --- Types ---
interface StoreDto {
  id: string;
  name: string;
  address: string;
  phone: string;
  category: string;
  status: string;
  minOrderAmount: number;
  ratingAvg: number;
  description: string;
}

interface Restaurant {
  id: string;
  name: string;
  category: string;
  rating: number;
  reviews: number;
  deliveryTime: string;
  deliveryFee: string;
  image: string;
  minOrder: string;
}

// --- Data ---
const categories = [
  { id: 1, name: '전체', icon: '🍽️' },
  { id: 2, name: '한식', icon: '🍚' },
  { id: 3, name: '치킨', icon: '🍗' },
  { id: 4, name: '피자', icon: '🍕' },
  { id: 5, name: '중식', icon: '🥟' },
  { id: 6, name: '카페', icon: '☕' },
];

const categoryIcons: Record<string, string> = {
  한식: '🍚',
  치킨: '🍗',
  피자: '🍕',
  중식: '🥟',
  카페: '☕',
  일식: '🍣',
  양식: '🍝',
  패스트푸드: '🍔',
};

// --- Backend API 조회 ---
const { $api } = useApi();
const restaurants = ref<Restaurant[]>([]);
const selectedCategory = ref('전체');
const loading = ref(false);

/** 가게 목록 조회 */
async function fetchStores(category?: string) {
  loading.value = true;
  try {
    const params: Record<string, string> = {};
    if (category && category !== '전체') params.category = category;

    const res = await $api<StoreDto[]>('/api/v1/stores', { params });
    if (res.success && res.data) {
      restaurants.value = res.data.map((store) => ({
        id: store.id,
        name: store.name,
        category: store.category || '-',
        rating: store.ratingAvg || 0,
        reviews: 0,
        deliveryTime: '25-40분',
        deliveryFee: store.minOrderAmount >= 20000 ? '무료' : '2,000원',
        image: categoryIcons[store.category] || '🍽️',
        minOrder: store.minOrderAmount
          ? new Intl.NumberFormat('ko-KR').format(store.minOrderAmount) + '원'
          : '-',
      }));
    }
  } catch {
    restaurants.value = [];
  } finally {
    loading.value = false;
  }
}

// 카테고리 선택 시 필터링
function selectCategory(name: string) {
  selectedCategory.value = name;
  fetchStores(name);
}

await fetchStores();

// --- State ---
const favorites = ref<string[]>([]);

// --- Actions ---
const toggleFavorite = (id: string) => {
  if (favorites.value.includes(id)) {
    favorites.value = favorites.value.filter((fId) => fId !== id);
  } else {
    favorites.value.push(id);
  }
};

// 숫자 포맷팅 (리뷰 수 등)
const formatNumber = (num: number) => num.toLocaleString();
</script>

<template>
  <div class="max-w-md mx-auto">
    <!-- Header -->
    <div class="bg-white shadow-sm sticky top-0 z-40">
      <div class="px-4 py-4">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center space-x-2">
            <UIcon name="i-heroicons-map-pin" class="w-5 h-5 text-green-600" />
            <div>
              <div class="font-semibold text-gray-900">서울시 강남구</div>
              <div class="text-xs text-gray-500">역삼동 123-45</div>
            </div>
          </div>
          <UIcon
            name="i-heroicons-shopping-bag"
            class="w-6 h-6 text-gray-700"
          />
        </div>

        <!-- Search Bar (Nuxt UI Input 사용) -->
        <UInput
          icon="i-heroicons-magnifying-glass"
          placeholder="음식점 또는 메뉴 검색"
          size="lg"
          :ui="{ icon: { trailing: { pointer: '' } } }"
          class="w-full"
        />
      </div>
    </div>

    <!-- Main Content -->
    <div class="pb-4">
      <!-- Categories -->
      <div class="bg-white px-4 py-4 mb-2">
        <div class="flex space-x-4 overflow-x-auto scrollbar-hide pb-2">
          <button
            v-for="cat in categories"
            :key="cat.id"
            class="flex flex-col items-center min-w-[56px] group"
            @click="selectCategory(cat.name)"
          >
            <div
              class="w-14 h-14 rounded-full flex items-center justify-center text-2xl mb-1 transition-colors"
              :class="
                selectedCategory === cat.name
                  ? 'bg-green-100 ring-2 ring-green-500'
                  : 'bg-gray-100 group-hover:bg-gray-200'
              "
            >
              {{ cat.icon }}
            </div>
            <span
              class="text-xs"
              :class="
                selectedCategory === cat.name
                  ? 'text-green-600 font-semibold'
                  : 'text-gray-700'
              "
              >{{ cat.name }}</span
            >
          </button>
        </div>
      </div>

      <!-- Banner -->
      <div class="px-4 py-4">
        <div
          class="bg-gradient-to-r from-green-500 to-emerald-600 rounded-xl p-6 text-white shadow-lg"
        >
          <div class="text-sm font-medium mb-1">🎉 첫 주문 특별 할인</div>
          <div class="text-xl font-bold mb-2">최대 10,000원 할인</div>
          <div class="text-sm opacity-90">지금 바로 주문하고 혜택 받으세요</div>
        </div>
      </div>

      <!-- Restaurant List -->
      <div class="px-4">
        <h2 class="text-lg font-bold text-gray-900 mb-4">인기 음식점</h2>

        <!-- 로딩 상태 -->
        <div v-if="loading" class="flex justify-center py-8">
          <div
            class="animate-spin rounded-full h-8 w-8 border-b-2 border-green-500"
          ></div>
        </div>

        <!-- 빈 상태 -->
        <div
          v-else-if="restaurants.length === 0"
          class="text-center py-8 text-gray-400"
        >
          등록된 음식점이 없습니다.
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="restaurant in restaurants"
            :key="restaurant.id"
            class="bg-white rounded-xl shadow-sm overflow-hidden hover:shadow-md transition-shadow border border-gray-100"
          >
            <div class="flex p-4">
              <!-- Image Area -->
              <div
                class="w-24 h-24 bg-gray-100 rounded-lg flex items-center justify-center text-5xl mr-4 flex-shrink-0"
              >
                {{ restaurant.image }}
              </div>

              <!-- Content Area -->
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between mb-1">
                  <h3 class="font-semibold text-gray-900 truncate">
                    {{ restaurant.name }}
                  </h3>
                  <button
                    @click="toggleFavorite(restaurant.id)"
                    class="ml-2 flex-shrink-0 focus:outline-none"
                  >
                    <!-- 하트 아이콘 -->
                    <UIcon
                      :name="
                        favorites.includes(restaurant.id)
                          ? 'i-heroicons-heart-solid'
                          : 'i-heroicons-heart'
                      "
                      class="w-5 h-5 transition-colors"
                      :class="
                        favorites.includes(restaurant.id)
                          ? 'text-red-500'
                          : 'text-gray-300'
                      "
                    />
                  </button>
                </div>

                <div class="flex items-center text-sm text-gray-600 mb-2">
                  <UIcon
                    name="i-heroicons-star-solid"
                    class="w-4 h-4 text-yellow-400 mr-1"
                  />
                  <span class="font-medium">{{ restaurant.rating }}</span>
                  <span class="mx-1">•</span>
                  <span>리뷰 {{ formatNumber(restaurant.reviews) }}</span>
                </div>

                <div class="flex items-center text-xs text-gray-500 space-x-2">
                  <div class="flex items-center">
                    <UIcon name="i-heroicons-clock" class="w-3 h-3 mr-1" />
                    {{ restaurant.deliveryTime }}
                  </div>
                  <span>•</span>
                  <span>배달비 {{ restaurant.deliveryFee }}</span>
                </div>

                <div class="mt-2 text-xs text-gray-400">
                  최소주문 {{ restaurant.minOrder }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
