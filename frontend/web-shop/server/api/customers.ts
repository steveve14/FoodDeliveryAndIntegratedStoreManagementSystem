import { fetchBackendData } from '../utils/backendApi';

interface Customer {
  id: string;
  name: string;
  email: string;
  avatar?: { src: string };
  status: string;
  location: string;
}

interface CustomerSummary {
  userId: string;
  ordersCount: number;
  lastOrderAt: string;
  grade: 'vip' | 'regular';
}

export default eventHandler(async (event) => {
  const [customers, summaries] = await Promise.all([
    fetchBackendData<Customer[]>(event, '/api/v1/users/frontend/customers'),
    fetchBackendData<CustomerSummary[]>(event, '/api/v1/orders/frontend/customer-summaries'),
  ]);
  const summaryMap = new Map(summaries.map(summary => [summary.userId, summary]));

  return customers.map((customer) => {
    const summary = summaryMap.get(customer.id);

    return {
      ...customer,
      orders: summary?.ordersCount ?? 0,
      lastOrderAt: summary?.lastOrderAt ?? null,
      grade: summary?.grade ?? 'regular',
    };
  });
});
