// app/composables/api/useUserApi.ts
import type {
  UserDto,
  UserProfileDto,
  RegisterRequest,
  UpdateProfileRequest,
  AddressDto,
  CreateAddressRequest,
  UpdateAddressRequest,
} from '~/types/api';

/**
 * 회원 서비스 API (service-user)
 * Gateway 경로: /api/v1/users/**
 */
export const useUserApi = () => {
  const { $api } = useApi();

  /** 회원가입 (인증 불필요) */
  const register = (body: RegisterRequest) =>
    $api<UserDto>('/api/v1/users/register', {
      method: 'POST',
      body,
    });

  /** 이메일로 사용자 조회 */
  const getUserByEmail = (email: string) =>
    $api<UserDto>(`/api/v1/users?email=${encodeURIComponent(email)}`);

  /** 프로필 조회 */
  const getProfile = (userId: string) =>
    $api<UserProfileDto>(`/api/v1/users/${userId}/profile`);

  /** 프로필 수정 */
  const updateProfile = (userId: string, body: UpdateProfileRequest) =>
    $api<UserProfileDto>(`/api/v1/users/${userId}/profile`, {
      method: 'PUT',
      body,
    });

  /** 회원 탈퇴 */
  const deleteUser = (userId: string) =>
    $api<null>(`/api/v1/users/${userId}`, {
      method: 'DELETE',
    });

  /** 내 배송지 목록 조회 */
  const getMyAddresses = () =>
    $api<AddressDto[]>('/api/v1/users/me/addresses');

  /** 배송지 추가 */
  const createAddress = (body: CreateAddressRequest) =>
    $api<AddressDto>('/api/v1/users/me/addresses', {
      method: 'POST',
      body,
    });

  /** 배송지 수정 */
  const updateAddress = (addressId: string, body: UpdateAddressRequest) =>
    $api<AddressDto>(`/api/v1/users/me/addresses/${addressId}`, {
      method: 'PUT',
      body,
    });

  /** 배송지 삭제 */
  const deleteAddress = (addressId: string) =>
    $api<null>(`/api/v1/users/me/addresses/${addressId}`, {
      method: 'DELETE',
    });

  return {
    register,
    getUserByEmail,
    getProfile,
    updateProfile,
    deleteUser,
    getMyAddresses,
    createAddress,
    updateAddress,
    deleteAddress,
  };
};
