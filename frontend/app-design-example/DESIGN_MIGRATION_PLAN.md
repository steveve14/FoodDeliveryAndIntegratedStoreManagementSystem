# Android App 디자인 수정 계획서

> **기준 디자인:** "The Executive Inventory" Design System (DESIGN.md + 19개 HTML 프로토타입)  
> **대상 앱:** `app-android-shop`, `app-android-delivery`, `app-android-kiosk`, `app-android-user`  
> **작성일:** 2026-04-06

---

## 1. 현재 상태 진단 (AS-IS)

### 공통 문제점

| 항목 | 현재 상태 | 문제 |
|---|---|---|
| **색상 팔레트** | 4개 앱 동일: `green_500(#2E7D32)`, `green_700(#1B5E20)`, `orange_300(#FFB74D)`, black, white | 디자인 시스템의 Deep Navy 톤(`#003354`) 및 Multi-Surface 계층과 완전히 불일치 |
| **타이포그래피** | 시스템 기본 폰트만 사용 (커스텀 폰트 없음) | Manrope(Display) + Inter(Body) 이중 서체 미적용 |
| **테마 구조** | `Theme.MaterialComponents.DayNight.NoActionBar` — 색상 5개만 정의 | Material3 Surface 계층(7단계), Error/Tertiary 토큰 전무 |
| **레이아웃** | 하드코딩 색상(`#F5F5F5`, `#000000`, `#FFFFFF` 등) 직접 사용 | 테마 속성 참조 없이 인라인 컬러만 사용; 디자인 일관성 불가 |
| **카드/엘리베이션** | `CardView` + `cardElevation="2dp"` 표준 그림자 | "No-Shadow, Tonal Layering" 원칙 위반 |
| **보더** | 암묵적으로 1px 라인 구분 사용 | DESIGN.md "No-Line Rule" 위반 |
| **Drawable/아이콘** | `logo.png` 1개만 존재, 시스템 아이콘 사용 | Material Symbols Outlined 미적용 |
| **코너 라운딩** | `16dp`, `24dp` 등 비일관적 | DESIGN.md: `0.125rem(2dp)` ~ `0.75rem(12dp)` 범위 가이드 미준수 |

---

## 2. 목표 디자인 (TO-BE) 핵심 원칙

DESIGN.md의 **"The Architectural Ledger"** 컨셉을 Android 네이티브로 이식:

1. **Deep Navy 톤 + Tonal Surface 계층** — 7단계 Surface Color로 공간을 정의
2. **Manrope + Inter 이중 서체** — Display/Headline은 Manrope, Body/Label은 Inter
3. **No-Line Rule** — 1px 보더 대신 배경색 변화로 영역 구분
4. **Tonal Layering** — 전통적 그림자 대신 Surface 단계 차이로 높이감 표현
5. **Editorial Whitespace** — 섹션 간 `spacing-10(36dp)` 이상의 넉넉한 여백

---

## 3. Phase별 수정 계획

### Phase 1: 디자인 토큰 & 테마 기반 구축 (공통)

**작업 범위:** 4개 앱 공통 (`colors.xml`, `themes.xml`, `dimens.xml`, `type.xml`, font 리소스)

#### 3.1.1 colors.xml 전면 교체

```xml
<!-- 새 colors.xml (4개 앱 공통) -->
<resources>
    <!-- Primary -->
    <color name="primary">#FF003354</color>
    <color name="on_primary">#FFFFFFFF</color>
    <color name="primary_container">#FF004A77</color>
    <color name="on_primary_container">#FF86BAED</color>
    <color name="primary_fixed">#FFCFE5FF</color>
    <color name="primary_fixed_dim">#FF98CBFF</color>

    <!-- Secondary (Professional Green) -->
    <color name="secondary">#FF1B6D24</color>
    <color name="on_secondary">#FFFFFFFF</color>
    <color name="secondary_container">#FFA0F399</color>
    <color name="on_secondary_container">#FF217128</color>

    <!-- Tertiary -->
    <color name="tertiary">#FF4D2600</color>
    <color name="on_tertiary">#FFFFFFFF</color>
    <color name="tertiary_container">#FF6E3900</color>
    <color name="on_tertiary_container">#FFF0A465</color>

    <!-- Error -->
    <color name="error">#FFBA1A1A</color>
    <color name="on_error">#FFFFFFFF</color>
    <color name="error_container">#FFFFDAD6</color>
    <color name="on_error_container">#FF93000A</color>

    <!-- Surface 계층 (7단계) -->
    <color name="surface">#FFF8F9FD</color>
    <color name="surface_dim">#FFD9DADE</color>
    <color name="surface_bright">#FFF8F9FD</color>
    <color name="surface_container_lowest">#FFFFFFFF</color>
    <color name="surface_container_low">#FFF3F3F8</color>
    <color name="surface_container">#FFEDEEF2</color>
    <color name="surface_container_high">#FFE7E8EC</color>
    <color name="surface_container_highest">#FFE1E2E7</color>

    <!-- On Surface -->
    <color name="on_surface">#FF191C1F</color>
    <color name="on_surface_variant">#FF41474F</color>
    <color name="surface_variant">#FFE1E2E7</color>

    <!-- Outline -->
    <color name="outline">#FF727880</color>
    <color name="outline_variant">#FFC1C7D0</color>

    <!-- Inverse -->
    <color name="inverse_surface">#FF2E3134</color>
    <color name="inverse_on_surface">#FFF0F0F5</color>
    <color name="inverse_primary">#FF98CBFF</color>

    <!-- Legacy (하위호환) -->
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
```

#### 3.1.2 themes.xml Material3 마이그레이션

```xml
<!-- Theme.MaterialComponents → Theme.Material3 마이그레이션 -->
<style name="Theme.AppAndroid[AppName]" parent="Theme.Material3.DayNight.NoActionBar">
    <!-- Primary -->
    <item name="colorPrimary">@color/primary</item>
    <item name="colorOnPrimary">@color/on_primary</item>
    <item name="colorPrimaryContainer">@color/primary_container</item>
    <item name="colorOnPrimaryContainer">@color/on_primary_container</item>

    <!-- Secondary -->
    <item name="colorSecondary">@color/secondary</item>
    <item name="colorOnSecondary">@color/on_secondary</item>
    <item name="colorSecondaryContainer">@color/secondary_container</item>
    <item name="colorOnSecondaryContainer">@color/on_secondary_container</item>

    <!-- Tertiary -->
    <item name="colorTertiary">@color/tertiary</item>
    <item name="colorOnTertiary">@color/on_tertiary</item>
    <item name="colorTertiaryContainer">@color/tertiary_container</item>
    <item name="colorOnTertiaryContainer">@color/on_tertiary_container</item>

    <!-- Error -->
    <item name="colorError">@color/error</item>
    <item name="colorOnError">@color/on_error</item>
    <item name="colorErrorContainer">@color/error_container</item>
    <item name="colorOnErrorContainer">@color/on_error_container</item>

    <!-- Surface -->
    <item name="colorSurface">@color/surface</item>
    <item name="colorOnSurface">@color/on_surface</item>
    <item name="colorSurfaceVariant">@color/surface_variant</item>
    <item name="colorOnSurfaceVariant">@color/on_surface_variant</item>
    <item name="colorSurfaceContainerLowest">@color/surface_container_lowest</item>
    <item name="colorSurfaceContainerLow">@color/surface_container_low</item>
    <item name="colorSurfaceContainer">@color/surface_container</item>
    <item name="colorSurfaceContainerHigh">@color/surface_container_high</item>
    <item name="colorSurfaceContainerHighest">@color/surface_container_highest</item>

    <!-- Outline -->
    <item name="colorOutline">@color/outline</item>
    <item name="colorOutlineVariant">@color/outline_variant</item>
</style>
```

#### 3.1.3 타이포그래피 시스템 추가

- `res/font/` 디렉토리에 **Manrope** (Regular, Bold, ExtraBold) + **Inter** (Regular, Medium, SemiBold, Bold) 추가
- `type.xml`로 TextAppearance 정의:

| 토큰 | 서체 | 크기 | 용도 |
|---|---|---|---|
| `display_lg` | Manrope ExtraBold | 56sp (3.5rem) | Hero KPI 숫자 (매출 금액 등) |
| `display_md` | Manrope Bold | 45sp | 주요 수치 |
| `headline_lg` | Manrope Bold | 32sp | 페이지 제목 |
| `headline_md` | Manrope Bold | 28sp | 섹션 제목 |
| `headline_sm` | Manrope Bold | 24sp | 카드 제목 |
| `title_lg` | Inter SemiBold | 22sp | 서브 타이틀 |
| `title_md` | Inter SemiBold | 16sp | 리스트 항목 제목 |
| `body_lg` | Inter Regular | 16sp | 본문 (큰) |
| `body_md` | Inter Regular | 14sp | 본문 (기본) |
| `label_lg` | Inter Medium | 14sp | 버튼 텍스트 |
| `label_md` | Inter Medium | 12sp | 배지, 태그 |
| `label_sm` | Inter Medium | 11sp | 보조 정보 |

#### 3.1.4 간격 시스템 (dimens.xml)

```xml
<resources>
    <dimen name="spacing_1">4dp</dimen>   <!-- 0.25rem -->
    <dimen name="spacing_2">8dp</dimen>   <!-- 0.5rem -->
    <dimen name="spacing_3">12dp</dimen>  <!-- 0.75rem -->
    <dimen name="spacing_4">14dp</dimen>  <!-- 0.9rem -->
    <dimen name="spacing_5">16dp</dimen>  <!-- 1rem -->
    <dimen name="spacing_6">20dp</dimen>  <!-- 1.25rem -->
    <dimen name="spacing_8">32dp</dimen>  <!-- 2rem -->
    <dimen name="spacing_10">36dp</dimen> <!-- 2.25rem -->

    <dimen name="radius_sm">2dp</dimen>   <!-- 0.125rem -->
    <dimen name="radius_md">6dp</dimen>   <!-- 0.375rem -->
    <dimen name="radius_lg">4dp</dimen>   <!-- 0.25rem -->
    <dimen name="radius_xl">8dp</dimen>   <!-- 0.5rem -->
    <dimen name="radius_full">12dp</dimen><!-- 0.75rem -->
</resources>
```

#### 3.1.5 build.gradle 의존성 업그레이드

```groovy
// Material Components → Material 3
implementation 'com.google.android.material:material:1.12.0'
// (기존 1.10.0에서 업그레이드 — Material3 전체 지원)
```

---

### Phase 2: app-android-shop (매장 관리 앱) — 핵심 대상

> 디자인 예시의 **대부분**이 매장 관리 UI (대시보드, 주문 접수, 메뉴 관리, 리뷰, 매출·정산, 설정, 영업시간, 배달지역, 알림 설정)이므로 **가장 큰 변경 범위**.

#### 화면별 수정 계획

| # | 화면 (Fragment) | 매칭 디자인 예시 | 주요 수정 사항 |
|---|---|---|---|
| 1 | **HomeFragment (대시보드)** | _2 (모바일 대시보드), _12 (웹 대시보드) | Hero KPI 카드(`$12,840.00`), 주문 건수 프로그레스바, 운영 제어(영업중/종료 토글), 성과 곡선 차트, 최근 거래 기록 테이블 |
| 2 | **OrdersFragment (주문 관리)** | _1 (주문 관리 모바일), _17 (주문 접수 웹) | 실시간 대기열 카운터(준비중/신규), 신규 요청 카드(수락/거절), 처리 파이프라인(준비→배송→완료), 일일 할당량 |
| 3 | **ProductsFragment (메뉴 관리)** | _7 (메뉴 관리 모바일), _19 (메뉴 관리 웹) | 카테고리 탭 + 필터링, 메뉴 카드(이미지+가격+판매중 토글+품절 배지), FAB로 새 메뉴 추가 |
| 4 | **DeliveryFragment (배달)** | _11 (배달 지역 설정) | 지도 영역(반경 표시), 최소 주문 금액, 배달팁 설정(고정/거리별), 지역별 상세(동 단위 토글) |
| 5 | **SettingsFragment (설정)** | _9 (설정 허브 모바일), _18 (설정 웹) | 설정 허브 구조(상점 정보 관리 / 결제 및 정산 / 앱 설정), 서브 페이지 네비게이션 |
| 6 | **[신규] SalesFragment** | _6 (매출·정산 모바일), _16 (매출 통계 웹) | 총 매출 Hero, 정산 예정 금액, 인기 메뉴 테이블, 성과 지표(유지율, 평균 주문 금액) |
| 7 | **[신규] ReviewsFragment** | _13 (리뷰 관리 웹) | 전체 평점, 리뷰 수, 미답변 리뷰, 리뷰 목록 + 답변 기능 |
| 8 | **[신규] OperatingHoursFragment** | _5 (영업시간 달력 모바일), _8 (영업시간 설정 모바일), _14 (영업시간 웹), _15 (영업시간 달력 웹) | 월간 캘린더(정상/휴무 표시), 요일별 운영 시간, 브레이크타임 설정, 즉시 일시정지 |
| 9 | **[신규] PaymentSettingsFragment** | _3 (결제 수단 관리) | 정산 계좌 정보, 결제 수단 토글(카드/카카오페이/네이버페이/애플페이), 자동 정산 알림 |
| 10 | **[신규] NotificationSettingsFragment** | _4 (알림 설정 모바일), _16 (알림 설정 웹) | 주문 알림, 리뷰 알림, 마케팅 알림 토글, 알림 수단(푸시/SMS) |

#### 컴포넌트 수정 상세

##### 2.1 TopAppBar 교체
- **AS-IS:** 표준 `Toolbar` 또는 없음
- **TO-BE:** Material3 `TopAppBarLayout` + 햄버거 메뉴 + 앱 제목 (Manrope Bold)
  - 배경: `surface_container` (#edeef2)
  - 제목 색: `primary` (#003354)
  - 알림 벨 아이콘: Material Symbols `notifications`

##### 2.2 NavigationDrawer 교체
- **AS-IS:** 기본 `DrawerLayout` + `NavigationView`
- **TO-BE:** 좌측 Deep Navy 사이드바 구조
  - 배경: `surface_container` (#edeef2)
  - 활성 항목: `primary_fixed` (#cfe5ff) 배경 + `primary` (#003354) 텍스트
  - 항목: Material Symbols 아이콘 + Inter 텍스트
  - 메뉴: 대시보드, 주문 접수, 메뉴 관리, 리뷰 관리, 매출 통계, 매장 관리, 설정

##### 2.3 BottomNavigation 교체
- **AS-IS:** 기본 BottomNavigationView (초록색)
- **TO-BE:** Material3 `NavigationBarView`
  - 배경: `surface_container_lowest` (#ffffff)
  - 활성 아이콘: `primary` (#003354) + Filled 변형
  - 비활성: `on_surface_variant` (#41474f) + Outlined 변형
  - 항목: 홈, 주문, 리뷰, 매출 (4개)

##### 2.4 카드 컴포넌트
- **AS-IS:** `CardView` with `elevation="2dp"`, `cornerRadius="16dp"`
- **TO-BE:** `MaterialCardView` — **그림자 제거**, 배경 `surface_container_lowest` 위에 `surface` 기반 tonal layering
  - `cardElevation="0dp"`
  - `strokeWidth="0dp"` (No-Line Rule)
  - 배경: `surface_container_lowest` (#ffffff)
  - 코너: `radius_xl` (8dp)

##### 2.5 버튼 시스템
- **Primary Button:** `primary`(#003354) 배경, `on_primary`(#ffffff) 텍스트, `radius_md`(6dp)
- **Secondary Button:** `secondary_container`(#a0f399) 배경, `on_secondary_container`(#217128) 텍스트
- **Tertiary Button:** 배경 없음, `primary` 텍스트만

##### 2.6 Status Badge
- **재고 있음:** `secondary_container` 배경 + `on_secondary_container` 텍스트
- **품절:** `error_container` (#ffdad6) 배경 + `on_error_container` (#93000a) 텍스트
- **처리 중:** `primary_fixed` (#cfe5ff) 배경 + `primary` 텍스트
- **NEW 배지:** `tertiary_container` 배경 + `on_tertiary_container` 텍스트

##### 2.7 태블릿 대응 (sw600dp)
- DrawerLayout → 상시 표시 `NavigationRail` 또는 고정 사이드바
- 주문 접수: Master-Detail 레이아웃 (좌측 리스트 + 우측 상세)
- 대시보드: 2~3 컬럼 그리드

---

### Phase 3: app-android-user (사용자 앱)

> 사용자 앱은 디자인 예시에 직접 대응하는 화면이 적으나, 동일한 디자인 시스템 톤을 적용.

| # | 화면 | 수정 사항 |
|---|---|---|
| 1 | **HomeFragment (가게 목록)** | 검색바 → Tonal Surface 기반, 카테고리 Chip → `surface_container_high` 배경, 가게 카드 → Tonal Layering (그림자 제거) |
| 2 | **StoreDetailFragment** | 헤더 Hero 이미지, 가게 정보 `headline_md`, 메뉴 리스트 → Inter Body, 가격 → Manrope Bold |
| 3 | **CartFragment** | 장바구니 아이템 카드 (No-Line), 결제 금액 → `display_md` Manrope, 주문하기 버튼 → Primary CTA |
| 4 | **ProfileFragment** | 사용자 인사말 → `headline_sm`, 주문 내역 링크, 설정 메뉴 리스트 |
| 5 | **SearchFragment (주문내역)** | 주문 상태 badge (처리중/배송완료/취소됨), 주문 카드 Tonal Layering |

#### BottomNavigation
- 항목: 홈, 주문내역, 장바구니, 내 정보
- 스타일: Phase 2의 2.3과 동일 적용

---

### Phase 4: app-android-delivery (배달기사 앱)

| # | 화면 | 수정 사항 |
|---|---|---|
| 1 | **ActiveDeliveryFragment** | 진행중 배달 카드 → 주소 + 상태 badge + 프로그레스, Tonal Layering |
| 2 | **HistoryFragment** | 배달 내역 리스트 → alternating `surface_dim` 배경, 완료/취소 badge |
| 3 | **ProfileFragment** | 배달 실적 KPI (Manrope Display), 누적 건수/수입 |

#### BottomNavigation  
- 항목: 진행중 배달, 배달 내역, 내 정보
- 활성 아이콘: `primary` (#003354)

---

### Phase 5: app-android-kiosk (키오스크 앱)

> 키오스크는 대형 화면(태블릿) 전용이므로 웹 디자인 예시(_12~_19)의 레이아웃 참조.

| # | 화면 | 수정 사항 |
|---|---|---|
| 1 | **MenuOrderFragment** | 좌측 카테고리 사이드바 + 우측 메뉴 그리드(3열), 메뉴 카드(이미지+이름+가격), 카테고리 아이콘 Tab |
| 2 | **CartFragment** | 우측 고정 장바구니 패널, 아이템 리스트 + 수량 조절, 총액 `display_md` |
| 3 | **OrderStatusFragment** | 주문 번호 `display_lg`, 상태 프로그레스 스텝 (접수→조리→완료) |

#### 키오스크 특화
- **항상 가로 모드** 고정 (`KioskConfig` 활용)
- BottomNav → 좌측 고정 `NavigationRail`
- 터치 타겟 최소 `48dp` → `64dp`로 확대
- 폰트 스케일: 전체 1.25x 확대

---

## 4. 신규 공통 리소스 생성 목록

| 파일 | 위치 | 내용 |
|---|---|---|
| `res/font/manrope_regular.ttf` | 4개 앱 공통 | Manrope Regular 서체 |
| `res/font/manrope_bold.ttf` | 4개 앱 공통 | Manrope Bold 서체 |
| `res/font/manrope_extrabold.ttf` | 4개 앱 공통 | Manrope ExtraBold 서체 |
| `res/font/inter_regular.ttf` | 4개 앱 공통 | Inter Regular 서체 |
| `res/font/inter_medium.ttf` | 4개 앱 공통 | Inter Medium 서체 |
| `res/font/inter_semibold.ttf` | 4개 앱 공통 | Inter SemiBold 서체 |
| `res/font/inter_bold.ttf` | 4개 앱 공통 | Inter Bold 서체 |
| `res/values/type.xml` | 4개 앱 공통 | TextAppearance 스타일 정의 |
| `res/values/dimens.xml` | 4개 앱 공통 | Spacing + Radius 토큰 |
| `res/values/colors.xml` | 4개 앱 공통 | 전체 디자인 토큰 색상 (35+개) |
| `res/values/themes.xml` | 앱별 테마명 상이 | Material3 테마 정의 |
| `res/values/styles.xml` | 4개 앱 공통 | 카드, 버튼, Badge 등 컴포넌트 스타일 |

---

## 5. 레이아웃 하드코딩 색상 제거 매핑

현재 레이아웃에 직접 사용된 하드코딩 색상을 테마 속성으로 교체:

| 하드코딩 값 | → 교체 대상 | 용도 |
|---|---|---|
| `#F5F5F5` | `?attr/colorSurface` | 화면 배경 |
| `#FFFFFF` | `?attr/colorSurfaceContainerLowest` | 카드 배경 |
| `#000000` | `?attr/colorOnSurface` | 기본 텍스트 (단, #000000 사용 금지) |
| `#222222` | `?attr/colorOnSurface` (#191C1F) | 제목 텍스트 |
| `#666666` | `?attr/colorOnSurfaceVariant` (#41474F) | 보조 텍스트 |
| `#FF5722` | `?attr/colorPrimary` (#003354) | CTA 버튼 |
| `#9E9E9E` | `?attr/colorOutline` (#727880) | 비활성/보조 버튼 |
| `#D1C4E9`, `#FFE082` | 이미지 placeholder → 제거 | 개발용 더미 |

---

## 6. 신규 화면 (Fragment) 추가 목록

### app-android-shop 전용

| Fragment | 디자인 기반 | 설명 |
|---|---|---|
| `SalesFragment` | _6, _16 | 매출·정산 대시보드 |
| `ReviewsFragment` | _13 | 리뷰 관리 (목록+답변) |
| `OperatingHoursFragment` | _5, _8, _14, _15 | 영업시간 캘린더 + 설정 |
| `PaymentSettingsFragment` | _3 | 결제 수단 관리 |
| `NotificationSettingsFragment` | _4, _16 | 알림 설정 |
| `DeliveryZoneFragment` | _11 | 배달 지역 설정 (기존 DeliveryFragment 리팩터) |
| `StoreInfoFragment` | _18 (설정 웹) | 기본 정보 관리 |

---

## 7. 우선순위 & 실행 순서

```
Phase 1 (공통 기반)     ━━━━━━━━━━ [1주]
  ├─ colors.xml 교체 (4개 앱)
  ├─ themes.xml Material3 마이그레이션
  ├─ 폰트 리소스 추가
  ├─ type.xml / dimens.xml 생성
  └─ build.gradle Material 버전 업그레이드

Phase 2 (app-android-shop) ━━━━━━━━ [3~4주]
  ├─ 2-A: 기존 화면 리디자인 (Home, Orders, Products, Settings)
  ├─ 2-B: 신규 화면 (Sales, Reviews, OperatingHours)
  ├─ 2-C: 신규 화면 (Payment, Notification, DeliveryZone)
  └─ 2-D: 태블릿 레이아웃 (sw600dp)

Phase 3 (app-android-user)  ━━━━━━━ [1~2주]
  └─ 기존 화면 리디자인 (Home, StoreDetail, Cart, Profile, Search)

Phase 4 (app-android-delivery) ━━━━ [1주]
  └─ 기존 화면 리디자인 (ActiveDelivery, History, Profile)

Phase 5 (app-android-kiosk)  ━━━━━━ [1~2주]
  └─ 태블릿 최적화 + 기존 화면 리디자인
```

---

## 8. 기술적 주의사항

1. **Material3 마이그레이션 주의:** `Theme.MaterialComponents` → `Theme.Material3` 변경 시 일부 위젯 스타일이 자동 변경됨. 점진적 마이그레이션을 위해 `MaterialComponentsBridge` 테마 사용 고려
2. **하드코딩 제거 우선:** 레이아웃 XML에서 `#XXXXXX` 직접 색상 참조를 모두 `?attr/` 또는 `@color/` 참조로 교체
3. **No-Line Rule:** `CardView`의 `strokeWidth`, `divider`, `<View>` 구분선 모두 제거 → Surface 색상 차이로 대체
4. **#000000 금지:** 최대 어두운 텍스트는 `on_surface` (#191C1F)
5. **폰트 라이선스:** Manrope (SIL Open Font License), Inter (SIL Open Font License) — 모두 무료 상업 사용 가능
6. **접근성:** 모든 텍스트-배경 조합 WCAG AA (4.5:1) 준수 확인 필수
