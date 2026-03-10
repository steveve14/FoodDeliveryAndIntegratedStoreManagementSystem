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
  const response = await $fetch<ApiResponse<T>>(`${baseUrl}${path}`);

  return response.data;
}