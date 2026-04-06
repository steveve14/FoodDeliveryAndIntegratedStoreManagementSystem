const fs = require('fs');
const path = require('path');
const base = __dirname;

function resLayout(app) {
    return path.join(base, app, 'app', 'src', 'main', 'res', 'layout');
}
function write(app, file, content) {
    fs.writeFileSync(path.join(resLayout(app), file), content, 'utf8');
}

// ═══════════════════════════════════════════════════════════════════
// APP-ANDROID-SHOP LAYOUTS
// ═══════════════════════════════════════════════════════════════════

write('app-android-shop', 'activity_main.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.coordinatorlayout.widget.CoordinatorLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/surface">

        <com.google.android.material.appbar.AppBarLayout
            android:id="@+id/app_bar_layout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/surface"
            app:elevation="0dp">

            <com.google.android.material.appbar.MaterialToolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="@dimen/top_bar_height"
                android:background="@color/surface"
                app:navigationIcon="@drawable/ic_menu_hamburger"
                app:navigationIconTint="@color/on_surface"
                app:titleTextColor="@color/on_surface"
                app:titleTextAppearance="@style/TextAppearance.App.Title.Large" />
        </com.google.android.material.appbar.AppBarLayout>

        <androidx.fragment.app.FragmentContainerView
            android:id="@+id/fragment_container"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layout_behavior="@string/appbar_scrolling_view_behavior" />

    </androidx.coordinatorlayout.widget.CoordinatorLayout>

    <com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="300dp"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:fitsSystemWindows="true"
        android:background="@color/surface_container_lowest"
        app:itemTextColor="@color/on_surface"
        app:itemIconTint="@color/on_surface_variant"
        app:itemBackground="@drawable/nav_item_background"
        app:itemShapeAppearance="@style/ShapeAppearance.App.SmallComponent"
        app:headerLayout="@layout/nav_header"
        app:menu="@menu/drawer_menu" />

</androidx.drawerlayout.widget.DrawerLayout>`);

write('app-android-shop', 'nav_header.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:background="@drawable/bg_gradient_primary"
    android:padding="@dimen/spacing_6">

    <ImageView
        android:id="@+id/iv_nav_logo"
        android:layout_width="@dimen/avatar_size_lg"
        android:layout_height="@dimen/avatar_size_lg"
        android:src="@drawable/logo"
        android:contentDescription="매장 로고"
        android:layout_marginBottom="@dimen/spacing_4" />

    <TextView
        android:id="@+id/tv_store_name"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="가게 이름"
        android:textAppearance="@style/TextAppearance.App.Title.Large"
        android:textColor="@color/on_primary"
        android:layout_marginBottom="@dimen/spacing_1" />

    <TextView
        android:id="@+id/tv_store_status"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="영업중"
        android:textAppearance="@style/TextAppearance.App.Label.Small"
        android:textColor="@color/primary_fixed_dim" />
</LinearLayout>`);

write('app-android-shop', 'fragment_home.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <!-- ── Hero KPI Card (Gradient) ──────────────── -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_5"
            style="@style/Widget.App.Card.Hero">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@drawable/bg_gradient_primary"
                android:orientation="vertical"
                android:padding="@dimen/spacing_6">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="오늘의 매출"
                    android:textAppearance="@style/TextAppearance.App.Label.Medium"
                    android:textColor="@color/primary_fixed_dim"
                    android:layout_marginBottom="@dimen/spacing_2" />

                <TextView
                    android:id="@+id/tv_today_revenue"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="₩0"
                    android:textAppearance="@style/TextAppearance.App.Display.Medium"
                    android:textColor="@color/on_primary" />

                <LinearLayout
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:gravity="center_vertical"
                    android:layout_marginTop="@dimen/spacing_3">

                    <TextView
                        android:id="@+id/tv_revenue_trend"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="어제 대비 +0%"
                        android:textAppearance="@style/TextAppearance.App.Label.Small"
                        android:textColor="@color/primary_fixed" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- ── Stats Row ──────────────────────────────── -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginBottom="@dimen/spacing_5">

            <com.google.android.material.card.MaterialCardView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginEnd="@dimen/spacing_2"
                style="@style/Widget.App.Card">
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="@dimen/spacing_5">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="주문 건수"
                        android:textAppearance="@style/TextAppearance.App.Label.Small" />
                    <TextView
                        android:id="@+id/tv_order_count"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="0건"
                        android:textAppearance="@style/TextAppearance.App.Headline.Medium"
                        android:textColor="@color/primary"
                        android:layout_marginTop="@dimen/spacing_2" />
                    <ProgressBar
                        android:id="@+id/pb_daily_goal"
                        style="@style/Widget.Material3.LinearProgressIndicator"
                        android:layout_width="match_parent"
                        android:layout_height="6dp"
                        android:layout_marginTop="@dimen/spacing_3"
                        android:progress="75"
                        android:max="100"
                        android:contentDescription="일일 목표 진척률" />
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="목표 75%"
                        android:textAppearance="@style/TextAppearance.App.Label.Small"
                        android:layout_marginTop="@dimen/spacing_1" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <com.google.android.material.card.MaterialCardView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="@dimen/spacing_2"
                style="@style/Widget.App.Card">
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="@dimen/spacing_5">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="평균 주문"
                        android:textAppearance="@style/TextAppearance.App.Label.Small" />
                    <TextView
                        android:id="@+id/tv_avg_order"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="₩0"
                        android:textAppearance="@style/TextAppearance.App.Headline.Medium"
                        android:textColor="@color/primary"
                        android:layout_marginTop="@dimen/spacing_2" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
        </LinearLayout>

        <!-- ── Operation Control Card ────────────────── -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_5"
            style="@style/Widget.App.Card">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="@dimen/spacing_5">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="운영 제어"
                    android:textAppearance="@style/TextAppearance.App.Title.Medium"
                    android:layout_marginBottom="@dimen/spacing_2" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="모든 플랫폼의 매장 노출 상태를 관리하세요."
                    android:textAppearance="@style/TextAppearance.App.Body.Small"
                    android:layout_marginBottom="@dimen/spacing_5" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">

                    <Button
                        android:id="@+id/btn_open"
                        android:layout_width="0dp"
                        android:layout_height="@dimen/button_height_md"
                        android:layout_weight="1"
                        android:layout_marginEnd="@dimen/spacing_2"
                        android:text="영업중"
                        style="@style/Widget.App.Button.Secondary" />

                    <Button
                        android:id="@+id/btn_close"
                        android:layout_width="0dp"
                        android:layout_height="@dimen/button_height_md"
                        android:layout_weight="1"
                        android:layout_marginStart="@dimen/spacing_2"
                        android:text="영업 종료"
                        style="@style/Widget.App.Button.Outlined" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- ── Recent Transactions ────────────────────── -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginBottom="@dimen/spacing_4">

            <TextView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="최근 거래 기록"
                android:textAppearance="@style/TextAppearance.App.Title.Medium" />

            <TextView
                android:id="@+id/tv_view_all"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="전체 보기"
                android:textAppearance="@style/TextAppearance.App.Label.Medium"
                android:textColor="@color/primary"
                android:drawableEnd="@drawable/ic_arrow_right"
                android:drawablePadding="2dp" />
        </LinearLayout>

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rv_recent_orders"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:nestedScrollingEnabled="false"
            android:clipToPadding="false" />

    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

write('app-android-shop', 'fragment_cart.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:id="@+id/cart_header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/surface"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:id="@+id/tv_cart_title"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="장바구니"
            android:textAppearance="@style/TextAppearance.App.Headline.Small" />
    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_cart_items"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_4"
        app:layout_constraintBottom_toTopOf="@id/bottom_checkout_bar"
        app:layout_constraintTop_toBottomOf="@id/cart_header" />

    <!-- Glass-effect checkout bar -->
    <LinearLayout
        android:id="@+id/bottom_checkout_bar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_glass_card"
        android:orientation="vertical"
        android:padding="@dimen/spacing_5"
        app:layout_constraintBottom_toBottomOf="parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginBottom="@dimen/spacing_4">

            <TextView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="총 결제금액"
                android:textAppearance="@style/TextAppearance.App.Body.Large" />

            <TextView
                android:id="@+id/tv_total_price"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="0원"
                android:textAppearance="@style/TextAppearance.App.Title.Large"
                android:textColor="@color/primary" />
        </LinearLayout>

        <Button
            android:id="@+id/btn_checkout"
            android:layout_width="match_parent"
            android:layout_height="@dimen/button_height_md"
            android:text="배달 주문하기"
            style="@style/Widget.App.Button.Primary" />
    </LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-shop', 'fragment_orders.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:id="@+id/orders_header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="주문 관리"
            android:textAppearance="@style/TextAppearance.App.Headline.Small" />

        <com.google.android.material.chip.ChipGroup
            android:id="@+id/chip_group_status"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:singleSelection="true">
            <com.google.android.material.chip.Chip
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="전체"
                android:checked="true"
                style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="대기"
                style="@style/Widget.Material3.Chip.Filter" />
        </com.google.android.material.chip.ChipGroup>
    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_orders"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6"
        app:layout_constraintTop_toBottomOf="@id/orders_header"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-shop', 'item_order.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card"
    android:foreground="@drawable/ripple_surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="@dimen/spacing_5">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginBottom="@dimen/spacing_3">

            <TextView
                android:id="@+id/tv_order_number"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="#12345678"
                android:textAppearance="@style/TextAppearance.App.Title.Small"
                android:textColor="@color/primary" />

            <TextView
                android:id="@+id/tv_order_status"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="준비중"
                style="@style/Widget.App.Badge.Info" />
        </LinearLayout>

        <TextView
            android:id="@+id/tv_order_items"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="메뉴 항목"
            android:textAppearance="@style/TextAppearance.App.Body.Medium"
            android:layout_marginBottom="@dimen/spacing_2" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <TextView
                android:id="@+id/tv_order_time"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="12:00"
                android:textAppearance="@style/TextAppearance.App.Label.Small" />

            <TextView
                android:id="@+id/tv_order_price"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="₩0"
                android:textAppearance="@style/TextAppearance.App.Title.Medium"
                android:textColor="@color/on_surface" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

write('app-android-shop', 'item_product.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card"
    android:foreground="@drawable/ripple_surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="@dimen/spacing_4">

        <com.google.android.material.card.MaterialCardView
            android:layout_width="72dp"
            android:layout_height="72dp"
            android:layout_marginEnd="@dimen/spacing_4"
            app:cardCornerRadius="@dimen/radius_md"
            app:cardElevation="0dp"
            app:strokeWidth="0dp">
            <ImageView
                android:id="@+id/iv_product_image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@color/surface_container"
                android:scaleType="centerCrop"
                android:contentDescription="상품 이미지" />
        </com.google.android.material.card.MaterialCardView>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:gravity="center_vertical">

            <TextView
                android:id="@+id/tv_product_name"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="상품명"
                android:textAppearance="@style/TextAppearance.App.Title.Small"
                android:layout_marginBottom="@dimen/spacing_1" />

            <TextView
                android:id="@+id/tv_product_price"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="₩0"
                android:textAppearance="@style/TextAppearance.App.Body.Medium"
                android:textColor="@color/primary" />
        </LinearLayout>

        <com.google.android.material.switchmaterial.SwitchMaterial
            android:id="@+id/sw_available"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:contentDescription="상품 판매 가능 여부" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

write('app-android-shop', 'fragment_products.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:id="@+id/products_header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="메뉴 관리"
            android:textAppearance="@style/TextAppearance.App.Headline.Small" />

        <Button
            android:id="@+id/btn_add_product"
            android:layout_width="wrap_content"
            android:layout_height="@dimen/button_height_sm"
            android:text="+ 추가"
            style="@style/Widget.App.Button.Primary" />
    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_products"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6"
        app:layout_constraintTop_toBottomOf="@id/products_header"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-shop', 'fragment_reviews.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:id="@+id/reviews_header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="리뷰 관리"
            android:textAppearance="@style/TextAppearance.App.Headline.Small" />
    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_reviews"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6"
        app:layout_constraintTop_toBottomOf="@id/reviews_header"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-shop', 'fragment_sales.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="매출 · 정산"
            android:textAppearance="@style/TextAppearance.App.Headline.Small"
            android:layout_marginBottom="@dimen/spacing_5" />

        <!-- Period selector chips -->
        <com.google.android.material.chip.ChipGroup
            android:id="@+id/chip_period"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_5"
            app:singleSelection="true">
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="오늘" android:checked="true" style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="이번 주" style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="이번 달" style="@style/Widget.Material3.Chip.Filter" />
        </com.google.android.material.chip.ChipGroup>

        <!-- Revenue Card -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_5"
            style="@style/Widget.App.Card">
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="@dimen/spacing_5">
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="총 매출액" android:textAppearance="@style/TextAppearance.App.Label.Small" android:layout_marginBottom="@dimen/spacing_2" />
                <TextView android:id="@+id/tv_total_revenue" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="₩0" android:textAppearance="@style/TextAppearance.App.Headline.Large" android:textColor="@color/primary" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- Stats Grid -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginBottom="@dimen/spacing_5">
            <com.google.android.material.card.MaterialCardView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:layout_marginEnd="@dimen/spacing_2" style="@style/Widget.App.Card.Tonal">
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_4">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="주문 건수" android:textAppearance="@style/TextAppearance.App.Label.Small" />
                    <TextView android:id="@+id/tv_order_count_sales" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="0건" android:textAppearance="@style/TextAppearance.App.Title.Large" android:textColor="@color/secondary" android:layout_marginTop="@dimen/spacing_1" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
            <com.google.android.material.card.MaterialCardView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:layout_marginStart="@dimen/spacing_2" style="@style/Widget.App.Card.Tonal">
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_4">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="평균 주문" android:textAppearance="@style/TextAppearance.App.Label.Small" />
                    <TextView android:id="@+id/tv_avg_order_sales" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="₩0" android:textAppearance="@style/TextAppearance.App.Title.Large" android:textColor="@color/tertiary" android:layout_marginTop="@dimen/spacing_1" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
        </LinearLayout>

    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

write('app-android-shop', 'fragment_search.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface">

    <!-- Glass Search Bar -->
    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:layout_marginHorizontal="@dimen/page_horizontal_padding"
        android:layout_marginTop="@dimen/spacing_5"
        android:layout_marginBottom="@dimen/spacing_4"
        style="@style/Widget.App.Card.Glass">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center_vertical"
            android:paddingHorizontal="@dimen/spacing_5">

            <ImageView
                android:layout_width="@dimen/icon_size_sm"
                android:layout_height="@dimen/icon_size_sm"
                android:src="@drawable/ic_search"
                android:contentDescription="검색" />

            <EditText
                android:id="@+id/et_search"
                android:layout_width="0dp"
                android:layout_height="match_parent"
                android:layout_weight="1"
                android:layout_marginStart="@dimen/spacing_3"
                android:background="@null"
                android:hint="검색"
                android:textAppearance="@style/TextAppearance.App.Body.Medium"
                android:inputType="text"
                android:importantForAutofill="no" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_search_results"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding" />

</LinearLayout>`);

write('app-android-shop', 'fragment_profile.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <!-- Profile Header with gradient bg -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_5"
            style="@style/Widget.App.Card.Hero">
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@drawable/bg_gradient_primary"
                android:orientation="horizontal"
                android:gravity="center_vertical"
                android:padding="@dimen/spacing_6">
                <ImageView
                    android:id="@+id/iv_profile_avatar"
                    android:layout_width="@dimen/avatar_size_lg"
                    android:layout_height="@dimen/avatar_size_lg"
                    android:src="@drawable/ic_profile"
                    android:background="@drawable/bg_glass_card"
                    android:padding="@dimen/spacing_4"
                    android:contentDescription="프로필 아바타" />
                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:orientation="vertical"
                    android:layout_marginStart="@dimen/spacing_5">
                    <TextView
                        android:id="@+id/tv_profile_name"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="가게 이름"
                        android:textAppearance="@style/TextAppearance.App.Title.Large"
                        android:textColor="@color/on_primary" />
                    <TextView
                        android:id="@+id/tv_profile_email"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="email@example.com"
                        android:textAppearance="@style/TextAppearance.App.Body.Small"
                        android:textColor="@color/primary_fixed_dim" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <Button
            android:id="@+id/btn_edit_profile"
            android:layout_width="match_parent"
            android:layout_height="@dimen/button_height_md"
            android:text="프로필 수정"
            android:layout_marginBottom="@dimen/spacing_5"
            style="@style/Widget.App.Button.Outlined" />

        <Button
            android:id="@+id/btn_logout"
            android:layout_width="match_parent"
            android:layout_height="@dimen/button_height_md"
            android:text="로그아웃"
            style="@style/Widget.App.Button.Danger" />

    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

// Remaining shop fragments (settings, delivery, operating_hours, notification_settings, payment_settings)
write('app-android-shop', 'fragment_settings.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="설정"
            android:textAppearance="@style/TextAppearance.App.Headline.Small"
            android:layout_marginBottom="@dimen/spacing_6" />

        <!-- Settings Items -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_4"
            style="@style/Widget.App.Card">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_1">
                <LinearLayout android:id="@+id/btn_operating_hours" android:layout_width="match_parent" android:layout_height="@dimen/touch_target_min" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5" android:foreground="@drawable/ripple_surface">
                    <ImageView android:layout_width="@dimen/icon_size_md" android:layout_height="@dimen/icon_size_md" android:src="@drawable/ic_history" android:layout_marginEnd="@dimen/spacing_4" android:contentDescription="영업시간" />
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="영업시간 설정" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                    <ImageView android:layout_width="@dimen/icon_size_sm" android:layout_height="@dimen/icon_size_sm" android:src="@drawable/ic_arrow_right" android:contentDescription="이동" />
                </LinearLayout>
                <View style="@style/Widget.App.Divider" android:layout_marginHorizontal="@dimen/spacing_5" />
                <LinearLayout android:id="@+id/btn_delivery_settings" android:layout_width="match_parent" android:layout_height="@dimen/touch_target_min" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5" android:foreground="@drawable/ripple_surface">
                    <ImageView android:layout_width="@dimen/icon_size_md" android:layout_height="@dimen/icon_size_md" android:src="@drawable/ic_delivery" android:layout_marginEnd="@dimen/spacing_4" android:contentDescription="배달 설정" />
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="배달 설정" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                    <ImageView android:layout_width="@dimen/icon_size_sm" android:layout_height="@dimen/icon_size_sm" android:src="@drawable/ic_arrow_right" android:contentDescription="이동" />
                </LinearLayout>
                <View style="@style/Widget.App.Divider" android:layout_marginHorizontal="@dimen/spacing_5" />
                <LinearLayout android:id="@+id/btn_payment_settings" android:layout_width="match_parent" android:layout_height="@dimen/touch_target_min" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5" android:foreground="@drawable/ripple_surface">
                    <ImageView android:layout_width="@dimen/icon_size_md" android:layout_height="@dimen/icon_size_md" android:src="@drawable/ic_sales" android:layout_marginEnd="@dimen/spacing_4" android:contentDescription="결제 설정" />
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="결제 설정" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                    <ImageView android:layout_width="@dimen/icon_size_sm" android:layout_height="@dimen/icon_size_sm" android:src="@drawable/ic_arrow_right" android:contentDescription="이동" />
                </LinearLayout>
                <View style="@style/Widget.App.Divider" android:layout_marginHorizontal="@dimen/spacing_5" />
                <LinearLayout android:id="@+id/btn_notification_settings" android:layout_width="match_parent" android:layout_height="@dimen/touch_target_min" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5" android:foreground="@drawable/ripple_surface">
                    <ImageView android:layout_width="@dimen/icon_size_md" android:layout_height="@dimen/icon_size_md" android:src="@drawable/ic_notification" android:layout_marginEnd="@dimen/spacing_4" android:contentDescription="알림 설정" />
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="알림 설정" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                    <ImageView android:layout_width="@dimen/icon_size_sm" android:layout_height="@dimen/icon_size_sm" android:src="@drawable/ic_arrow_right" android:contentDescription="이동" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

write('app-android-shop', 'fragment_delivery.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="배달 설정"
            android:textAppearance="@style/TextAppearance.App.Headline.Small"
            android:layout_marginBottom="@dimen/spacing_5" />

        <!-- Map + Radius Card -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_5"
            style="@style/Widget.App.Card">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_5">
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="배달 반경" android:textAppearance="@style/TextAppearance.App.Label.Medium" android:textColor="@color/on_surface_variant" android:layout_marginBottom="@dimen/spacing_3" />
                <com.google.android.material.card.MaterialCardView android:layout_width="match_parent" android:layout_height="160dp" android:layout_marginBottom="@dimen/spacing_4" app:cardCornerRadius="@dimen/radius_md" app:cardBackgroundColor="@color/surface_container" app:cardElevation="0dp" app:strokeWidth="0dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_gravity="center" android:text="지도 영역" android:textAppearance="@style/TextAppearance.App.Label.Small" />
                </com.google.android.material.card.MaterialCardView>
                <com.google.android.material.textfield.TextInputLayout android:id="@+id/til_delivery_radius" android:layout_width="match_parent" android:layout_height="wrap_content" android:hint="배달 반경 (km)" style="@style/Widget.App.TextInputLayout">
                    <com.google.android.material.textfield.TextInputEditText android:id="@+id/et_delivery_radius" android:layout_width="match_parent" android:layout_height="wrap_content" android:inputType="numberDecimal" android:text="3.0" />
                </com.google.android.material.textfield.TextInputLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- Order Conditions Card -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_5"
            style="@style/Widget.App.Card">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_5">
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="주문 조건" android:textAppearance="@style/TextAppearance.App.Label.Medium" android:textColor="@color/on_surface_variant" android:layout_marginBottom="@dimen/spacing_4" />
                <com.google.android.material.textfield.TextInputLayout android:id="@+id/til_min_order" android:layout_width="match_parent" android:layout_height="wrap_content" android:hint="최소 주문금액 (원)" android:layout_marginBottom="@dimen/spacing_4" style="@style/Widget.App.TextInputLayout">
                    <com.google.android.material.textfield.TextInputEditText android:id="@+id/et_min_order" android:layout_width="match_parent" android:layout_height="wrap_content" android:inputType="number" android:text="15000" />
                </com.google.android.material.textfield.TextInputLayout>
                <com.google.android.material.textfield.TextInputLayout android:id="@+id/til_delivery_fee" android:layout_width="match_parent" android:layout_height="wrap_content" android:hint="배달비 (원)" style="@style/Widget.App.TextInputLayout">
                    <com.google.android.material.textfield.TextInputEditText android:id="@+id/et_delivery_fee" android:layout_width="match_parent" android:layout_height="wrap_content" android:inputType="number" android:text="3000" />
                </com.google.android.material.textfield.TextInputLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <Button android:id="@+id/btn_save_delivery" android:layout_width="match_parent" android:layout_height="@dimen/button_height_md" android:text="저장" style="@style/Widget.App.Button.Primary" />
    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

write('app-android-shop', 'fragment_operating_hours.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="영업시간 설정" android:textAppearance="@style/TextAppearance.App.Headline.Small" android:layout_marginBottom="@dimen/spacing_5" />

        <com.google.android.material.card.MaterialCardView android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginBottom="@dimen/spacing_5" style="@style/Widget.App.Card">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_5">
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:layout_marginBottom="@dimen/spacing_4">
                    <com.google.android.material.textfield.TextInputLayout android:id="@+id/til_open_time" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:layout_marginEnd="@dimen/spacing_2" android:hint="오픈 시간" style="@style/Widget.App.TextInputLayout">
                        <com.google.android.material.textfield.TextInputEditText android:id="@+id/et_open_time" android:layout_width="match_parent" android:layout_height="wrap_content" android:inputType="time" android:text="09:00" />
                    </com.google.android.material.textfield.TextInputLayout>
                    <com.google.android.material.textfield.TextInputLayout android:id="@+id/til_close_time" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:layout_marginStart="@dimen/spacing_2" android:hint="마감 시간" style="@style/Widget.App.TextInputLayout">
                        <com.google.android.material.textfield.TextInputEditText android:id="@+id/et_close_time" android:layout_width="match_parent" android:layout_height="wrap_content" android:inputType="time" android:text="22:00" />
                    </com.google.android.material.textfield.TextInputLayout>
                </LinearLayout>
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:gravity="center_vertical">
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="휴무일 설정" android:textAppearance="@style/TextAppearance.App.Body.Medium" />
                    <com.google.android.material.switchmaterial.SwitchMaterial android:id="@+id/sw_holiday" android:layout_width="wrap_content" android:layout_height="wrap_content" android:contentDescription="휴무일 활성화" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <Button android:id="@+id/btn_save_hours" android:layout_width="match_parent" android:layout_height="@dimen/button_height_md" android:text="저장" style="@style/Widget.App.Button.Primary" />
    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

write('app-android-shop', 'fragment_notification_settings.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="알림 설정" android:textAppearance="@style/TextAppearance.App.Headline.Small" android:layout_marginBottom="@dimen/spacing_5" />

        <com.google.android.material.card.MaterialCardView android:layout_width="match_parent" android:layout_height="wrap_content" style="@style/Widget.App.Card">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_1">
                <LinearLayout android:layout_width="match_parent" android:layout_height="@dimen/touch_target_min" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5">
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="새 주문 알림" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                    <com.google.android.material.switchmaterial.SwitchMaterial android:id="@+id/sw_new_order" android:layout_width="wrap_content" android:layout_height="wrap_content" android:checked="true" android:contentDescription="새 주문 알림" />
                </LinearLayout>
                <View style="@style/Widget.App.Divider" android:layout_marginHorizontal="@dimen/spacing_5" />
                <LinearLayout android:layout_width="match_parent" android:layout_height="@dimen/touch_target_min" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5">
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="리뷰 알림" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                    <com.google.android.material.switchmaterial.SwitchMaterial android:id="@+id/sw_review" android:layout_width="wrap_content" android:layout_height="wrap_content" android:checked="true" android:contentDescription="리뷰 알림" />
                </LinearLayout>
                <View style="@style/Widget.App.Divider" android:layout_marginHorizontal="@dimen/spacing_5" />
                <LinearLayout android:layout_width="match_parent" android:layout_height="@dimen/touch_target_min" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5">
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="마케팅 알림" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                    <com.google.android.material.switchmaterial.SwitchMaterial android:id="@+id/sw_marketing" android:layout_width="wrap_content" android:layout_height="wrap_content" android:contentDescription="마케팅 알림" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

write('app-android-shop', 'fragment_payment_settings.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="결제 설정" android:textAppearance="@style/TextAppearance.App.Headline.Small" android:layout_marginBottom="@dimen/spacing_5" />

        <com.google.android.material.card.MaterialCardView android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginBottom="@dimen/spacing_5" style="@style/Widget.App.Card">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_5">
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="정산 계좌" android:textAppearance="@style/TextAppearance.App.Label.Medium" android:textColor="@color/on_surface_variant" android:layout_marginBottom="@dimen/spacing_4" />
                <com.google.android.material.textfield.TextInputLayout android:id="@+id/til_bank" android:layout_width="match_parent" android:layout_height="wrap_content" android:hint="은행명" android:layout_marginBottom="@dimen/spacing_3" style="@style/Widget.App.TextInputLayout">
                    <com.google.android.material.textfield.TextInputEditText android:id="@+id/et_bank" android:layout_width="match_parent" android:layout_height="wrap_content" android:inputType="text" />
                </com.google.android.material.textfield.TextInputLayout>
                <com.google.android.material.textfield.TextInputLayout android:id="@+id/til_account" android:layout_width="match_parent" android:layout_height="wrap_content" android:hint="계좌번호" style="@style/Widget.App.TextInputLayout">
                    <com.google.android.material.textfield.TextInputEditText android:id="@+id/et_account" android:layout_width="match_parent" android:layout_height="wrap_content" android:inputType="number" />
                </com.google.android.material.textfield.TextInputLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <Button android:id="@+id/btn_save_payment" android:layout_width="match_parent" android:layout_height="@dimen/button_height_md" android:text="저장" style="@style/Widget.App.Button.Primary" />
    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

console.log('Shop layouts: 17 files written');

// ═══════════════════════════════════════════════════════════════════
// APP-ANDROID-USER LAYOUTS
// ═══════════════════════════════════════════════════════════════════

write('app-android-user', 'activity_main.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    android:background="@color/surface">

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_container"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottom_nav_view"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav_view"
        style="@style/Widget.App.BottomNav"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:menu="@menu/bottom_nav_menu" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-user', 'fragment_home.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface">

    <!-- Glass Search Bar -->
    <LinearLayout
        android:id="@+id/search_bar_card"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:layout_marginHorizontal="@dimen/page_horizontal_padding"
        android:layout_marginTop="@dimen/spacing_6"
        android:layout_marginBottom="@dimen/spacing_4"
        android:background="@drawable/bg_glass_search"
        android:gravity="center_vertical"
        android:paddingHorizontal="@dimen/spacing_5">

        <ImageView
            android:layout_width="@dimen/icon_size_sm"
            android:layout_height="@dimen/icon_size_sm"
            android:src="@drawable/ic_search"
            android:contentDescription="검색" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="@dimen/spacing_3"
            android:text="먹고 싶은 메뉴, 가게 검색"
            android:textAppearance="@style/TextAppearance.App.Body.Medium"
            android:textColor="@color/on_surface_variant" />
    </LinearLayout>

    <!-- Category Chips -->
    <HorizontalScrollView
        android:id="@+id/category_scroll"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:scrollbars="none"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:layout_marginBottom="@dimen/spacing_5">

        <com.google.android.material.chip.ChipGroup
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:singleSelection="true">
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="전체" style="@style/Widget.Material3.Chip.Filter" android:checked="true" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="1인분" style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="치킨" style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="피자/양식" style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="한식" style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="중식" style="@style/Widget.Material3.Chip.Filter" />
        </com.google.android.material.chip.ChipGroup>
    </HorizontalScrollView>

    <TextView
        android:id="@+id/tv_recommendation_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="@dimen/page_horizontal_padding"
        android:layout_marginBottom="@dimen/spacing_4"
        android:text="우리 동네 맛집 추천"
        android:textAppearance="@style/TextAppearance.App.Title.Medium" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_stores"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingBottom="@dimen/spacing_6" />

</LinearLayout>`);

write('app-android-user', 'item_store.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_4"
    android:layout_marginHorizontal="@dimen/page_horizontal_padding"
    style="@style/Widget.App.Card"
    android:foreground="@drawable/ripple_surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <ImageView
            android:id="@+id/iv_store_image"
            android:layout_width="match_parent"
            android:layout_height="@dimen/store_image_height"
            android:background="@color/surface_container"
            android:scaleType="centerCrop"
            android:contentDescription="가게 이미지" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="@dimen/spacing_4">

            <TextView
                android:id="@+id/tv_store_name"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="가게 이름"
                android:textAppearance="@style/TextAppearance.App.Title.Medium"
                android:layout_marginBottom="@dimen/spacing_1" />

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:gravity="center_vertical">

                <ImageView
                    android:layout_width="14dp"
                    android:layout_height="14dp"
                    android:src="@drawable/ic_reviews"
                    android:layout_marginEnd="@dimen/spacing_1"
                    android:contentDescription="별점" />

                <TextView
                    android:id="@+id/tv_store_info"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="0.0 · 리뷰 0"
                    android:textAppearance="@style/TextAppearance.App.Label.Small" />
            </LinearLayout>
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

write('app-android-user', 'fragment_store_detail.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/surface"
        app:elevation="0dp">
        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar_detail"
            android:layout_width="match_parent"
            android:layout_height="@dimen/top_bar_height"
            app:navigationIcon="@drawable/ic_arrow_back"
            app:titleTextAppearance="@style/TextAppearance.App.Title.Large"
            app:titleTextColor="@color/on_surface" />
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:paddingBottom="@dimen/spacing_8">

            <ImageView
                android:id="@+id/iv_store_banner"
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:background="@color/surface_container"
                android:scaleType="centerCrop"
                android:contentDescription="가게 배너" />

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:paddingHorizontal="@dimen/page_horizontal_padding"
                android:paddingTop="@dimen/spacing_5">

                <TextView
                    android:id="@+id/tv_store_name_detail"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:textAppearance="@style/TextAppearance.App.Headline.Medium"
                    android:layout_marginBottom="@dimen/spacing_2" />

                <TextView
                    android:id="@+id/tv_store_info_detail"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:textAppearance="@style/TextAppearance.App.Body.Medium"
                    android:textColor="@color/on_surface_variant"
                    android:layout_marginBottom="@dimen/spacing_6" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="메뉴"
                    android:textAppearance="@style/TextAppearance.App.Title.Medium"
                    android:layout_marginBottom="@dimen/spacing_3" />
            </LinearLayout>

            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/rv_menus"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:nestedScrollingEnabled="false"
                android:clipToPadding="false"
                android:paddingHorizontal="@dimen/page_horizontal_padding" />
        </LinearLayout>
    </androidx.core.widget.NestedScrollView>

</androidx.coordinatorlayout.widget.CoordinatorLayout>`);

write('app-android-user', 'item_menu.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card"
    android:foreground="@drawable/ripple_surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="@dimen/spacing_4">

        <com.google.android.material.card.MaterialCardView
            android:layout_width="80dp"
            android:layout_height="80dp"
            android:layout_marginEnd="@dimen/spacing_4"
            app:cardCornerRadius="@dimen/radius_md"
            app:cardElevation="0dp" app:strokeWidth="0dp">
            <ImageView android:id="@+id/iv_menu_image" android:layout_width="match_parent" android:layout_height="match_parent" android:background="@color/surface_container" android:scaleType="centerCrop" android:contentDescription="메뉴 이미지" />
        </com.google.android.material.card.MaterialCardView>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:gravity="center_vertical">
            <TextView android:id="@+id/tv_menu_name" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Title.Small" android:layout_marginBottom="@dimen/spacing_1" />
            <TextView android:id="@+id/tv_menu_description" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Body.Small" android:maxLines="2" android:ellipsize="end" android:layout_marginBottom="@dimen/spacing_2" />
            <TextView android:id="@+id/tv_menu_price" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Title.Small" android:textColor="@color/primary" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

write('app-android-user', 'fragment_cart.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <TextView
        android:id="@+id/tv_cart_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        android:text="장바구니"
        android:textAppearance="@style/TextAppearance.App.Headline.Small"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_cart_items"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        app:layout_constraintTop_toBottomOf="@id/tv_cart_title"
        app:layout_constraintBottom_toTopOf="@id/bottom_checkout_bar" />

    <LinearLayout
        android:id="@+id/bottom_checkout_bar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_glass_card"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        app:layout_constraintBottom_toBottomOf="parent">

        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:layout_marginBottom="@dimen/spacing_4">
            <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="총 결제금액" android:textAppearance="@style/TextAppearance.App.Body.Large" />
            <TextView android:id="@+id/tv_total_price" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="0원" android:textAppearance="@style/TextAppearance.App.Title.Large" android:textColor="@color/primary" />
        </LinearLayout>

        <Button android:id="@+id/btn_checkout" android:layout_width="match_parent" android:layout_height="@dimen/button_height_md" android:text="주문하기" style="@style/Widget.App.Button.Primary" />
    </LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-user', 'item_cart.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card">

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:padding="@dimen/spacing_4" android:gravity="center_vertical">
        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:orientation="vertical">
            <TextView android:id="@+id/tv_cart_item_name" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Title.Small" android:layout_marginBottom="@dimen/spacing_1" />
            <TextView android:id="@+id/tv_cart_item_price" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Body.Medium" android:textColor="@color/primary" />
        </LinearLayout>
        <LinearLayout android:layout_width="wrap_content" android:layout_height="wrap_content" android:orientation="horizontal" android:gravity="center_vertical" android:background="@drawable/bg_glass_card" android:paddingHorizontal="@dimen/spacing_2" android:paddingVertical="@dimen/spacing_1">
            <Button android:id="@+id/btn_decrease" android:layout_width="32dp" android:layout_height="32dp" android:text="-" android:contentDescription="수량 감소" style="@style/Widget.Material3.Button.IconButton" />
            <TextView android:id="@+id/tv_quantity" android:layout_width="32dp" android:layout_height="wrap_content" android:gravity="center" android:text="1" android:textAppearance="@style/TextAppearance.App.Title.Small" />
            <Button android:id="@+id/btn_increase" android:layout_width="32dp" android:layout_height="32dp" android:text="+" android:contentDescription="수량 증가" style="@style/Widget.Material3.Button.IconButton" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

write('app-android-user', 'item_order_user.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card"
    android:foreground="@drawable/ripple_surface">

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_5">
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:gravity="center_vertical" android:layout_marginBottom="@dimen/spacing_2">
            <TextView android:id="@+id/tv_order_store" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:textAppearance="@style/TextAppearance.App.Title.Small" />
            <TextView android:id="@+id/tv_order_status" android:layout_width="wrap_content" android:layout_height="wrap_content" style="@style/Widget.App.Badge.Info" />
        </LinearLayout>
        <TextView android:id="@+id/tv_order_items" android:layout_width="match_parent" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Body.Medium" android:layout_marginBottom="@dimen/spacing_2" />
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:gravity="center_vertical">
            <TextView android:id="@+id/tv_order_date" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:textAppearance="@style/TextAppearance.App.Label.Small" />
            <TextView android:id="@+id/tv_order_price" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Title.Small" android:textColor="@color/primary" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

write('app-android-user', 'fragment_search.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:layout_marginHorizontal="@dimen/page_horizontal_padding"
        android:layout_marginTop="@dimen/spacing_5"
        android:layout_marginBottom="@dimen/spacing_4"
        android:background="@drawable/bg_glass_search"
        android:gravity="center_vertical"
        android:paddingHorizontal="@dimen/spacing_5">
        <ImageView android:layout_width="@dimen/icon_size_sm" android:layout_height="@dimen/icon_size_sm" android:src="@drawable/ic_search" android:contentDescription="검색" />
        <EditText android:id="@+id/et_search" android:layout_width="0dp" android:layout_height="match_parent" android:layout_weight="1" android:layout_marginStart="@dimen/spacing_3" android:background="@null" android:hint="메뉴, 가게 검색" android:textAppearance="@style/TextAppearance.App.Body.Medium" android:inputType="text" android:importantForAutofill="no" />
    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_search_results"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingBottom="@dimen/spacing_6" />
</LinearLayout>`);

write('app-android-user', 'fragment_profile.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_5"
            style="@style/Widget.App.Card.Hero">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:background="@drawable/bg_gradient_primary" android:orientation="horizontal" android:gravity="center_vertical" android:padding="@dimen/spacing_6">
                <ImageView android:id="@+id/iv_avatar" android:layout_width="@dimen/avatar_size_lg" android:layout_height="@dimen/avatar_size_lg" android:src="@drawable/ic_profile" android:background="@drawable/bg_glass_card" android:padding="@dimen/spacing_4" android:contentDescription="프로필" />
                <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:orientation="vertical" android:layout_marginStart="@dimen/spacing_5">
                    <TextView android:id="@+id/tv_user_name" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="사용자 이름" android:textAppearance="@style/TextAppearance.App.Title.Large" android:textColor="@color/on_primary" />
                    <TextView android:id="@+id/tv_user_email" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="email@example.com" android:textAppearance="@style/TextAppearance.App.Body.Small" android:textColor="@color/primary_fixed_dim" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <com.google.android.material.card.MaterialCardView android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginBottom="@dimen/spacing_4" style="@style/Widget.App.Card">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_1">
                <LinearLayout android:id="@+id/btn_order_history" android:layout_width="match_parent" android:layout_height="@dimen/touch_target_min" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5" android:foreground="@drawable/ripple_surface">
                    <ImageView android:layout_width="@dimen/icon_size_md" android:layout_height="@dimen/icon_size_md" android:src="@drawable/ic_orders" android:layout_marginEnd="@dimen/spacing_4" android:contentDescription="주문 내역" />
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="주문 내역" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                    <ImageView android:layout_width="@dimen/icon_size_sm" android:layout_height="@dimen/icon_size_sm" android:src="@drawable/ic_arrow_right" android:contentDescription="이동" />
                </LinearLayout>
                <View style="@style/Widget.App.Divider" android:layout_marginHorizontal="@dimen/spacing_5" />
                <LinearLayout android:id="@+id/btn_settings" android:layout_width="match_parent" android:layout_height="@dimen/touch_target_min" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5" android:foreground="@drawable/ripple_surface">
                    <ImageView android:layout_width="@dimen/icon_size_md" android:layout_height="@dimen/icon_size_md" android:src="@drawable/ic_settings" android:layout_marginEnd="@dimen/spacing_4" android:contentDescription="설정" />
                    <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="설정" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                    <ImageView android:layout_width="@dimen/icon_size_sm" android:layout_height="@dimen/icon_size_sm" android:src="@drawable/ic_arrow_right" android:contentDescription="이동" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <Button android:id="@+id/btn_logout" android:layout_width="match_parent" android:layout_height="@dimen/button_height_md" android:text="로그아웃" style="@style/Widget.App.Button.Danger" />
    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

console.log('User layouts: 10 files written');

// ═══════════════════════════════════════════════════════════════════
// APP-ANDROID-DELIVERY LAYOUTS
// ═══════════════════════════════════════════════════════════════════

write('app-android-delivery', 'activity_main.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    android:background="@color/surface">

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_container"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottom_nav_view"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav_view"
        style="@style/Widget.App.BottomNav"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:menu="@menu/bottom_nav_menu" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-delivery', 'fragment_home.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_8">

        <!-- Hero Status Card -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="@dimen/spacing_5"
            style="@style/Widget.App.Card.Hero">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:background="@drawable/bg_gradient_primary" android:orientation="vertical" android:padding="@dimen/spacing_6">
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="배달 현황" android:textAppearance="@style/TextAppearance.App.Label.Medium" android:textColor="@color/primary_fixed_dim" android:layout_marginBottom="@dimen/spacing_2" />
                <TextView android:id="@+id/tv_delivery_status" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="대기중" android:textAppearance="@style/TextAppearance.App.Headline.Large" android:textColor="@color/on_primary" />
                <TextView android:id="@+id/tv_today_deliveries" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="오늘 0건 완료" android:textAppearance="@style/TextAppearance.App.Body.Small" android:textColor="@color/primary_fixed" android:layout_marginTop="@dimen/spacing_2" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- Stats Row -->
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:layout_marginBottom="@dimen/spacing_5">
            <com.google.android.material.card.MaterialCardView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:layout_marginEnd="@dimen/spacing_2" style="@style/Widget.App.Card.Tonal">
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_4">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="오늘 수입" android:textAppearance="@style/TextAppearance.App.Label.Small" />
                    <TextView android:id="@+id/tv_today_earnings" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="₩0" android:textAppearance="@style/TextAppearance.App.Title.Large" android:textColor="@color/secondary" android:layout_marginTop="@dimen/spacing_1" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
            <com.google.android.material.card.MaterialCardView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:layout_marginStart="@dimen/spacing_2" style="@style/Widget.App.Card.Tonal">
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_4">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="평균 배달" android:textAppearance="@style/TextAppearance.App.Label.Small" />
                    <TextView android:id="@+id/tv_avg_time" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="0분" android:textAppearance="@style/TextAppearance.App.Title.Large" android:textColor="@color/tertiary" android:layout_marginTop="@dimen/spacing_1" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
        </LinearLayout>

        <Button android:id="@+id/btn_start_delivery" android:layout_width="match_parent" android:layout_height="@dimen/button_height_lg" android:text="배달 시작하기" style="@style/Widget.App.Button.Accent" />

    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

write('app-android-delivery', 'fragment_active_delivery.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <TextView
        android:id="@+id/tv_active_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        android:text="진행중 배달"
        android:textAppearance="@style/TextAppearance.App.Headline.Small"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_active_deliveries"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6"
        app:layout_constraintTop_toBottomOf="@id/tv_active_title"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-delivery', 'item_delivery.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card"
    android:foreground="@drawable/ripple_surface">

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_5">
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:gravity="center_vertical" android:layout_marginBottom="@dimen/spacing_3">
            <TextView android:id="@+id/tv_delivery_id" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:textAppearance="@style/TextAppearance.App.Title.Small" android:textColor="@color/primary" />
            <TextView android:id="@+id/tv_delivery_status" android:layout_width="wrap_content" android:layout_height="wrap_content" style="@style/Widget.App.Badge.Info" />
        </LinearLayout>
        <TextView android:id="@+id/tv_pickup_address" android:layout_width="match_parent" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Body.Medium" android:drawableStart="@drawable/ic_store" android:drawablePadding="@dimen/spacing_2" android:layout_marginBottom="@dimen/spacing_1" />
        <TextView android:id="@+id/tv_delivery_address" android:layout_width="match_parent" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Body.Medium" android:drawableStart="@drawable/ic_active_delivery" android:drawablePadding="@dimen/spacing_2" android:layout_marginBottom="@dimen/spacing_3" />
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal">
            <Button android:id="@+id/btn_pickup" android:layout_width="0dp" android:layout_height="@dimen/button_height_sm" android:layout_weight="1" android:layout_marginEnd="@dimen/spacing_2" android:text="픽업 완료" style="@style/Widget.App.Button.Secondary" />
            <Button android:id="@+id/btn_complete" android:layout_width="0dp" android:layout_height="@dimen/button_height_sm" android:layout_weight="1" android:layout_marginStart="@dimen/spacing_2" android:text="배달 완료" style="@style/Widget.App.Button.Primary" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

write('app-android-delivery', 'fragment_history.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <TextView
        android:id="@+id/tv_history_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        android:text="배달 내역"
        android:textAppearance="@style/TextAppearance.App.Headline.Small"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_history"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6"
        app:layout_constraintTop_toBottomOf="@id/tv_history_title"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-delivery', 'item_history.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card">

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:padding="@dimen/spacing_5" android:gravity="center_vertical">
        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:orientation="vertical">
            <TextView android:id="@+id/tv_history_store" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Title.Small" android:layout_marginBottom="@dimen/spacing_1" />
            <TextView android:id="@+id/tv_history_date" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Label.Small" />
        </LinearLayout>
        <TextView android:id="@+id/tv_history_amount" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Title.Medium" android:textColor="@color/secondary" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

write('app-android-delivery', 'fragment_profile.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:paddingHorizontal="@dimen/page_horizontal_padding" android:paddingTop="@dimen/spacing_6" android:paddingBottom="@dimen/spacing_8">

        <com.google.android.material.card.MaterialCardView android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginBottom="@dimen/spacing_5" style="@style/Widget.App.Card.Hero">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:background="@drawable/bg_gradient_primary" android:orientation="horizontal" android:gravity="center_vertical" android:padding="@dimen/spacing_6">
                <ImageView android:id="@+id/iv_avatar" android:layout_width="@dimen/avatar_size_lg" android:layout_height="@dimen/avatar_size_lg" android:src="@drawable/ic_profile" android:background="@drawable/bg_glass_card" android:padding="@dimen/spacing_4" android:contentDescription="프로필" />
                <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:orientation="vertical" android:layout_marginStart="@dimen/spacing_5">
                    <TextView android:id="@+id/tv_rider_name" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="배달원 이름" android:textAppearance="@style/TextAppearance.App.Title.Large" android:textColor="@color/on_primary" />
                    <TextView android:id="@+id/tv_rider_email" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="email@example.com" android:textAppearance="@style/TextAppearance.App.Body.Small" android:textColor="@color/primary_fixed_dim" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <Button android:id="@+id/btn_edit_profile" android:layout_width="match_parent" android:layout_height="@dimen/button_height_md" android:text="프로필 수정" android:layout_marginBottom="@dimen/spacing_5" style="@style/Widget.App.Button.Outlined" />
        <Button android:id="@+id/btn_logout" android:layout_width="match_parent" android:layout_height="@dimen/button_height_md" android:text="로그아웃" style="@style/Widget.App.Button.Danger" />
    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

// Delivery: fragment_cart.xml and fragment_search.xml (reuse patterns)
write('app-android-delivery', 'fragment_cart.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <TextView android:id="@+id/tv_cart_title" android:layout_width="match_parent" android:layout_height="wrap_content" android:paddingHorizontal="@dimen/page_horizontal_padding" android:paddingVertical="@dimen/spacing_5" android:text="장바구니" android:textAppearance="@style/TextAppearance.App.Headline.Small" app:layout_constraintTop_toTopOf="parent" />

    <androidx.recyclerview.widget.RecyclerView android:id="@+id/rv_cart_items" android:layout_width="match_parent" android:layout_height="0dp" android:clipToPadding="false" android:paddingHorizontal="@dimen/page_horizontal_padding" app:layout_constraintTop_toBottomOf="@id/tv_cart_title" app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

write('app-android-delivery', 'fragment_search.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface">
    <LinearLayout android:layout_width="match_parent" android:layout_height="48dp" android:layout_marginHorizontal="@dimen/page_horizontal_padding" android:layout_marginTop="@dimen/spacing_5" android:layout_marginBottom="@dimen/spacing_4" android:background="@drawable/bg_glass_search" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5">
        <ImageView android:layout_width="@dimen/icon_size_sm" android:layout_height="@dimen/icon_size_sm" android:src="@drawable/ic_search" android:contentDescription="검색" />
        <EditText android:id="@+id/et_search" android:layout_width="0dp" android:layout_height="match_parent" android:layout_weight="1" android:layout_marginStart="@dimen/spacing_3" android:background="@null" android:hint="검색" android:textAppearance="@style/TextAppearance.App.Body.Medium" android:inputType="text" android:importantForAutofill="no" />
    </LinearLayout>
    <androidx.recyclerview.widget.RecyclerView android:id="@+id/rv_search_results" android:layout_width="match_parent" android:layout_height="0dp" android:layout_weight="1" android:clipToPadding="false" android:paddingHorizontal="@dimen/page_horizontal_padding" />
</LinearLayout>`);

console.log('Delivery layouts: 9 files written');

// ═══════════════════════════════════════════════════════════════════
// APP-ANDROID-KIOSK LAYOUTS
// ═══════════════════════════════════════════════════════════════════

write('app-android-kiosk', 'activity_main.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal"
    android:background="@color/surface">

    <!-- Sidebar Navigation -->
    <com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="@dimen/kiosk_sidebar_width"
        android:layout_height="match_parent"
        android:background="@color/surface_container_lowest"
        app:itemTextColor="@color/on_surface"
        app:itemIconTint="@color/on_surface_variant"
        app:itemBackground="@drawable/nav_item_background"
        app:headerLayout="@layout/nav_header"
        app:menu="@menu/kiosk_nav_menu"
        app:elevation="@dimen/elevation_sm" />

    <!-- Main Content -->
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_container"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1" />

    <!-- Cart Panel -->
    <LinearLayout
        android:id="@+id/cart_panel"
        android:layout_width="@dimen/kiosk_cart_panel_width"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:background="@color/surface_container_lowest"
        android:elevation="@dimen/elevation_sm">

        <TextView android:layout_width="match_parent" android:layout_height="wrap_content" android:text="주문 내역" android:textAppearance="@style/TextAppearance.App.Title.Large" android:padding="@dimen/spacing_5" />

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rv_cart_kiosk"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:clipToPadding="false"
            android:paddingHorizontal="@dimen/spacing_4" />

        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:background="@drawable/bg_glass_card" android:padding="@dimen/spacing_5">
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:layout_marginBottom="@dimen/spacing_3">
                <TextView android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="총 금액" android:textAppearance="@style/TextAppearance.App.Body.Large" />
                <TextView android:id="@+id/tv_total_kiosk" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="₩0" android:textAppearance="@style/TextAppearance.App.Headline.Medium" android:textColor="@color/primary" />
            </LinearLayout>
            <Button android:id="@+id/btn_order_kiosk" android:layout_width="match_parent" android:layout_height="@dimen/button_height_lg" android:text="주문하기" style="@style/Widget.App.Button.Primary" />
        </LinearLayout>
    </LinearLayout>
</LinearLayout>`);

write('app-android-kiosk', 'nav_header.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:background="@drawable/bg_gradient_primary"
    android:padding="@dimen/spacing_5">
    <ImageView android:layout_width="@dimen/avatar_size" android:layout_height="@dimen/avatar_size" android:src="@drawable/logo" android:contentDescription="키오스크 로고" android:layout_marginBottom="@dimen/spacing_3" />
    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="키오스크" android:textAppearance="@style/TextAppearance.App.Title.Medium" android:textColor="@color/on_primary" />
</LinearLayout>`);

write('app-android-kiosk', 'fragment_menu_order.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface">

    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="메뉴 선택" android:textAppearance="@style/TextAppearance.App.Headline.Medium" android:paddingHorizontal="@dimen/page_horizontal_padding" android:paddingTop="@dimen/spacing_6" android:paddingBottom="@dimen/spacing_4" />

    <HorizontalScrollView android:layout_width="match_parent" android:layout_height="wrap_content" android:scrollbars="none" android:clipToPadding="false" android:paddingHorizontal="@dimen/page_horizontal_padding" android:layout_marginBottom="@dimen/spacing_4">
        <com.google.android.material.chip.ChipGroup android:id="@+id/chip_categories" android:layout_width="wrap_content" android:layout_height="wrap_content" app:singleSelection="true">
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="전체" android:checked="true" style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="인기" style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="세트" style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="음료" style="@style/Widget.Material3.Chip.Filter" />
        </com.google.android.material.chip.ChipGroup>
    </HorizontalScrollView>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_menu_kiosk"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6" />

</LinearLayout>`);

write('app-android-kiosk', 'item_menu_kiosk.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card"
    android:foreground="@drawable/ripple_surface">

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:padding="@dimen/spacing_5">
        <com.google.android.material.card.MaterialCardView android:layout_width="96dp" android:layout_height="96dp" android:layout_marginEnd="@dimen/spacing_5" app:cardCornerRadius="@dimen/radius_md" app:cardElevation="0dp" app:strokeWidth="0dp">
            <ImageView android:id="@+id/iv_menu_image" android:layout_width="match_parent" android:layout_height="match_parent" android:background="@color/surface_container" android:scaleType="centerCrop" android:contentDescription="메뉴 이미지" />
        </com.google.android.material.card.MaterialCardView>
        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:orientation="vertical">
            <TextView android:id="@+id/tv_menu_name" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Title.Medium" android:layout_marginBottom="@dimen/spacing_1" />
            <TextView android:id="@+id/tv_menu_desc" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Body.Small" android:maxLines="2" android:ellipsize="end" android:layout_marginBottom="@dimen/spacing_3" />
            <TextView android:id="@+id/tv_menu_price" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Title.Large" android:textColor="@color/primary" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

write('app-android-kiosk', 'item_cart_kiosk.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="center_vertical"
    android:padding="@dimen/spacing_3"
    android:minHeight="@dimen/touch_target_kiosk">

    <TextView android:id="@+id/tv_item_name" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:textAppearance="@style/TextAppearance.App.Body.Medium" />
    <TextView android:id="@+id/tv_item_qty" android:layout_width="32dp" android:layout_height="wrap_content" android:gravity="center" android:textAppearance="@style/TextAppearance.App.Label.Large" android:textColor="@color/primary" />
    <TextView android:id="@+id/tv_item_price" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Title.Small" android:layout_marginStart="@dimen/spacing_3" />
</LinearLayout>`);

write('app-android-kiosk', 'fragment_order_status.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface"
    android:gravity="center">

    <com.google.android.material.card.MaterialCardView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        style="@style/Widget.App.Card.Hero"
        android:layout_marginBottom="@dimen/spacing_5">
        <LinearLayout android:layout_width="wrap_content" android:layout_height="wrap_content" android:background="@drawable/bg_gradient_primary" android:orientation="vertical" android:padding="@dimen/spacing_8" android:gravity="center">
            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="주문 번호" android:textAppearance="@style/TextAppearance.App.Label.Medium" android:textColor="@color/primary_fixed_dim" android:layout_marginBottom="@dimen/spacing_2" />
            <TextView android:id="@+id/tv_order_number" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textAppearance="@style/TextAppearance.App.Display.Large" android:textColor="@color/on_primary" android:layout_marginBottom="@dimen/spacing_4" />
            <TextView android:id="@+id/tv_order_status_text" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="준비중" android:textAppearance="@style/TextAppearance.App.Title.Large" android:textColor="@color/primary_fixed" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>
</LinearLayout>`);

// Kiosk remaining: fragment_home, fragment_cart, fragment_search, fragment_profile
write('app-android-kiosk', 'fragment_home.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface"
    android:gravity="center"
    android:padding="@dimen/spacing_8">

    <ImageView android:layout_width="120dp" android:layout_height="120dp" android:src="@drawable/logo" android:contentDescription="로고" android:layout_marginBottom="@dimen/spacing_6" />
    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="주문을 시작하세요" android:textAppearance="@style/TextAppearance.App.Headline.Large" android:layout_marginBottom="@dimen/spacing_3" />
    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="사이드 메뉴에서 주문을 시작할 수 있습니다" android:textAppearance="@style/TextAppearance.App.Body.Large" android:textColor="@color/on_surface_variant" />
</LinearLayout>`);

write('app-android-kiosk', 'fragment_cart.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface"
    android:padding="@dimen/spacing_5">
    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="장바구니" android:textAppearance="@style/TextAppearance.App.Headline.Small" android:layout_marginBottom="@dimen/spacing_4" />
    <androidx.recyclerview.widget.RecyclerView android:id="@+id/rv_cart_items" android:layout_width="match_parent" android:layout_height="0dp" android:layout_weight="1" />
</LinearLayout>`);

write('app-android-kiosk', 'fragment_search.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface">
    <LinearLayout android:layout_width="match_parent" android:layout_height="56dp" android:layout_marginHorizontal="@dimen/page_horizontal_padding" android:layout_marginTop="@dimen/spacing_5" android:layout_marginBottom="@dimen/spacing_4" android:background="@drawable/bg_glass_search" android:gravity="center_vertical" android:paddingHorizontal="@dimen/spacing_5">
        <ImageView android:layout_width="@dimen/icon_size_md" android:layout_height="@dimen/icon_size_md" android:src="@drawable/ic_search" android:contentDescription="검색" />
        <EditText android:id="@+id/et_search" android:layout_width="0dp" android:layout_height="match_parent" android:layout_weight="1" android:layout_marginStart="@dimen/spacing_3" android:background="@null" android:hint="메뉴 검색" android:textAppearance="@style/TextAppearance.App.Body.Large" android:inputType="text" android:importantForAutofill="no" />
    </LinearLayout>
    <androidx.recyclerview.widget.RecyclerView android:id="@+id/rv_search_results" android:layout_width="match_parent" android:layout_height="0dp" android:layout_weight="1" android:clipToPadding="false" android:paddingHorizontal="@dimen/page_horizontal_padding" />
</LinearLayout>`);

write('app-android-kiosk', 'fragment_profile.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface"
    android:padding="@dimen/spacing_5">
    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="관리자 설정" android:textAppearance="@style/TextAppearance.App.Headline.Small" android:layout_marginBottom="@dimen/spacing_5" />
    <com.google.android.material.card.MaterialCardView android:layout_width="match_parent" android:layout_height="wrap_content" style="@style/Widget.App.Card">
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical" android:padding="@dimen/spacing_5">
            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="키오스크 모드 설정" android:textAppearance="@style/TextAppearance.App.Title.Medium" android:layout_marginBottom="@dimen/spacing_3" />
            <Button android:id="@+id/btn_admin_login" android:layout_width="match_parent" android:layout_height="@dimen/button_height_md" android:text="관리자 로그인" style="@style/Widget.App.Button.Primary" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>
</LinearLayout>`);

console.log('Kiosk layouts: 10 files written');
console.log('=== ALL 46 LAYOUTS DONE ===');
