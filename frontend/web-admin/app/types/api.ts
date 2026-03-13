// app/types/api.ts
// ─── Backend API 공통 타입 및 DTO 정의 ────────────────────────────

/* ───── 공통 응답 래퍼 ───── */

export interface ApiResponse<T = unknown> {
  success: boolean;
  data: T;
  error: string | null;
}

/* ───── Auth ───── */

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RefreshRequest {
  refreshToken: string;
}

export interface LogoutRequest {
  refreshToken: string;
}

/* ───── User ───── */

export type UserRole = 'USER' | 'STORE' | 'ADMIN';

export interface UserDto {
  id: string;
  email: string;
  name: string;
  roles: string;
  createdAt: string;
}

export interface UserProfileDto {
  id: string;
  email: string;
  name: string;
  username?: string | null;
  phone?: string | null;
  avatarUrl?: string | null;
  location?: string | null;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
}

export interface UpdateProfileRequest {
  name: string;
  username?: string;
  phone?: string;
  avatarUrl?: string;
  location?: string;
}

/* ───── Address ───── */

export interface AddressDto {
  id: string;
  userId: string;
  label: string;
  line1: string;
  line2: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  primaryAddress: boolean;
  createdAt: string;
}

export interface CreateAddressRequest {
  label: string;
  line1: string;
  line2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  primaryAddress?: boolean;
}

export type UpdateAddressRequest = CreateAddressRequest;

/* ───── Store ───── */

export type StoreStatus = 'OPEN' | 'CLOSED';
export type StoreCategory =
  | 'KOREAN' |
  'CHINESE' |
  'JAPANESE' |
  'WESTERN' |
  'CHICKEN' |
  'PIZZA' |
  'BURGER' |
  'CAFE' |
  'DESSERT' |
  'SNACK' |
  'NIGHT' |
  'BOSSAM' |
  'ASIAN' |
  'SALAD' |
  'LUNCHBOX' |
  'OTHER';

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

export interface CreateStoreRequest {
  name: string;
  address: string;
  phone?: string;
  category: string;
  status: StoreStatus;
  latitude?: number;
  longitude?: number;
  minOrderAmount?: number;
  ratingAvg?: number;
  description?: string;
  openingHours?: string;
  ownerId: string;
}

export interface StoreQueryParams {
  category?: string;
  status?: StoreStatus;
}

/* ───── Menu ───── */

export interface MenuDto {
  id: string;
  storeId: string;
  name: string;
  description: string;
  price: number;
  available: boolean;
  createdAt: string;
}

export interface CreateMenuRequest {
  name: string;
  description?: string;
  price: number;
  available: boolean;
}

export type UpdateMenuRequest = CreateMenuRequest;

/* ───── Order ───── */

export type OrderStatus =
  | 'CREATED' |
  'COOKING' |
  'DELIVERING' |
  'DONE' |
  'CANCELLED';

export interface OrderItemDto {
  productId: string;
  quantity: number;
  price: number;
}

export interface OrderDto {
  id: string;
  userId: string;
  storeId: string;
  totalAmount: number;
  items: OrderItemDto[];
  status: OrderStatus;
  createdAt: string;
}

export interface CreateOrderRequest {
  userId: string;
  items: OrderItemDto[];
  address?: string;
  totalAmount: number;
}

/* ───── Delivery ───── */

export type DeliveryStatus =
  | 'SCHEDULED' |
  'PENDING' |
  'PICKED_UP' |
  'IN_TRANSIT' |
  'DELIVERED' |
  'CANCELLED';

export interface DeliveryDto {
  id: string;
  orderId: string;
  courier: string;
  status: DeliveryStatus;
  deliveryFee: number;
  scheduledAt: string;
}

export interface CreateDeliveryRequest {
  orderId: string;
  address: string;
  courier?: string;
}

/* ───── Event ───── */

export interface EventDto {
  id: string;
  type: string;
  payload: string;
  createdAt: string;
}

export interface CreateEventRequest {
  type: string;
  payload: string;
}
