import {
  expect,
  type APIRequestContext,
  type BrowserContext,
  type Cookie,
} from '@playwright/test';

export const GATEWAY_URL = 'http://localhost:8000';

const extractSetCookieValue = (setCookieHeaders: string[], cookieName: string) => {
  const header = setCookieHeaders.find(value => value.startsWith(`${cookieName}=`));
  return header?.match(new RegExp(`^${cookieName}=([^;]+)`))?.[1] ?? null;
};

type SeedOptions = {
  roleOverride?: 'USER' | 'STORE' | 'ADMIN';
};

export const signupLoginAndSeedSession = async (
  request: APIRequestContext,
  context: BrowserContext,
  appUrl: string,
  options: SeedOptions = {},
) => {
  const unique = `${Date.now()}-${Math.floor(Math.random() * 100000)}`;
  const email = `e2e-user-${unique}@example.com`;
  const password = 'Password123!';

  const signupRes = await request.post(`${GATEWAY_URL}/api/v1/auth/signup`, {
    data: { name: 'E2E User', email, password },
  });
  expect(signupRes.ok()).toBeTruthy();
  const signupPayload = await signupRes.json();
  expect(signupPayload.success).toBeTruthy();

  const loginRes = await request.post(`${GATEWAY_URL}/api/v1/auth/login`, {
    data: { email, password },
  });
  expect(loginRes.ok()).toBeTruthy();

  const setCookieHeaders = loginRes
    .headersArray()
    .filter((header: { name: string; value: string }) => header.name.toLowerCase() === 'set-cookie')
    .map((header: { name: string; value: string }) => header.value);

  const accessToken = extractSetCookieValue(setCookieHeaders, 'access-token');
  const refreshToken = extractSetCookieValue(setCookieHeaders, 'refresh-token');
  expect(accessToken).toBeTruthy();

  const sessionData = {
    ...signupPayload.data,
    role: options.roleOverride || signupPayload.data.role,
  };

  const cookies = [
    {
      name: 'user-session',
      value: encodeURIComponent(JSON.stringify(sessionData)),
      url: appUrl,
      sameSite: 'Lax' as const,
      httpOnly: false,
      secure: false,
    },
    {
      name: 'access-token',
      value: accessToken as string,
      url: appUrl,
      sameSite: 'Lax' as const,
      httpOnly: true,
      secure: false,
    },
  ];

  if (refreshToken) {
    cookies.push({
      name: 'refresh-token',
      value: refreshToken,
      url: appUrl,
      sameSite: 'Lax' as const,
      httpOnly: true,
      secure: false,
    });
  }

  await context.addCookies(cookies);

  const seededCookies = await context.cookies(appUrl);
  expect(seededCookies.some((cookie: Cookie) => cookie.name === 'user-session')).toBeTruthy();
  expect(seededCookies.some((cookie: Cookie) => cookie.name === 'access-token')).toBeTruthy();

  return {
    email,
    password,
    accessToken: accessToken as string,
  };
};
