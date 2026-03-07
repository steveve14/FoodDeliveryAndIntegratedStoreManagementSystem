// app/composables/api/useDeliveryApi.ts
import type {
  ApiResponse,
  DeliveryDto,
  DeliveryStatus,
  CreateDeliveryRequest
} from '~/types/api'

/**
 * 배달 서비스 API (service-delivery)
 * Gateway 경로: /api/v1/deliveries/**
 */
export const useDeliveryApi = () => {
  const { $api } = useApi()

  /** 배달 생성 */
  const createDelivery = (body: CreateDeliveryRequest) =>
    $api<DeliveryDto>('/api/v1/deliveries', {
      method: 'POST',
      body
    })

  /** 배달 단건 조회 */
  const getDelivery = (deliveryId: string) =>
    $api<DeliveryDto>(`/api/v1/deliveries/${deliveryId}`)

  /** 배달 상태 변경 */
  const updateDeliveryStatus = (deliveryId: string, status: DeliveryStatus) =>
    $api<DeliveryDto>(`/api/v1/deliveries/${deliveryId}/status`, {
      method: 'PATCH',
      body: { status }
    })

  return {
    createDelivery,
    getDelivery,
    updateDeliveryStatus
  }
}
