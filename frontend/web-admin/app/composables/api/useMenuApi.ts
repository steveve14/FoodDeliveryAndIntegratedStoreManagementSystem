// app/composables/api/useMenuApi.ts
import type {
  ApiResponse,
  MenuDto,
  CreateMenuRequest,
  UpdateMenuRequest
} from '~/types/api'

/**
 * 메뉴 서비스 API (service-store 내부)
 * Gateway 경로: /api/v1/stores/{storeId}/menus/**
 */
export const useMenuApi = () => {
  const { $api } = useApi()

  /** 메뉴 목록 조회 */
  const getMenus = (storeId: string) =>
    $api<MenuDto[]>(`/api/v1/stores/${storeId}/menus`)

  /** 메뉴 추가 */
  const createMenu = (storeId: string, body: CreateMenuRequest) =>
    $api<MenuDto>(`/api/v1/stores/${storeId}/menus`, {
      method: 'POST',
      body
    })

  /** 메뉴 수정 */
  const updateMenu = (storeId: string, menuId: string, body: UpdateMenuRequest) =>
    $api<MenuDto>(`/api/v1/stores/${storeId}/menus/${menuId}`, {
      method: 'PUT',
      body
    })

  /** 메뉴 삭제 */
  const deleteMenu = (storeId: string, menuId: string) =>
    $api<null>(`/api/v1/stores/${storeId}/menus/${menuId}`, {
      method: 'DELETE'
    })

  return {
    getMenus,
    createMenu,
    updateMenu,
    deleteMenu
  }
}
