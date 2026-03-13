interface HomeStoreSummary {
  id: string;
}

interface HomeOrderItem {
  productId: string;
  quantity: number;
  price: number;
}

export interface HomeOrderApiItem {
  id: string;
  userId: string;
  storeId: string;
  totalAmount: number;
  status: string;
  createdAt: string;
  items: HomeOrderItem[];
}

interface HomeMenuApiItem {
  id: string;
  name: string;
}

export interface HomeStoreSource {
  orders: HomeOrderApiItem[];
  menuNameById: Record<string, string>;
}

export const useHomeStoreSource = () => {
  const { $api } = useApi();

  return useAsyncData<HomeStoreSource>('home-store-source', async () => {
    const myStore = await $api<HomeStoreSummary>('/api/v1/stores/me');
    if (!myStore.success || !myStore.data) {
      return {
        orders: [],
        menuNameById: {},
      };
    }

    const storeId = myStore.data.id;

    const [ordersRes, menusRes] = await Promise.all([
      $api<HomeOrderApiItem[]>(`/api/v1/orders/store/${storeId}`),
      $api<HomeMenuApiItem[]>(`/api/v1/stores/${storeId}/menus`),
    ]);

    const menuNameById = Object.fromEntries(
      (menusRes.data ?? []).map(menu => [menu.id, menu.name]),
    );

    return {
      orders: ordersRes.success && ordersRes.data ? ordersRes.data : [],
      menuNameById,
    };
  });
};
