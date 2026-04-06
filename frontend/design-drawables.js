const fs = require('fs');
const path = require('path');

const apps = ['app-android-shop', 'app-android-user', 'app-android-delivery', 'app-android-kiosk'];
const base = __dirname;

// ═══════════════════════ DRAWABLE FILES ═══════════════════════

const drawables = {
    // ── Gradient backgrounds ──
    'bg_gradient_primary.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/gradient_primary_start"
        android:centerColor="@color/gradient_primary_mid"
        android:endColor="@color/gradient_primary_end"
        android:angle="135" />
    <corners android:radius="@dimen/radius_xl" />
</shape>`,

    'bg_gradient_hero.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/gradient_hero_start"
        android:endColor="@color/gradient_hero_end"
        android:angle="160" />
    <corners android:radius="@dimen/radius_xl" />
</shape>`,

    'bg_gradient_accent.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/gradient_accent_start"
        android:endColor="@color/gradient_accent_end"
        android:angle="135" />
    <corners android:radius="@dimen/radius_xl" />
</shape>`,

    'bg_gradient_success.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/gradient_success_start"
        android:endColor="@color/gradient_success_end"
        android:angle="135" />
    <corners android:radius="@dimen/radius_xl" />
</shape>`,

    'bg_gradient_surface.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/surface"
        android:endColor="@color/surface_container_low"
        android:angle="270" />
</shape>`,

    // ── Glassmorphism cards ──
    'bg_glass_card.xml': `<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/glass_white" />
            <corners android:radius="@dimen/radius_xl" />
        </shape>
    </item>
    <item>
        <shape android:shape="rectangle">
            <stroke android:width="1dp" android:color="@color/glass_border" />
            <corners android:radius="@dimen/radius_xl" />
        </shape>
    </item>
</layer-list>`,

    'bg_glass_card_dark.xml': `<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/glass_dark" />
            <corners android:radius="@dimen/radius_xl" />
        </shape>
    </item>
    <item>
        <shape android:shape="rectangle">
            <stroke android:width="1dp" android:color="@color/glass_border" />
            <corners android:radius="@dimen/radius_xl" />
        </shape>
    </item>
</layer-list>`,

    'bg_glass_search.xml': `<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/glass_white" />
            <corners android:radius="@dimen/radius_full" />
        </shape>
    </item>
    <item>
        <shape android:shape="rectangle">
            <stroke android:width="1dp" android:color="@color/glass_border" />
            <corners android:radius="@dimen/radius_full" />
        </shape>
    </item>
</layer-list>`,

    // ── Button backgrounds ──
    'bg_button_gradient.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/gradient_primary_start"
        android:endColor="@color/gradient_primary_end"
        android:angle="90" />
    <corners android:radius="@dimen/radius_md" />
</shape>`,

    'bg_button_accent.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/gradient_accent_start"
        android:endColor="@color/gradient_accent_end"
        android:angle="90" />
    <corners android:radius="@dimen/radius_md" />
</shape>`,

    // ── Ripple effects ──
    'ripple_primary.xml': `<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/primary_fixed_dim">
    <item android:id="@android:id/mask">
        <shape android:shape="rectangle">
            <solid android:color="@color/white" />
            <corners android:radius="@dimen/radius_lg" />
        </shape>
    </item>
</ripple>`,

    'ripple_surface.xml': `<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/surface_container_high">
    <item android:id="@android:id/mask">
        <shape android:shape="rectangle">
            <solid android:color="@color/white" />
            <corners android:radius="@dimen/radius_lg" />
        </shape>
    </item>
</ripple>`,

    // ── Updated badge backgrounds ──
    'bg_badge_success.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/secondary_container" />
    <corners android:radius="@dimen/radius_full" />
</shape>`,

    'bg_badge_error.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/error_container" />
    <corners android:radius="@dimen/radius_full" />
</shape>`,

    'bg_badge_info.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/primary_container" />
    <corners android:radius="@dimen/radius_full" />
</shape>`,

    'bg_badge_new.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/tertiary_container" />
    <corners android:radius="@dimen/radius_full" />
</shape>`,

    // ── Nav item background ──
    'nav_item_background.xml': `<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_checked="true">
        <shape android:shape="rectangle">
            <solid android:color="@color/primary_container" />
            <corners android:radius="@dimen/radius_full" />
        </shape>
    </item>
    <item android:state_activated="true">
        <shape android:shape="rectangle">
            <solid android:color="@color/primary_container" />
            <corners android:radius="@dimen/radius_full" />
        </shape>
    </item>
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@android:color/transparent" />
            <corners android:radius="@dimen/radius_full" />
        </shape>
    </item>
</selector>`,

    // ── Shimmer placeholder ──
    'bg_shimmer.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/shimmer_base" />
    <corners android:radius="@dimen/radius_md" />
</shape>`,

    // ── Divider ──
    'bg_divider.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/outline_variant" />
    <size android:height="1dp" />
</shape>`,

    // ── Dot indicator (green online) ──
    'ic_dot_green.xml': `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <solid android:color="@color/secondary" />
    <size android:width="8dp" android:height="8dp" />
</shape>`,

    // ═══════════════════ VECTOR ICONS (Outlined, modern) ═══════════════════

    'ic_home.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M12,5.69L17,10.19V18H15V12H9V18H7V10.19L12,5.69M12,3L2,12H5V20H11V14H13V20H19V12H22L12,3Z" />
</vector>`,

    'ic_search.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M15.5,14h-0.79l-0.28,-0.27C15.41,12.59 16,11.11 16,9.5 16,5.91 13.09,3 9.5,3S3,5.91 3,9.5 5.91,16 9.5,16c1.61,0 3.09,-0.59 4.23,-1.57l0.27,0.28v0.79l5,4.99L20.49,19l-4.99,-5zM9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z" />
</vector>`,

    'ic_cart.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M7,18c-1.1,0 -1.99,0.9 -1.99,2S5.9,22 7,22s2,-0.9 2,-2 -0.9,-2 -2,-2zM1,3v2h2l3.6,7.59 -1.35,2.44C5.09,15.32 5,15.65 5,16c0,1.1 0.9,2 2,2h12v-2H7.42c-0.14,0 -0.25,-0.11 -0.25,-0.25l0.03,-0.12 0.9,-1.63h7.45c0.75,0 1.41,-0.41 1.75,-1.03l3.58,-6.49C21.25,5.15 20.78,4.5 20,4.5H5.21l-0.94,-2H1zM17,18c-1.1,0 -1.99,0.9 -1.99,2s0.89,2 1.99,2 2,-0.9 2,-2 -0.9,-2 -2,-2z" />
</vector>`,

    'ic_profile.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M12,5.9c1.16,0 2.1,0.94 2.1,2.1s-0.94,2.1 -2.1,2.1S9.9,9.16 9.9,8s0.94,-2.1 2.1,-2.1M12,14.9c2.97,0 6.1,1.46 6.1,2.1v1.1H5.9V17c0,-0.64 3.13,-2.1 6.1,-2.1M12,4C9.79,4 8,5.79 8,8s1.79,4 4,4 4,-1.79 4,-4 -1.79,-4 -4,-4zM12,13c-2.67,0 -8,1.34 -8,4v3h16v-3c0,-2.66 -5.33,-4 -8,-4z" />
</vector>`,

    'ic_orders.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M14,2H6C4.9,2 4.01,2.9 4.01,4L4,20c0,1.1 0.89,2 1.99,2H18c1.1,0 2,-0.9 2,-2V8l-6,-6zM16,18H8v-2h8v2zM16,14H8v-2h8v2zM13,9V3.5L18.5,9H13z" />
</vector>`,

    'ic_delivery.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M20,8h-3V4H3c-1.1,0 -2,0.9 -2,2v11h2c0,1.66 1.34,3 3,3s3,-1.34 3,-3h6c0,1.66 1.34,3 3,3s3,-1.34 3,-3h2v-5l-3,-4zM6,18.5c-0.83,0 -1.5,-0.67 -1.5,-1.5s0.67,-1.5 1.5,-1.5 1.5,0.67 1.5,1.5 -0.67,1.5 -1.5,1.5zM19.5,9.5l1.96,2.5H17V9.5h2.5zM18,18.5c-0.83,0 -1.5,-0.67 -1.5,-1.5s0.67,-1.5 1.5,-1.5 1.5,0.67 1.5,1.5 -0.67,1.5 -1.5,1.5z" />
</vector>`,

    'ic_dashboard.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M3,13h8V3H3v10zM3,21h8v-6H3v6zM13,21h8V11h-8v10zM13,3v6h8V3h-8z" />
</vector>`,

    'ic_products.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M11.99,2l-10,4.5v11l10,4.5 10,-4.5v-11l-10,-4.5zM12,4.21L19.5,7.5 12,10.79 4.5,7.5 12,4.21zM4.5,9.08l6.5,2.93v7.49l-6.5,-2.93V9.08zM13,19.49v-7.49l6.5,-2.93v7.49L13,19.49z" />
</vector>`,

    'ic_reviews.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M12,17.27L18.18,21l-1.64,-7.03L22,9.24l-7.19,-0.61L12,2 9.19,8.63 2,9.24l5.46,4.73L5.82,21z" />
</vector>`,

    'ic_sales.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M5,9.2h3V19H5V9.2zM10.6,5h2.8v14h-2.8V5zM16.2,13H19v6h-2.8v-6z" />
</vector>`,

    'ic_settings.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M19.14,12.94c0.04,-0.3 0.06,-0.61 0.06,-0.94c0,-0.32 -0.02,-0.64 -0.07,-0.94l2.03,-1.58c0.18,-0.14 0.23,-0.41 0.12,-0.61l-1.92,-3.32c-0.12,-0.22 -0.37,-0.29 -0.59,-0.22l-2.39,0.96c-0.5,-0.38 -1.03,-0.7 -1.62,-0.94L14.4,2.81c-0.04,-0.24 -0.24,-0.41 -0.48,-0.41h-3.84c-0.24,0 -0.43,0.17 -0.47,0.41L9.25,5.35C8.66,5.59 8.12,5.92 7.63,6.29L5.24,5.33c-0.22,-0.08 -0.47,0 -0.59,0.22L2.74,8.87C2.62,9.08 2.66,9.34 2.86,9.48l2.03,1.58C4.84,11.36 4.8,11.69 4.8,12s0.02,0.64 0.07,0.94l-2.03,1.58c-0.18,0.14 -0.23,0.41 -0.12,0.61l1.92,3.32c0.12,0.22 0.37,0.29 0.59,0.22l2.39,-0.96c0.5,0.38 1.03,0.7 1.62,0.94l0.36,2.54c0.05,0.24 0.24,0.41 0.48,0.41h3.84c0.24,0 0.44,-0.17 0.47,-0.41l0.36,-2.54c0.59,-0.24 1.13,-0.56 1.62,-0.94l2.39,0.96c0.22,0.08 0.47,0 0.59,-0.22l1.92,-3.32c0.12,-0.22 0.07,-0.47 -0.12,-0.61L19.14,12.94zM12,15.6c-1.98,0 -3.6,-1.62 -3.6,-3.6s1.62,-3.6 3.6,-3.6s3.6,1.62 3.6,3.6S13.98,15.6 12,15.6z" />
</vector>`,

    'ic_menu_hamburger.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface"
        android:pathData="M3,18h18v-2H3v2zM3,13h18v-2H3v2zM3,6v2h18V6H3z" />
</vector>`,

    'ic_arrow_right.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M10,6L8.59,7.41 13.17,12l-4.58,4.59L10,18l6,-6z" />
</vector>`,

    'ic_arrow_back.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface"
        android:pathData="M20,11H7.83l5.59,-5.59L12,4l-8,8 8,8 1.41,-1.41L7.83,13H20v-2z" />
</vector>`,

    'ic_notification.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M12,22c1.1,0 2,-0.9 2,-2h-4c0,1.1 0.89,2 2,2zM18,16v-5c0,-3.07 -1.64,-5.64 -4.5,-6.32V4c0,-0.83 -0.67,-1.5 -1.5,-1.5s-1.5,0.67 -1.5,1.5v0.68C7.63,5.36 6,7.92 6,11v5l-2,2v1h16v-1l-2,-2z" />
</vector>`,

    'ic_history.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M13,3c-4.97,0 -9,4.03 -9,9H1l3.89,3.89 0.07,0.14L9,12H6c0,-3.87 3.13,-7 7,-7s7,3.13 7,7 -3.13,7 -7,7c-1.93,0 -3.68,-0.79 -4.94,-2.06l-1.42,1.42C8.27,19.99 10.51,21 13,21c4.97,0 9,-4.03 9,-9s-4.03,-9 -9,-9zM12,8v5l4.28,2.54 0.72,-1.21 -3.5,-2.08V8H12z" />
</vector>`,

    'ic_store.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M20,4H4v2h16V4zM21,14v-2l-1,-5H4l-1,5v2h1v6h10v-6h4v6h2v-6h1zM12,18H6v-4h6v4z" />
</vector>`,

    'ic_menu_food.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M8.1,13.34l2.83,-2.83L3.91,3.5c-1.56,1.56 -1.56,4.09 0,5.66l4.19,4.18zM14.88,11.53c1.53,0.71 3.68,0.21 5.27,-1.38 1.91,-1.91 2.28,-4.65 0.81,-6.12 -1.46,-1.46 -4.2,-1.1 -6.12,0.81 -1.59,1.59 -2.09,3.74 -1.38,5.27L3.7,19.87l1.41,1.41L12,14.41l6.88,6.88 1.41,-1.41L13.41,13l1.47,-1.47z" />
</vector>`,

    'ic_info.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM13,17h-2v-6h2v6zM13,9h-2V7h2v2z" />
</vector>`,

    // ── Menu icon (replace ic_menu.xml) ──
    'ic_menu.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface"
        android:pathData="M3,18h18v-2H3v2zM3,13h18v-2H3v2zM3,6v2h18V6H3z" />
</vector>`,

    'ic_active_delivery.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M12,2C8.13,2 5,5.13 5,9c0,5.25 7,13 7,13s7,-7.75 7,-13c0,-3.87 -3.13,-7 -7,-7zM12,11.5c-1.38,0 -2.5,-1.12 -2.5,-2.5s1.12,-2.5 2.5,-2.5 2.5,1.12 2.5,2.5 -1.12,2.5 -2.5,2.5z" />
</vector>`,

    'ic_order_status.xml': `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/on_surface_variant"
        android:pathData="M19,3h-4.18C14.4,1.84 13.3,1 12,1c-1.3,0 -2.4,0.84 -2.82,2H5c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2V5c0,-1.1 -0.9,-2 -2,-2zM12,3c0.55,0 1,0.45 1,1s-0.45,1 -1,1 -1,-0.45 -1,-1 0.45,-1 1,-1zM10,17l-4,-4 1.41,-1.41L10,14.17l6.59,-6.59L18,9l-8,8z" />
</vector>`,
};

// ═══════════════════════ WRITE DRAWABLE FILES ═══════════════════════
for (const app of apps) {
    const drawableDir = path.join(base, app, 'app', 'src', 'main', 'res', 'drawable');
    
    // Ensure directory exists
    if (!fs.existsSync(drawableDir)) {
        fs.mkdirSync(drawableDir, { recursive: true });
    }
    
    for (const [filename, content] of Object.entries(drawables)) {
        fs.writeFileSync(path.join(drawableDir, filename), content, 'utf8');
    }
    
    console.log(`${app}: ${Object.keys(drawables).length} drawables written`);
}

console.log('=== ALL DRAWABLES DONE ===');
