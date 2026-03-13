import { expect, type APIRequestContext, type BrowserContext } from '@playwright/test';

export const GATEWAY_URL = 'http://localhost:8000';

const extractSetCookieValue = (setCookieHeaders: string[], cookieName: string) => {
  const header = setCookieHeaders.find(value => value.startsWith(`${cookieName}=`));
  return header?.match(new RegExp(`^${cookieName}=([^;]+)`))?.[1] ?? null;
};

export const signupLoginAndSeedSession = async (
  request: APIRequestContext,
  context: BrowserContext,
  appUrl: string,
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
    .filter(header => header.name.toLowerCase() === 'set-cookie')
    .map(header => header.value);

  const accessToken = extractSetCookieValue(setCookieHeaders, 'access-token');
  const refreshToken = extractSetCookieValue(setCookieHeaders, 'refresh-token');
  expect(accessToken).toBeTruthy();

  const cookies = [
    {
      name: 'user-session',
      value: encodeURIComponent(JSON.stringify(signupPayload.data)),
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
  expect(seededCookies.some(cookie => cookie.name === 'user-session')).toBeTruthy();
  expect(seededCookies.some(cookie => cookie.name === 'access-token')).toBeTruthy();

  return {
    email,
    password,
    accessToken: accessToken as string,
  };
};
