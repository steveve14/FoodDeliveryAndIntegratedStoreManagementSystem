<script setup lang="ts">
interface FavoriteStore {
  id: string;
  storeId: string;
  name: string;
  category: string;
  rating: number;
  deliveryTime: string;
  minOrder: string;
  imageIcon: string;
}

interface FavoriteStoreApiItem {
  id: string;
  userId: string;
  storeId: string;
  name: string;
  category: string;
  rating: number;
  deliveryTime: string;
  minOrder: string;
  imageIcon: string;
}

const { $api } = useApi();
const favorites = ref<FavoriteStore[]>([]);

const loadFavorites = async () => {
  const res = await $api<FavoriteStoreApiItem[]>('/api/v1/users/me/favorites');
  if (!res.success || !res.data) {
    favorites.value = [];
    return;
  }

  favorites.value = res.data.map(store => ({
    id: store.id,
    storeId: store.storeId,
    name: store.name,
    category: store.category,
    rating: store.rating,
    deliveryTime: store.deliveryTime,
    minOrder: store.minOrder,
    imageIcon: store.imageIcon,
  }));
};

await loadFavorites();

const removeFavorite = async (id: string) => {
  await $api(`/api/v1/users/me/favorites/${id}`, {
    method: 'DELETE',
  });
  favorites.value = favorites.value.filter(
    (store: FavoriteStore) => store.id !== id,
  );
};
</script>

<template>
  <UDashboardPanel grow>
    <template #header>
      <UDashboardNavbar
        title="찜한 가게"
        :badge="favorites.length"
      >
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <div class="flex flex-col gap-4 p-4">
        <div
          v-for="store in favorites"
          :key="store.id"
          class="flex items-center gap-4 rounded-lg border border-gray-200 dark:border-gray-700 p-4 shadow-sm"
        >
          <div
            class="w-12 h-12 rounded-lg bg-primary-50 dark:bg-primary-900/30 flex items-center justify-center"
          >
            <UIcon
              :name="store.imageIcon"
              class="w-6 h-6 text-primary-500"
            />
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <NuxtLink
                :to="`/stores/${store.storeId}`"
                class="font-semibold text-sm truncate hover:text-primary-600"
              >
                {{ store.name }}
              </NuxtLink>
              <UBadge
                color="neutral"
                variant="subtle"
                size="xs"
              >
                {{
                  store.category
                }}
              </UBadge>
            </div>
            <div class="flex items-center gap-3 mt-1 text-xs text-gray-500">
              <span class="flex items-center gap-1">
                <UIcon
                  name="i-lucide-star"
                  class="w-3 h-3 text-yellow-400"
                />
                {{ store.rating }}
              </span>
              <span>{{ store.deliveryTime }}</span>
              <span>최소 {{ store.minOrder }}</span>
            </div>
          </div>
          <UButton
            color="neutral"
            variant="ghost"
            icon="i-lucide-heart-off"
            size="sm"
            @click="removeFavorite(store.id)"
          />
        </div>

        <div
          v-if="favorites.length === 0"
          class="text-center py-16 text-gray-500"
        >
          <UIcon
            name="i-lucide-heart"
            class="w-10 h-10 mx-auto mb-2 text-gray-300"
          />
          <p class="text-sm">
            찜한 가게가 없습니다.
          </p>
        </div>
      </div>
    </template>
  </UDashboardPanel>
</template>
