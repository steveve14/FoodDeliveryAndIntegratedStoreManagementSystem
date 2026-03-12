export const STORE_CATEGORY_OPTIONS = [
  { label: '한식', value: '한식' },
  { label: '중식', value: '중식' },
  { label: '일식', value: '일식' },
  { label: '양식', value: '양식' },
  { label: '치킨', value: '치킨' },
  { label: '피자', value: '피자' },
  { label: '버거', value: '버거' },
  { label: '카페', value: '카페' },
  { label: '디저트', value: '디저트' },
  { label: '분식', value: '분식' },
  { label: '족발/보쌈', value: '족발/보쌈' },
  { label: '야식', value: '야식' },
  { label: '아시안', value: '아시안' },
  { label: '샐러드', value: '샐러드' },
  { label: '도시락', value: '도시락' },
];

export const STORE_CATEGORY_LABELS = STORE_CATEGORY_OPTIONS.map(
  option => option.label,
);

export const DEFAULT_STORE_CATEGORY_LABEL = STORE_CATEGORY_LABELS[0];
