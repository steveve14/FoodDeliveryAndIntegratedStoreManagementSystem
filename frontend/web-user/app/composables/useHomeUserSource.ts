interface HomeStoreSummary {
  id: string;
  name: string;
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

export interface HomeUserSource {
  orders: HomeOrderApiItem[];
  menuNameById: Record<string, string>;
  storeNameById: Record<string, string>;
}

export const useHomeUserSource = () => {
  const { $api } = useApi();

  return useAsyncData<HomeUserSource>('home-user-source', async () => {
    const [ordersRes, storesRes] = await Promise.all([
      $api<HomeOrderApiItem[]>('/api/v1/orders/my'),
      $api<HomeStoreSummary[]>('/api/v1/stores'),
    ]);

    const orders = ordersRes.success && ordersRes.data ? ordersRes.data : [];
    const stores = storesRes.success && storesRes.data ? storesRes.data : [];

    const storeNameById = Object.fromEntries(
      stores.map(store => [store.id, store.name]),
    );

    const uniqueStoreIds = [...new Set(orders.map(order => order.storeId))];
    const menuResults = await Promise.all(
      uniqueStoreIds.map(async (storeId) => {
        const res = await $api<HomeMenuApiItem[]>(`/api/v1/stores/${storeId}/menus`);
        if (!res.success || !res.data) {
          return [] as HomeMenuApiItem[];
        }

        return res.data;
      }),
    );

    const menuNameById = Object.fromEntries(
      menuResults.flat().map(menu => [menu.id, menu.name]),
    );

    return {
      orders,
      menuNameById,
      storeNameById,
    };
  });
};
