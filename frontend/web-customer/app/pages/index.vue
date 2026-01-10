<script setup lang="ts">
// --- Data ---
const categories = [
  { id: 1, name: '전체', icon: '🍽️' },
  { id: 2, name: '한식', icon: '🍚' },
  { id: 3, name: '치킨', icon: '🍗' },
  { id: 4, name: '피자', icon: '🍕' },
  { id: 5, name: '중식', icon: '🥟' },
  { id: 6, name: '카페', icon: '☕' },
]

const restaurants = [
  {
    id: 1,
    name: '맛있는 한식당',
    category: '한식',
    rating: 4.8,
    reviews: 1234,
    deliveryTime: '25-35분',
    deliveryFee: '2,000원',
    image: '🍚',
    minOrder: '12,000원'
  },
  {
    id: 2,
    name: '치킨마루',
    category: '치킨',
    rating: 4.9,
    reviews: 2341,
    deliveryTime: '30-40분',
    deliveryFee: '무료',
    image: '🍗',
    minOrder: '15,000원'
  },
  {
    id: 3,
    name: '피자헤븐',
    category: '피자',
    rating: 4.7,
    reviews: 876,
    deliveryTime: '35-45분',
    deliveryFee: '3,000원',
    image: '🍕',
    minOrder: '18,000원'
  },
  {
    id: 4,
    name: '짜장명가',
    category: '중식',
    rating: 4.6,
    reviews: 543,
    deliveryTime: '20-30분',
    deliveryFee: '1,500원',
    image: '🥟',
    minOrder: '10,000원'
  }
]

// --- State ---
const favorites = ref<number[]>([])

// --- Actions ---
const toggleFavorite = (id: number) => {
  if (favorites.value.includes(id)) {
    favorites.value = favorites.value.filter(fId => fId !== id)
  } else {
    favorites.value.push(id)
  }
}

// 숫자 포맷팅 (리뷰 수 등)
const formatNumber = (num: number) => num.toLocaleString()
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
          <UIcon name="i-heroicons-shopping-bag" class="w-6 h-6 text-gray-700" />
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
          >
            <div class="w-14 h-14 bg-gray-100 rounded-full flex items-center justify-center text-2xl mb-1 group-hover:bg-gray-200 transition-colors">
              {{ cat.icon }}
            </div>
            <span class="text-xs text-gray-700">{{ cat.name }}</span>
          </button>
        </div>
      </div>

      <!-- Banner -->
      <div class="px-4 py-4">
        <div class="bg-gradient-to-r from-green-500 to-emerald-600 rounded-xl p-6 text-white shadow-lg">
          <div class="text-sm font-medium mb-1">🎉 첫 주문 특별 할인</div>
          <div class="text-xl font-bold mb-2">최대 10,000원 할인</div>
          <div class="text-sm opacity-90">지금 바로 주문하고 혜택 받으세요</div>
        </div>
      </div>

      <!-- Restaurant List -->
      <div class="px-4">
        <h2 class="text-lg font-bold text-gray-900 mb-4">인기 음식점</h2>
        <div class="space-y-4">
          <div 
            v-for="restaurant in restaurants" 
            :key="restaurant.id" 
            class="bg-white rounded-xl shadow-sm overflow-hidden hover:shadow-md transition-shadow border border-gray-100"
          >
            <div class="flex p-4">
              <!-- Image Area -->
              <div class="w-24 h-24 bg-gray-100 rounded-lg flex items-center justify-center text-5xl mr-4 flex-shrink-0">
                {{ restaurant.image }}
              </div>
              
              <!-- Content Area -->
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between mb-1">
                  <h3 class="font-semibold text-gray-900 truncate">{{ restaurant.name }}</h3>
                  <button 
                    @click="toggleFavorite(restaurant.id)"
                    class="ml-2 flex-shrink-0 focus:outline-none"
                  >
                    <!-- 하트 아이콘 -->
                    <UIcon 
                      :name="favorites.includes(restaurant.id) ? 'i-heroicons-heart-solid' : 'i-heroicons-heart'"
                      class="w-5 h-5 transition-colors"
                      :class="favorites.includes(restaurant.id) ? 'text-red-500' : 'text-gray-300'"
                    />
                  </button>
                </div>
                
                <div class="flex items-center text-sm text-gray-600 mb-2">
                  <UIcon name="i-heroicons-star-solid" class="w-4 h-4 text-yellow-400 mr-1" />
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