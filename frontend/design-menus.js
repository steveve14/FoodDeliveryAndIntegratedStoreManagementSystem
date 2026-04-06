const fs = require('fs');
const path = require('path');
const base = __dirname;

function writeMenu(app, file, content) {
    const dir = path.join(base, app, 'app', 'src', 'main', 'res', 'menu');
    fs.writeFileSync(path.join(dir, file), content, 'utf8');
}

// ═══ SHOP ═══

writeMenu('app-android-shop', 'bottom_nav_menu.xml', `<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_home"
        android:icon="@drawable/ic_home"
        android:title="홈" />
    <item
        android:id="@+id/nav_search"
        android:icon="@drawable/ic_search"
        android:title="검색" />
    <item
        android:id="@+id/nav_cart"
        android:icon="@drawable/ic_cart"
        android:title="장바구니" />
    <item
        android:id="@+id/nav_profile"
        android:icon="@drawable/ic_profile"
        android:title="내 정보" />
</menu>`);

writeMenu('app-android-shop', 'drawer_menu.xml', `<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <group android:checkableBehavior="single">
        <item
            android:id="@+id/nav_home"
            android:icon="@drawable/ic_dashboard"
            android:title="대시보드" />
        <item
            android:id="@+id/nav_delivery"
            android:icon="@drawable/ic_delivery"
            android:title="배달 관리" />
        <item
            android:id="@+id/nav_order_reception"
            android:icon="@drawable/ic_orders"
            android:title="주문 관리" />
        <item
            android:id="@+id/nav_menu_management"
            android:icon="@drawable/ic_products"
            android:title="메뉴 관리" />
        <item
            android:id="@+id/nav_review_management"
            android:icon="@drawable/ic_reviews"
            android:title="리뷰 관리" />
        <item
            android:id="@+id/nav_sales_statistics"
            android:icon="@drawable/ic_sales"
            android:title="매출·정산" />
        <item
            android:id="@+id/nav_store_management"
            android:icon="@drawable/ic_store"
            android:title="가게 관리" />
        <item
            android:id="@+id/nav_settings"
            android:icon="@drawable/ic_settings"
            android:title="설정" />
    </group>
</menu>`);

// ═══ USER ═══

writeMenu('app-android-user', 'bottom_nav_menu.xml', `<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_home"
        android:icon="@drawable/ic_home"
        android:title="홈" />
    <item
        android:id="@+id/nav_search"
        android:icon="@drawable/ic_orders"
        android:title="주문내역" />
    <item
        android:id="@+id/nav_cart"
        android:icon="@drawable/ic_cart"
        android:title="장바구니" />
    <item
        android:id="@+id/nav_profile"
        android:icon="@drawable/ic_profile"
        android:title="내 정보" />
</menu>`);

// ═══ DELIVERY ═══

writeMenu('app-android-delivery', 'bottom_nav_menu.xml', `<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_active"
        android:icon="@drawable/ic_active_delivery"
        android:title="진행중 배달" />
    <item
        android:id="@+id/nav_history"
        android:icon="@drawable/ic_history"
        android:title="배달 내역" />
    <item
        android:id="@+id/nav_profile"
        android:icon="@drawable/ic_profile"
        android:title="내 정보" />
</menu>`);

// ═══ KIOSK ═══

writeMenu('app-android-kiosk', 'bottom_nav_menu.xml', `<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_home"
        android:icon="@drawable/ic_home"
        android:title="홈" />
    <item
        android:id="@+id/nav_search"
        android:icon="@drawable/ic_search"
        android:title="검색" />
    <item
        android:id="@+id/nav_cart"
        android:icon="@drawable/ic_cart"
        android:title="장바구니" />
    <item
        android:id="@+id/nav_profile"
        android:icon="@drawable/ic_profile"
        android:title="내 정보" />
</menu>`);

writeMenu('app-android-kiosk', 'kiosk_nav_menu.xml', `<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_menu_order"
        android:icon="@drawable/ic_menu_food"
        android:title="메뉴 주문" />
    <item
        android:id="@+id/nav_order_status"
        android:icon="@drawable/ic_order_status"
        android:title="주문 현황" />
</menu>`);

console.log('All menu XMLs updated with custom icons');
