import type { OrderDto, StoreDto } from '~/types/api';

interface FrontendCustomerDto {
  id: string;
  name: string;
  email: string;
  status: string;
  location: string;
}

export interface AdminHomeSource {
  customers: FrontendCustomerDto[];
  stores: StoreDto[];
  orders: OrderDto[];
}

export const useAdminHomeSource = () => {
  const { $api } = useApi();

  return useAsyncData<AdminHomeSource>('admin-home-source', async () => {
    const [customersRes, storesRes] = await Promise.all([
      $api<FrontendCustomerDto[]>('/api/v1/users/frontend/customers'),
      $api<StoreDto[]>('/api/v1/stores'),
    ]);

    const customers = customersRes.success && customersRes.data ? customersRes.data : [];
    const stores = storesRes.success && storesRes.data ? storesRes.data : [];

    const orderResults = await Promise.all(
      stores.map(async (store) => {
        try {
          const res = await $api<OrderDto[]>(`/api/v1/orders/store/${store.id}`);
          return res.success && res.data ? res.data : [];
        } catch {
          return [];
        }
      }),
    );

    return {
      customers,
      stores,
      orders: orderResults.flat(),
    };
  });
};
