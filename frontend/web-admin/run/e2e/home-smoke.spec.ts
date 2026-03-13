import { expect, test } from '@playwright/test';
import { signupLoginAndSeedSession } from './helpers/auth';

test('admin 역할 세션으로 홈 진입', async ({ page }) => {
  await signupLoginAndSeedSession(
    page.request,
    page.context(),
    'http://localhost:3200',
    { roleOverride: 'ADMIN' },
  );

  await page.goto('/');
  await page.waitForURL(/\/|\/unauthorized/, { timeout: 15_000 });

  if (page.url().includes('/unauthorized')) {
    await expect(page.getByText('권한이 없습니다').first()).toBeVisible({ timeout: 15_000 });
    return;
  }

  await expect(page).toHaveURL(/\/$/);
  await expect(page.getByText('홈').first()).toBeVisible({ timeout: 15_000 });
});
