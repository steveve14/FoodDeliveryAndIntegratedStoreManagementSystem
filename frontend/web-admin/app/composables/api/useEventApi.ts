// app/composables/api/useEventApi.ts
import type {
  ApiResponse,
  EventDto,
  CreateEventRequest
} from '~/types/api'

/**
 * 이벤트 서비스 API (service-event)
 * Gateway 경로: /api/v1/events/**
 */
export const useEventApi = () => {
  const { $api } = useApi()

  /** 이벤트 생성 */
  const createEvent = (body: CreateEventRequest) =>
    $api<EventDto>('/api/v1/events', {
      method: 'POST',
      body
    })

  /** 이벤트 단건 조회 */
  const getEvent = (eventId: string) =>
    $api<EventDto>(`/api/v1/events/${eventId}`)

  return {
    createEvent,
    getEvent
  }
}
