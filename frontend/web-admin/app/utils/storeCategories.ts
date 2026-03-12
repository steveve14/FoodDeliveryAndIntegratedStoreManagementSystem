import type { StoreCategory } from '~/types/api';

type StoreCategoryOption = {
  label: string;
  value: StoreCategory;
};

export const STORE_CATEGORY_OPTIONS: StoreCategoryOption[] = [
  { label: '한식', value: 'KOREAN' },
  { label: '중식', value: 'CHINESE' },
  { label: '일식', value: 'JAPANESE' },
  { label: '양식', value: 'WESTERN' },
  { label: '치킨', value: 'CHICKEN' },
  { label: '피자', value: 'PIZZA' },
  { label: '버거', value: 'BURGER' },
  { label: '카페', value: 'CAFE' },
  { label: '디저트', value: 'DESSERT' },
  { label: '분식', value: 'SNACK' },
  { label: '족발/보쌈', value: 'BOSSAM' },
  { label: '야식', value: 'NIGHT' },
  { label: '아시안', value: 'ASIAN' },
  { label: '샐러드', value: 'SALAD' },
  { label: '도시락', value: 'LUNCHBOX' },
  { label: '기타', value: 'OTHER' },
];

export const STORE_CATEGORY_FILTER_OPTIONS = [
  { label: '전체 업종', value: 'all' },
  ...STORE_CATEGORY_OPTIONS,
];

const STORE_CATEGORY_LABEL_MAP = Object.fromEntries(
  STORE_CATEGORY_OPTIONS.map(option => [option.value, option.label]),
) as Record<StoreCategory, string>;

const KOREAN_LABEL_TO_CODE = Object.fromEntries(
  STORE_CATEGORY_OPTIONS.map(option => [option.label, option.value]),
);

export function normalizeStoreCategoryValue (category: string | null | undefined) {
  if (!category) {
    return 'OTHER';
  }

  const trimmed = category.trim();
  if (!trimmed) {
    return 'OTHER';
  }

  return (KOREAN_LABEL_TO_CODE[trimmed] ||
    trimmed.toUpperCase()) as StoreCategory;
}

export function getStoreCategoryLabel (category: string | null | undefined) {
  const normalized = normalizeStoreCategoryValue(category);
  return STORE_CATEGORY_LABEL_MAP[normalized] || '기타';
}
