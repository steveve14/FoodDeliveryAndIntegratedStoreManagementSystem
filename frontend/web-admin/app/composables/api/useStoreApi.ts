// app/composables/api/useStoreApi.ts
import type {
  StoreDto,
  CreateStoreRequest,
  StoreQueryParams,
} from '~/types/api';

/**
 * 가게 서비스 API (service-store)
 * Gateway 경로: /api/v1/stores/**
 */
export const useStoreApi = () => {
  const { $api } = useApi();

  /** 가게 목록 조회 (카테고리/상태 필터 가능) */
  const getStores = (params?: StoreQueryParams) => {
    const query = new URLSearchParams();
    if (params?.category) {
      query.set('category', params.category);
    }
    if (params?.status) {
      query.set('status', params.status);
    }
    const qs = query.toString();
    return $api<StoreDto[]>(`/api/v1/stores${qs ? `?${qs}` : ''}`);
  };

  /** 가게 단건 조회 */
  const getStore = (storeId: string) =>
    $api<StoreDto>(`/api/v1/stores/${storeId}`);

  /** 가게 등록 */
  const createStore = (body: CreateStoreRequest) =>
    $api<StoreDto>('/api/v1/stores', {
      method: 'POST',
      body,
    });

  return {
    getStores,
    getStore,
    createStore,
  };
};
