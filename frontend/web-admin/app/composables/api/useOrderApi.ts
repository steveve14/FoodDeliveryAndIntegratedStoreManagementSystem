// app/composables/api/useOrderApi.ts
import type {
  OrderDto,
  OrderStatus,
  CreateOrderRequest,
} from '~/types/api';

/**
 * 주문 서비스 API (service-order)
 * Gateway 경로: /api/v1/orders/**
 */
export const useOrderApi = () => {
  const { $api } = useApi();

  /** 주문 생성 */
  const createOrder = (body: CreateOrderRequest) =>
    $api<OrderDto>('/api/v1/orders', {
      method: 'POST',
      body,
    });

  /** 주문 단건 조회 */
  const getOrder = (orderId: string) =>
    $api<OrderDto>(`/api/v1/orders/${orderId}`);

  /** 주문 상태 변경 */
  const updateOrderStatus = (orderId: string, status: OrderStatus) =>
    $api<OrderDto>(`/api/v1/orders/${orderId}/status`, {
      method: 'PATCH',
      body: { status },
    });

  return {
    createOrder,
    getOrder,
    updateOrderStatus,
  };
};
