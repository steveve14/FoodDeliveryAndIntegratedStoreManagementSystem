import { expect, type APIRequestContext } from '@playwright/test';
import { GATEWAY_URL } from './auth';

type ApiResponse<T> = {
  success: boolean;
  data: T;
};

const authHeaders = (accessToken: string) => ({
  Cookie: `access-token=${accessToken}`,
});

export const getCartOrFail = async (request: APIRequestContext, accessToken: string) => {
  const response = await request.get(`${GATEWAY_URL}/api/v1/users/me/cart`, {
    headers: authHeaders(accessToken),
  });
  expect(response.ok()).toBeTruthy();

  const payload = (await response.json()) as ApiResponse<Array<Record<string, unknown>>>;
  expect(payload.success).toBeTruthy();
  expect(Array.isArray(payload.data)).toBeTruthy();
  return payload.data;
};

export const ensureCartHasItems = async (request: APIRequestContext, accessToken: string) => {
  const currentItems = await getCartOrFail(request, accessToken);
  if (currentItems.length > 0) {
    return { seeded: false, itemCount: currentItems.length };
  }

  const storeListRes = await request.get(`${GATEWAY_URL}/api/v1/stores`);
  expect(storeListRes.ok()).toBeTruthy();
  const storeListPayload = (await storeListRes.json()) as ApiResponse<Array<Record<string, unknown>>>;
  expect(storeListPayload.success).toBeTruthy();

  const firstStore = storeListPayload.data?.[0] as { id: string; name: string } | undefined;
  expect(firstStore).toBeTruthy();

  const menuListRes = await request.get(`${GATEWAY_URL}/api/v1/stores/${firstStore!.id}/menus`);
  expect(menuListRes.ok()).toBeTruthy();
  const menuListPayload = (await menuListRes.json()) as ApiResponse<Array<Record<string, unknown>>>;
  expect(menuListPayload.success).toBeTruthy();

  const firstMenu = menuListPayload.data?.[0] as { id: string; name: string; price: number } | undefined;
  expect(firstMenu).toBeTruthy();

  const addCartRes = await request.post(`${GATEWAY_URL}/api/v1/users/me/cart`, {
    headers: authHeaders(accessToken),
    data: {
      storeId: firstStore!.id,
      storeName: firstStore!.name,
      menuId: firstMenu!.id,
      menuName: firstMenu!.name,
      quantity: 1,
      price: firstMenu!.price,
    },
  });
  expect(addCartRes.ok()).toBeTruthy();

  const afterSeedItems = await getCartOrFail(request, accessToken);
  expect(afterSeedItems.length).toBeGreaterThan(0);
  return { seeded: true, itemCount: afterSeedItems.length };
};

export const getMyOrdersOrFail = async (request: APIRequestContext, accessToken: string) => {
  const response = await request.get(`${GATEWAY_URL}/api/v1/orders/my`, {
    headers: authHeaders(accessToken),
  });
  expect(response.ok()).toBeTruthy();

  const payload = (await response.json()) as ApiResponse<Array<Record<string, unknown>>>;
  expect(payload.success).toBeTruthy();
  expect(Array.isArray(payload.data)).toBeTruthy();
  return payload.data;
};
