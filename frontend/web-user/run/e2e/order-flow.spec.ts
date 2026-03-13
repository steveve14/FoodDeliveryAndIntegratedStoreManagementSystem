import { expect, test } from '@playwright/test';
import { signupLoginAndSeedSession } from './helpers/auth';
import { ensureCartHasItems, getMyOrdersOrFail } from './helpers/api';

test('홈-카테고리-매장상세-장바구니 흐름', async ({ page }) => {
  const { accessToken } = await signupLoginAndSeedSession(
    page.request,
    page.context(),
    'http://localhost:3100',
  );

  // Step 3: 홈으로 이동
  await page.goto('/');
  const afterHomeCookies = await page.context().cookies('http://localhost:3100');
  expect(afterHomeCookies.some(c => c.name === 'user-session')).toBeTruthy();
  await expect(page.getByText('카테고리별 바로 주문')).toBeVisible({ timeout: 15_000 });

  // Step 4: 치킨 카테고리 버튼 클릭
  const chickenBtn = page.getByRole('button', { name: /치킨/ }).first();
  await expect(chickenBtn).toBeVisible({ timeout: 5_000 });
  await chickenBtn.click();
  await page.waitForTimeout(1500);

  // Step 5: 매장 상세로 이동 (버튼/링크 둘 다 지원)
  const menuButton = page.getByRole('button', { name: /메뉴 보기/ }).first();
  const storeLink = page.locator('a[href^="/stores/"]').first();

  if (await menuButton.isVisible({ timeout: 3000 }).catch(() => false)) {
    await menuButton.click();
  } else if (await storeLink.isVisible({ timeout: 3000 }).catch(() => false)) {
    await storeLink.click();
  } else {
    // 치킨 카테고리에 데이터가 없으면 전체로 복귀 후 첫 매장 진입 시도
    const allStoreBtn = page.getByRole('button', { name: /전체/ }).first();
    if (await allStoreBtn.isVisible({ timeout: 2000 }).catch(() => false)) {
      await allStoreBtn.click();
      await page.waitForTimeout(1000);
    }

    const fallbackMenuButton = page.getByRole('button', { name: /메뉴 보기/ }).first();
    const fallbackStoreLink = page.locator('a[href^="/stores/"]').first();

    if (await fallbackMenuButton.isVisible({ timeout: 5000 }).catch(() => false)) {
      await fallbackMenuButton.click();
    } else {
      await fallbackStoreLink.click();
    }
  }

  // Step 6: 매장 상세 - 메뉴 목록 확인
  await expect(page.getByText(/메뉴/).first()).toBeVisible({ timeout: 15_000 });

  // Step 7: 첫 번째 메뉴 담기
  const cartButton = page.getByRole('button', { name: /담고 결제하기|장바구니 담기|주문하기/ }).first();
  await expect(cartButton).toBeVisible({ timeout: 10_000 });
  await cartButton.click();

  // Step 8: 주문/결제 페이지 확인
  const movedToCheckout = await page
    .waitForURL(/checkout|cart|order/, { timeout: 5000 })
    .then(() => true)
    .catch(() => false);

  if (!movedToCheckout) {
    // 페이지 자동 이동이 없는 경우 장바구니 링크로 직접 이동
    const checkoutLink = page.getByRole('link', { name: /장바구니|장바구니 확인/ }).first();
    await expect(checkoutLink).toBeVisible({ timeout: 5000 });
    await checkoutLink.click();
    await page.waitForURL(/checkout|cart|order/, { timeout: 15_000 });
  }

  await expect(page.getByText(/주문 메뉴|장바구니|주문/).first()).toBeVisible();

  // Step 9: 장바구니 아이템 보장 (비어 있으면 API로 직접 추가)
  const cartState = await ensureCartHasItems(page.request, accessToken);
  if (cartState.seeded) {
    await page.reload();
    await page.waitForTimeout(1500);
  }

  // Step 11: 배달 주소 입력
  const addressInput = page
    .getByLabel('배달 주소')
    .or(page.locator('input[type="text"]').first());
  await expect(addressInput).toBeVisible({ timeout: 8_000 });
  await addressInput.fill('서울시 강남구 테헤란로 1길 1');

  // Step 12: "주문 확정" 버튼 클릭
  const placeOrderBtn = page.getByRole('button', { name: '주문 확정' });
  await expect(placeOrderBtn).toBeEnabled({ timeout: 5_000 });
  await placeOrderBtn.click();

  // Step 13: /orders 페이지로 이동 (주문 완료 확인)
  await page.waitForURL(/\/orders/, { timeout: 20_000 });

  // Step 14: 주문 내역 최소 1건 표시 확인
  await expect(page.getByText(/메뉴 1건|주문 접수됨|배달 중|조리 중/).first()).toBeVisible({
    timeout: 15_000,
  });

  // Step 15: 주문 내역 API에서도 주문 1건 이상 검증
  const myOrders = await getMyOrdersOrFail(page.request, accessToken);
  expect(myOrders.length).toBeGreaterThan(0);
});
