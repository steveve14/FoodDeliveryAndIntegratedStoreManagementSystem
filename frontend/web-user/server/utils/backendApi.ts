import { getHeader } from 'h3';
import type { H3Event } from 'h3';

interface ApiResponse<T> {
  success: boolean;
  code: number;
  message: string;
  data: T;
}

export async function fetchBackendData<T> (event: H3Event, path: string) {
  const config = useRuntimeConfig(event);
  const baseUrl = config.public.apiBaseUrl as string;
  const cookie = getHeader(event, 'cookie');
  const authorization = getHeader(event, 'authorization');

  const response = await $fetch<ApiResponse<T>>(`${baseUrl}${path}`, {
    headers: {
      ...(cookie ? { cookie } : {}),
      ...(authorization ? { authorization } : {}),
    },
  });

  return response.data;
}
