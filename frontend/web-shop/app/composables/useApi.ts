/**
 * Backend API 호출 유틸리티
 * Nitro 프록시를 통해 Gateway(localhost:8000)의 /api/v1/** 엔드포인트를 호출합니다.
 */

/** Backend 공통 응답 래퍼 */
export interface ApiResponse<T = unknown> {
  success: boolean
  data: T
  error: string | null
}

export const useApi = () => {
  const accessToken = useCookie('access-token')

  /**
   * $fetch 래퍼 — Authorization 헤더를 자동으로 첨부합니다.
   */
  async function $api<T>(url: string, options?: Parameters<typeof $fetch>[1]): Promise<ApiResponse<T>> {
    return $fetch<ApiResponse<T>>(url, {
      ...options,
      headers: {
        ...(options?.headers as Record<string, string>),
        ...(accessToken.value ? { Authorization: `Bearer ${accessToken.value}` } : {})
      },
      onResponseError({ response }) {
        if (response.status === 401) {
          accessToken.value = null
          navigateTo('/login')
        }
      }
    })
  }

  return { $api, accessToken }
}
