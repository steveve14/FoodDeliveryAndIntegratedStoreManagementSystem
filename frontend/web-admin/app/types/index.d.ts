import type { AvatarProps } from '@nuxt/ui';

export type UserStatus = 'subscribed' | 'unsubscribed' | 'bounced';
export type SaleStatus = 'paid' | 'failed' | 'refunded';
export type Period = 'daily' | 'weekly' | 'monthly';
export interface User {
  id: string;
  name: string;
  email: string;
  avatar?: AvatarProps;
  status: UserStatus;
  location: string;
}

export interface Mail {
  id: number;
  unread?: boolean;
  from: User;
  subject: string;
  body: string;
  date: string;
}

export interface Member {
  name: string;
  username: string;
  role: 'member' | 'owner';
  avatar: AvatarProps;
}

export interface Stat {
  title: string;
  icon: string;
  value: number | string;
  variation: number;
  link?: string;
  formatter?: (value: number) => string;
}

export interface Sale {
  id: string;
  date: string;
  status: SaleStatus;
  email: string;
  amount: number;
}

export interface Notification {
  id: number;
  unread?: boolean;
  sender: User;
  body: string;
  date: string;
}

export interface Range {
  start: Date;
  end: Date;
}

// ─── Backend API 타입 ────────────────────────────────

/** Backend 공통 응답 래퍼 */
export interface ApiResponse<T = unknown> {
  success: boolean;
  data: T;
  error: string | null;
}

/** Backend StoreDto */
export interface StoreDto {
  id: string;
  name: string;
  address: string;
  phone: string;
  category: string;
  status: string;
  latitude: number;
  longitude: number;
  minOrderAmount: number;
  ratingAvg: number;
  description: string;
  openingHours: string;
  ownerId: string;
}

/** Backend MenuDto */
export interface MenuDto {
  id: string;
  storeId: string;
  name: string;
  description: string;
  price: number;
  available: boolean;
  createdAt: string;
}

/** Backend UserDto */
export interface UserDto {
  id: string;
  email: string;
  name: string;
  roles: string;
  createdAt: string;
}

/** Backend TokenResponse */
export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}
