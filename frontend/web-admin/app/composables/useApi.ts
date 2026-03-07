/**
 * Backend API 호출 유틸리티
 * Nitro 프록시를 통해 Gateway(localhost:8000)의 /api/v1/** 엔드포인트를 호출합니다.
 *
 * 주요 기능:
 * - Authorization 헤더 자동 첨부
 * - 401 응답 시 refreshToken으로 자동 갱신 후 원래 요청 재시도
 * - 갱신 실패 시 로그인 페이지로 리다이렉트
 */

import type { ApiResponse } from '~/types/api'

/** 토큰 갱신 중복 방지를 위한 싱글턴 프로미스 */
let refreshPromise: Promise<boolean> | null = null

export const useApi = () => {
  const accessToken = useCookie('access-token')
  const refreshToken = useCookie('refresh-token')

  /**
   * refreshToken으로 accessToken 갱신 (중복 방지)
   */
  async function tryRefreshToken(): Promise<boolean> {
    if (!refreshToken.value) return false

    // 이미 갱신 중이면 기존 프로미스를 대기
    if (refreshPromise) return refreshPromise

    refreshPromise = (async () => {
      try {
        const res = await $fetch<ApiResponse<{ accessToken: string, refreshToken: string }>>('/api/v1/auth/refresh', {
          method: 'POST',
          body: { refreshToken: refreshToken.value }
        })
        if (res.success) {
          accessToken.value = res.data.accessToken
          refreshToken.value = res.data.refreshToken
          return true
        }
        return false
      } catch {
        return false
      } finally {
        refreshPromise = null
      }
    })()

    return refreshPromise
  }

  /**
   * $fetch 래퍼 — Authorization 헤더 자동 첨부 + 401 자동 갱신 재시도
   * @param url - API 경로 (예: '/api/v1/stores')
   * @param options - $fetch 옵션
   * @returns ApiResponse<T> 형태의 응답
   */
  async function $api<T>(url: string, options?: Parameters<typeof $fetch>[1]): Promise<ApiResponse<T>> {
    const doFetch = (token?: string | null) => {
      return $fetch<ApiResponse<T>>(url, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          ...(options?.headers as Record<string, string>),
          ...(token ? { Authorization: `Bearer ${token}` } : {})
        }
      })
    }

    try {
      return await doFetch(accessToken.value)
    } catch (err: any) {
      // 401 Unauthorized → 토큰 갱신 시도 후 재시도
      if (err?.response?.status === 401) {
        const refreshed = await tryRefreshToken()
        if (refreshed) {
          // 새 토큰으로 원래 요청 재시도
          return await doFetch(accessToken.value)
        }

        // 갱신 실패 → 세션 정리 + 로그인 페이지 이동
        accessToken.value = null
        refreshToken.value = null
        useCookie('user-session').value = null
        navigateTo('/login')
      }
      throw err
    }
  }

  return { $api, accessToken, refreshToken }
}
