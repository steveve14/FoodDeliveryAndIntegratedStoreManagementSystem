const fs = require('fs');
const path = require('path');
const base = __dirname;

function resLayout(app) {
    return path.join(base, app, 'app', 'src', 'main', 'res', 'layout');
}
function write(app, file, content) {
    fs.writeFileSync(path.join(resLayout(app), file), content, 'utf8');
    console.log(`  ${app}/${file}`);
}

// ═══════════════════════════════════════════════════════════════════
// SHOP APP FIXES
// ═══════════════════════════════════════════════════════════════════
console.log('[SHOP] Fixing layouts...');

// item_order.xml - needs: tv_order_id, tv_order_status, tv_order_time, tv_order_items, tv_order_total,
//                         layout_action_buttons, btn_accept, btn_ship, btn_cancel
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

        <!-- Header: ID + Status -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginBottom="@dimen/spacing_3">

            <TextView
                android:id="@+id/tv_order_id"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textAppearance="@style/TextAppearance.App.Title.Small"
                android:textColor="@color/primary" />

            <TextView
                android:id="@+id/tv_order_status"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                style="@style/Widget.App.Badge.Info" />
        </LinearLayout>

        <!-- Items -->
        <TextView
            android:id="@+id/tv_order_items"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Body.Medium"
            android:layout_marginBottom="@dimen/spacing_2" />

        <!-- Time + Total -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginBottom="@dimen/spacing_4">

            <TextView
                android:id="@+id/tv_order_time"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textAppearance="@style/TextAppearance.App.Label.Small" />

            <TextView
                android:id="@+id/tv_order_total"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.App.Title.Medium"
                android:textColor="@color/on_surface" />
        </LinearLayout>

        <!-- Action Buttons -->
        <LinearLayout
            android:id="@+id/layout_action_buttons"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:visibility="gone">

            <Button
                android:id="@+id/btn_accept"
                android:layout_width="0dp"
                android:layout_height="@dimen/button_height_sm"
                android:layout_weight="1"
                android:layout_marginEnd="@dimen/spacing_2"
                android:text="수락"
                style="@style/Widget.App.Button.Primary" />

            <Button
                android:id="@+id/btn_ship"
                android:layout_width="0dp"
                android:layout_height="@dimen/button_height_sm"
                android:layout_weight="1"
                android:layout_marginEnd="@dimen/spacing_2"
                android:text="배달"
                style="@style/Widget.App.Button.Secondary" />

            <Button
                android:id="@+id/btn_cancel"
                android:layout_width="0dp"
                android:layout_height="@dimen/button_height_sm"
                android:layout_weight="1"
                android:text="취소"
                style="@style/Widget.App.Button.Outlined" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

// fragment_orders.xml - needs: rv_orders, tv_empty_orders
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
    </LinearLayout>

    <TextView
        android:id="@+id/tv_empty_orders"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="주문 내역이 없습니다"
        android:textAppearance="@style/TextAppearance.App.Body.Medium"
        android:textColor="@color/on_surface_variant"
        android:visibility="gone"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

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

// item_product.xml - needs: tv_product_name, tv_product_price, tv_product_status, btn_edit, btn_delete
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
        android:orientation="vertical"
        android:padding="@dimen/spacing_4">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginBottom="@dimen/spacing_3">

            <TextView
                android:id="@+id/tv_product_name"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textAppearance="@style/TextAppearance.App.Title.Small" />

            <TextView
                android:id="@+id/tv_product_status"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                style="@style/Widget.App.Badge.Success" />
        </LinearLayout>

        <TextView
            android:id="@+id/tv_product_price"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Body.Medium"
            android:textColor="@color/primary"
            android:layout_marginBottom="@dimen/spacing_3" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="end">

            <Button
                android:id="@+id/btn_edit"
                android:layout_width="wrap_content"
                android:layout_height="@dimen/button_height_sm"
                android:layout_marginEnd="@dimen/spacing_2"
                android:text="수정"
                style="@style/Widget.App.Button.Outlined" />

            <Button
                android:id="@+id/btn_delete"
                android:layout_width="wrap_content"
                android:layout_height="@dimen/button_height_sm"
                android:text="삭제"
                style="@style/Widget.App.Button.Danger" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

// fragment_products.xml - needs: rv_products, tv_empty_products, btn_add_menu
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
            android:id="@+id/btn_add_menu"
            android:layout_width="wrap_content"
            android:layout_height="@dimen/button_height_sm"
            android:text="+ 추가"
            style="@style/Widget.App.Button.Primary" />
    </LinearLayout>

    <TextView
        android:id="@+id/tv_empty_products"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="등록된 메뉴가 없습니다"
        android:textAppearance="@style/TextAppearance.App.Body.Medium"
        android:textColor="@color/on_surface_variant"
        android:visibility="gone"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

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

// fragment_settings.xml - needs: layout_login, layout_profile_info, et_email, et_password,
//                                btn_login, btn_logout, tv_greeting, tv_store_name
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

        <!-- ── Login Section ──────────────────────────── -->
        <LinearLayout
            android:id="@+id/layout_login"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="visible">

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
                        android:text="매장 로그인"
                        android:textAppearance="@style/TextAppearance.App.Title.Medium"
                        android:layout_marginBottom="@dimen/spacing_4" />

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="이메일"
                        android:layout_marginBottom="@dimen/spacing_3"
                        style="@style/Widget.App.TextInputLayout">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/et_email"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textEmailAddress"
                            android:importantForAutofill="no" />
                    </com.google.android.material.textfield.TextInputLayout>

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="비밀번호"
                        android:layout_marginBottom="@dimen/spacing_5"
                        style="@style/Widget.App.TextInputLayout"
                        app:endIconMode="password_toggle">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/et_password"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textPassword" />
                    </com.google.android.material.textfield.TextInputLayout>

                    <Button
                        android:id="@+id/btn_login"
                        android:layout_width="match_parent"
                        android:layout_height="@dimen/button_height_md"
                        android:text="로그인"
                        style="@style/Widget.App.Button.Primary" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
        </LinearLayout>

        <!-- ── Profile Info Section ────────────────────── -->
        <LinearLayout
            android:id="@+id/layout_profile_info"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="gone">

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
                        android:id="@+id/tv_greeting"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:textAppearance="@style/TextAppearance.App.Title.Large"
                        android:textColor="@color/on_primary"
                        android:layout_marginBottom="@dimen/spacing_1" />

                    <TextView
                        android:id="@+id/tv_store_name"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:textAppearance="@style/TextAppearance.App.Body.Medium"
                        android:textColor="@color/primary_fixed_dim" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Settings Navigation -->
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

            <Button
                android:id="@+id/btn_logout"
                android:layout_width="match_parent"
                android:layout_height="@dimen/button_height_md"
                android:text="로그아웃"
                style="@style/Widget.App.Button.Danger" />
        </LinearLayout>

    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

console.log('[SHOP] 5 layouts fixed');

// ═══════════════════════════════════════════════════════════════════
// USER APP FIXES
// ═══════════════════════════════════════════════════════════════════
console.log('[USER] Fixing layouts...');

// fragment_cart.xml - needs: tv_cart_store_name, tv_total_price, rv_cart_items, btn_checkout
write('app-android-user', 'fragment_cart.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:id="@+id/cart_header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="장바구니"
            android:textAppearance="@style/TextAppearance.App.Headline.Small"
            android:layout_marginBottom="@dimen/spacing_1" />

        <TextView
            android:id="@+id/tv_cart_store_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Body.Medium"
            android:textColor="@color/on_surface_variant" />
    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_cart_items"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        app:layout_constraintTop_toBottomOf="@id/cart_header"
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
            android:text="주문하기"
            style="@style/Widget.App.Button.Primary" />
    </LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>`);

// item_cart.xml - needs: tv_cart_item_name, tv_cart_item_price, tv_cart_item_qty
write('app-android-user', 'item_cart.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="@dimen/spacing_4"
        android:gravity="center_vertical">

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:id="@+id/tv_cart_item_name"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.App.Title.Small"
                android:layout_marginBottom="@dimen/spacing_1" />

            <TextView
                android:id="@+id/tv_cart_item_price"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.App.Body.Medium"
                android:textColor="@color/primary" />
        </LinearLayout>

        <TextView
            android:id="@+id/tv_cart_item_qty"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Title.Small"
            android:textColor="@color/on_surface_variant"
            android:layout_marginStart="@dimen/spacing_4" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

// fragment_search.xml (user) - actually shows order history
// needs: rv_orders, tv_empty_orders
write('app-android-user', 'fragment_search.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <TextView
        android:id="@+id/search_header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        android:text="주문 내역"
        android:textAppearance="@style/TextAppearance.App.Headline.Small"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/tv_empty_orders"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="주문 내역이 없습니다"
        android:textAppearance="@style/TextAppearance.App.Body.Medium"
        android:textColor="@color/on_surface_variant"
        android:visibility="gone"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_orders"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6"
        app:layout_constraintTop_toBottomOf="@id/search_header"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

// fragment_profile.xml (user) - needs: login_layout, profile_info_layout, et_email, et_password,
//                                      btn_login, tv_user_name, tv_user_email
write('app-android-user', 'fragment_profile.xml', `<?xml version="1.0" encoding="utf-8"?>
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
            android:text="내 정보"
            android:textAppearance="@style/TextAppearance.App.Headline.Small"
            android:layout_marginBottom="@dimen/spacing_6" />

        <!-- ── Login Section ──────────────────────────── -->
        <LinearLayout
            android:id="@+id/login_layout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="visible">

            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                style="@style/Widget.App.Card">
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="@dimen/spacing_5">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="로그인"
                        android:textAppearance="@style/TextAppearance.App.Title.Medium"
                        android:layout_marginBottom="@dimen/spacing_4" />

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="이메일"
                        android:layout_marginBottom="@dimen/spacing_3"
                        style="@style/Widget.App.TextInputLayout">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/et_email"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textEmailAddress"
                            android:importantForAutofill="no" />
                    </com.google.android.material.textfield.TextInputLayout>

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="비밀번호"
                        android:layout_marginBottom="@dimen/spacing_5"
                        style="@style/Widget.App.TextInputLayout"
                        app:endIconMode="password_toggle">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/et_password"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textPassword" />
                    </com.google.android.material.textfield.TextInputLayout>

                    <Button
                        android:id="@+id/btn_login"
                        android:layout_width="match_parent"
                        android:layout_height="@dimen/button_height_md"
                        android:text="로그인"
                        style="@style/Widget.App.Button.Primary" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
        </LinearLayout>

        <!-- ── Profile Info Section ────────────────────── -->
        <LinearLayout
            android:id="@+id/profile_info_layout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="gone">

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
                        android:layout_width="@dimen/avatar_size_lg"
                        android:layout_height="@dimen/avatar_size_lg"
                        android:src="@drawable/ic_profile"
                        android:background="@drawable/bg_glass_card"
                        android:padding="@dimen/spacing_4"
                        android:contentDescription="프로필" />

                    <LinearLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:orientation="vertical"
                        android:layout_marginStart="@dimen/spacing_5">

                        <TextView
                            android:id="@+id/tv_user_name"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:textAppearance="@style/TextAppearance.App.Title.Large"
                            android:textColor="@color/on_primary" />

                        <TextView
                            android:id="@+id/tv_user_email"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:textAppearance="@style/TextAppearance.App.Body.Small"
                            android:textColor="@color/primary_fixed_dim" />
                    </LinearLayout>
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="@dimen/spacing_4"
                style="@style/Widget.App.Card">
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
        </LinearLayout>

    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

// fragment_store_detail.xml - needs: tv_store_title, rv_menus
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
                    android:id="@+id/tv_store_title"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:textAppearance="@style/TextAppearance.App.Headline.Medium"
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

// item_menu.xml - needs: tv_menu_name, tv_menu_desc, tv_menu_price, tv_menu_status
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
            app:cardElevation="0dp"
            app:strokeWidth="0dp">
            <ImageView
                android:id="@+id/iv_menu_image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@color/surface_container"
                android:scaleType="centerCrop"
                android:contentDescription="메뉴 이미지" />
        </com.google.android.material.card.MaterialCardView>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:gravity="center_vertical">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:gravity="center_vertical"
                android:layout_marginBottom="@dimen/spacing_1">

                <TextView
                    android:id="@+id/tv_menu_name"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:textAppearance="@style/TextAppearance.App.Title.Small" />

                <TextView
                    android:id="@+id/tv_menu_status"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    style="@style/Widget.App.Badge.Success" />
            </LinearLayout>

            <TextView
                android:id="@+id/tv_menu_desc"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.App.Body.Small"
                android:maxLines="2"
                android:ellipsize="end"
                android:layout_marginBottom="@dimen/spacing_2" />

            <TextView
                android:id="@+id/tv_menu_price"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.App.Title.Small"
                android:textColor="@color/primary" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

// item_order_user.xml - needs: tv_order_store, tv_order_id, tv_order_items, tv_order_amount,
//                               tv_order_date, tv_order_status, btn_reorder
write('app-android-user', 'item_order_user.xml', `<?xml version="1.0" encoding="utf-8"?>
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
            android:layout_marginBottom="@dimen/spacing_2">

            <TextView
                android:id="@+id/tv_order_store"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textAppearance="@style/TextAppearance.App.Title.Small" />

            <TextView
                android:id="@+id/tv_order_status"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                style="@style/Widget.App.Badge.Info" />
        </LinearLayout>

        <TextView
            android:id="@+id/tv_order_id"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Label.Small"
            android:textColor="@color/on_surface_variant"
            android:layout_marginBottom="@dimen/spacing_1" />

        <TextView
            android:id="@+id/tv_order_items"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Body.Medium"
            android:layout_marginBottom="@dimen/spacing_3" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <TextView
                android:id="@+id/tv_order_date"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textAppearance="@style/TextAppearance.App.Label.Small" />

            <TextView
                android:id="@+id/tv_order_amount"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.App.Title.Small"
                android:textColor="@color/primary" />
        </LinearLayout>

        <Button
            android:id="@+id/btn_reorder"
            android:layout_width="match_parent"
            android:layout_height="@dimen/button_height_sm"
            android:layout_marginTop="@dimen/spacing_3"
            android:text="재주문"
            style="@style/Widget.App.Button.Outlined" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

console.log('[USER] 6 layouts fixed');

// ═══════════════════════════════════════════════════════════════════
// DELIVERY APP FIXES
// ═══════════════════════════════════════════════════════════════════
console.log('[DELIVERY] Fixing layouts...');

// fragment_active_delivery.xml - needs: rv_deliveries, tv_empty_deliveries
write('app-android-delivery', 'fragment_active_delivery.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <TextView
        android:id="@+id/active_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        android:text="진행중 배달"
        android:textAppearance="@style/TextAppearance.App.Headline.Small"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/tv_empty_deliveries"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="진행중인 배달이 없습니다"
        android:textAppearance="@style/TextAppearance.App.Body.Medium"
        android:textColor="@color/on_surface_variant"
        android:visibility="gone"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_deliveries"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6"
        app:layout_constraintTop_toBottomOf="@id/active_title"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

// fragment_history.xml - needs: rv_history, tv_empty_history, tv_completed_count, tv_total_earnings
write('app-android-delivery', 'fragment_history.xml', `<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface">

    <LinearLayout
        android:id="@+id/history_header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingVertical="@dimen/spacing_5"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="배달 내역"
            android:textAppearance="@style/TextAppearance.App.Headline.Small"
            android:layout_marginBottom="@dimen/spacing_4" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <com.google.android.material.card.MaterialCardView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginEnd="@dimen/spacing_2"
                style="@style/Widget.App.Card.Tonal">
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="@dimen/spacing_4">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="완료 건수"
                        android:textAppearance="@style/TextAppearance.App.Label.Small" />
                    <TextView
                        android:id="@+id/tv_completed_count"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="0건"
                        android:textAppearance="@style/TextAppearance.App.Title.Large"
                        android:textColor="@color/secondary"
                        android:layout_marginTop="@dimen/spacing_1" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <com.google.android.material.card.MaterialCardView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="@dimen/spacing_2"
                style="@style/Widget.App.Card.Tonal">
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="@dimen/spacing_4">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="총 수입"
                        android:textAppearance="@style/TextAppearance.App.Label.Small" />
                    <TextView
                        android:id="@+id/tv_total_earnings"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="₩0"
                        android:textAppearance="@style/TextAppearance.App.Title.Large"
                        android:textColor="@color/primary"
                        android:layout_marginTop="@dimen/spacing_1" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
        </LinearLayout>
    </LinearLayout>

    <TextView
        android:id="@+id/tv_empty_history"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="배달 내역이 없습니다"
        android:textAppearance="@style/TextAppearance.App.Body.Medium"
        android:textColor="@color/on_surface_variant"
        android:visibility="gone"
        app:layout_constraintTop_toBottomOf="@id/history_header"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_history"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6"
        app:layout_constraintTop_toBottomOf="@id/history_header"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>`);

// fragment_profile.xml (delivery) - needs: layout_login, layout_profile_info, et_email, et_password,
//     btn_login, btn_logout, tv_greeting, tv_email, tv_stat_completed, tv_stat_earnings
write('app-android-delivery', 'fragment_profile.xml', `<?xml version="1.0" encoding="utf-8"?>
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
            android:text="내 정보"
            android:textAppearance="@style/TextAppearance.App.Headline.Small"
            android:layout_marginBottom="@dimen/spacing_6" />

        <!-- ── Login Section ──────────────────────────── -->
        <LinearLayout
            android:id="@+id/layout_login"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="visible">

            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                style="@style/Widget.App.Card">
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="@dimen/spacing_5">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="배달원 로그인"
                        android:textAppearance="@style/TextAppearance.App.Title.Medium"
                        android:layout_marginBottom="@dimen/spacing_4" />

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="이메일"
                        android:layout_marginBottom="@dimen/spacing_3"
                        style="@style/Widget.App.TextInputLayout">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/et_email"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textEmailAddress"
                            android:importantForAutofill="no" />
                    </com.google.android.material.textfield.TextInputLayout>

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="비밀번호"
                        android:layout_marginBottom="@dimen/spacing_5"
                        style="@style/Widget.App.TextInputLayout"
                        app:endIconMode="password_toggle">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/et_password"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textPassword" />
                    </com.google.android.material.textfield.TextInputLayout>

                    <Button
                        android:id="@+id/btn_login"
                        android:layout_width="match_parent"
                        android:layout_height="@dimen/button_height_md"
                        android:text="로그인"
                        style="@style/Widget.App.Button.Primary" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
        </LinearLayout>

        <!-- ── Profile Info Section ────────────────────── -->
        <LinearLayout
            android:id="@+id/layout_profile_info"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="gone">

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
                        android:id="@+id/tv_greeting"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:textAppearance="@style/TextAppearance.App.Title.Large"
                        android:textColor="@color/on_primary"
                        android:layout_marginBottom="@dimen/spacing_1" />

                    <TextView
                        android:id="@+id/tv_email"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:textAppearance="@style/TextAppearance.App.Body.Small"
                        android:textColor="@color/primary_fixed_dim" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Stats Cards -->
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
                    style="@style/Widget.App.Card.Tonal">
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="vertical"
                        android:padding="@dimen/spacing_4">
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="완료 건수"
                            android:textAppearance="@style/TextAppearance.App.Label.Small" />
                        <TextView
                            android:id="@+id/tv_stat_completed"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="0건"
                            android:textAppearance="@style/TextAppearance.App.Title.Large"
                            android:textColor="@color/secondary"
                            android:layout_marginTop="@dimen/spacing_1" />
                    </LinearLayout>
                </com.google.android.material.card.MaterialCardView>

                <com.google.android.material.card.MaterialCardView
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:layout_marginStart="@dimen/spacing_2"
                    style="@style/Widget.App.Card.Tonal">
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="vertical"
                        android:padding="@dimen/spacing_4">
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="총 수입"
                            android:textAppearance="@style/TextAppearance.App.Label.Small" />
                        <TextView
                            android:id="@+id/tv_stat_earnings"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="₩0"
                            android:textAppearance="@style/TextAppearance.App.Title.Large"
                            android:textColor="@color/primary"
                            android:layout_marginTop="@dimen/spacing_1" />
                    </LinearLayout>
                </com.google.android.material.card.MaterialCardView>
            </LinearLayout>

            <Button
                android:id="@+id/btn_logout"
                android:layout_width="match_parent"
                android:layout_height="@dimen/button_height_md"
                android:text="로그아웃"
                style="@style/Widget.App.Button.Danger" />
        </LinearLayout>

    </LinearLayout>
</androidx.core.widget.NestedScrollView>`);

// item_delivery.xml - needs: tv_delivery_id, tv_delivery_status, tv_delivery_address, tv_delivery_time, btn_action
write('app-android-delivery', 'item_delivery.xml', `<?xml version="1.0" encoding="utf-8"?>
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
                android:id="@+id/tv_delivery_id"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textAppearance="@style/TextAppearance.App.Title.Small"
                android:textColor="@color/primary" />

            <TextView
                android:id="@+id/tv_delivery_status"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                style="@style/Widget.App.Badge.Info" />
        </LinearLayout>

        <TextView
            android:id="@+id/tv_delivery_address"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Body.Medium"
            android:drawableStart="@drawable/ic_active_delivery"
            android:drawablePadding="@dimen/spacing_2"
            android:layout_marginBottom="@dimen/spacing_2" />

        <TextView
            android:id="@+id/tv_delivery_time"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Label.Small"
            android:layout_marginBottom="@dimen/spacing_3" />

        <Button
            android:id="@+id/btn_action"
            android:layout_width="match_parent"
            android:layout_height="@dimen/button_height_sm"
            android:text="배달 완료"
            style="@style/Widget.App.Button.Primary" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

// item_history.xml - needs: tv_history_id, tv_history_date, tv_history_items, tv_history_amount
write('app-android-delivery', 'item_history.xml', `<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/spacing_3"
    style="@style/Widget.App.Card">

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
            android:layout_marginBottom="@dimen/spacing_2">

            <TextView
                android:id="@+id/tv_history_id"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textAppearance="@style/TextAppearance.App.Title.Small"
                android:textColor="@color/primary" />

            <TextView
                android:id="@+id/tv_history_amount"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.App.Title.Medium"
                android:textColor="@color/secondary" />
        </LinearLayout>

        <TextView
            android:id="@+id/tv_history_items"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Body.Medium"
            android:layout_marginBottom="@dimen/spacing_1" />

        <TextView
            android:id="@+id/tv_history_date"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Label.Small" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

console.log('[DELIVERY] 5 layouts fixed');

// ═══════════════════════════════════════════════════════════════════
// KIOSK APP FIXES
// ═══════════════════════════════════════════════════════════════════
console.log('[KIOSK] Fixing layouts...');

// activity_main.xml - needs: nav_view, main_fragment_container, cart_fragment_container
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
        android:id="@+id/main_fragment_container"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1" />

    <!-- Cart Panel -->
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/cart_fragment_container"
        android:layout_width="@dimen/kiosk_cart_panel_width"
        android:layout_height="match_parent"
        android:background="@color/surface_container_lowest"
        android:elevation="@dimen/elevation_sm" />
</LinearLayout>`);

// fragment_menu_order.xml - needs: rv_kiosk_menus
write('app-android-kiosk', 'fragment_menu_order.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="메뉴 선택"
        android:textAppearance="@style/TextAppearance.App.Headline.Medium"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingTop="@dimen/spacing_6"
        android:paddingBottom="@dimen/spacing_4" />

    <HorizontalScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:scrollbars="none"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:layout_marginBottom="@dimen/spacing_4">

        <com.google.android.material.chip.ChipGroup
            android:id="@+id/chip_categories"
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
                android:text="인기"
                style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="세트"
                style="@style/Widget.Material3.Chip.Filter" />
            <com.google.android.material.chip.Chip
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="음료"
                style="@style/Widget.Material3.Chip.Filter" />
        </com.google.android.material.chip.ChipGroup>
    </HorizontalScrollView>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_kiosk_menus"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/page_horizontal_padding"
        android:paddingBottom="@dimen/spacing_6" />

</LinearLayout>`);

// fragment_order_status.xml - needs: tv_preparing_orders, tv_serving_orders
write('app-android-kiosk', 'fragment_order_status.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface"
    android:padding="@dimen/spacing_6">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="주문 현황"
        android:textAppearance="@style/TextAppearance.App.Headline.Medium"
        android:layout_marginBottom="@dimen/spacing_6" />

    <!-- Preparing Orders -->
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
                android:text="준비중"
                android:textAppearance="@style/TextAppearance.App.Label.Medium"
                android:textColor="@color/primary_fixed_dim"
                android:layout_marginBottom="@dimen/spacing_3" />

            <TextView
                android:id="@+id/tv_preparing_orders"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="준비중인 주문이 없습니다"
                android:textAppearance="@style/TextAppearance.App.Title.Large"
                android:textColor="@color/on_primary" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <!-- Serving Orders -->
    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        style="@style/Widget.App.Card">
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="@dimen/spacing_6">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="완료"
                android:textAppearance="@style/TextAppearance.App.Label.Medium"
                android:textColor="@color/secondary"
                android:layout_marginBottom="@dimen/spacing_3" />

            <TextView
                android:id="@+id/tv_serving_orders"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="완료된 주문이 없습니다"
                android:textAppearance="@style/TextAppearance.App.Title.Large"
                android:textColor="@color/on_surface" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>
</LinearLayout>`);

// fragment_cart.xml (kiosk) - needs: rv_cart_items, tv_empty_cart, tv_total_price, btn_checkout
write('app-android-kiosk', 'fragment_cart.xml', `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/surface_container_lowest">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="주문 내역"
        android:textAppearance="@style/TextAppearance.App.Title.Large"
        android:padding="@dimen/spacing_5" />

    <TextView
        android:id="@+id/tv_empty_cart"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="장바구니가 비어 있습니다"
        android:textAppearance="@style/TextAppearance.App.Body.Medium"
        android:textColor="@color/on_surface_variant"
        android:gravity="center"
        android:padding="@dimen/spacing_6"
        android:visibility="gone" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_cart_items"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingHorizontal="@dimen/spacing_4" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:background="@drawable/bg_glass_card"
        android:padding="@dimen/spacing_5">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginBottom="@dimen/spacing_3">

            <TextView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="총 금액"
                android:textAppearance="@style/TextAppearance.App.Body.Large" />

            <TextView
                android:id="@+id/tv_total_price"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="₩0"
                android:textAppearance="@style/TextAppearance.App.Headline.Medium"
                android:textColor="@color/primary" />
        </LinearLayout>

        <Button
            android:id="@+id/btn_checkout"
            android:layout_width="match_parent"
            android:layout_height="@dimen/button_height_lg"
            android:text="주문하기"
            style="@style/Widget.App.Button.Primary" />
    </LinearLayout>
</LinearLayout>`);

// item_menu_kiosk.xml - needs: tv_menu_name, tv_menu_status
write('app-android-kiosk', 'item_menu_kiosk.xml', `<?xml version="1.0" encoding="utf-8"?>
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
        android:padding="@dimen/spacing_5">

        <com.google.android.material.card.MaterialCardView
            android:layout_width="96dp"
            android:layout_height="96dp"
            android:layout_marginEnd="@dimen/spacing_5"
            app:cardCornerRadius="@dimen/radius_md"
            app:cardElevation="0dp"
            app:strokeWidth="0dp">
            <ImageView
                android:id="@+id/iv_menu_image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@color/surface_container"
                android:scaleType="centerCrop"
                android:contentDescription="메뉴 이미지" />
        </com.google.android.material.card.MaterialCardView>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:gravity="center_vertical"
                android:layout_marginBottom="@dimen/spacing_1">

                <TextView
                    android:id="@+id/tv_menu_name"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:textAppearance="@style/TextAppearance.App.Title.Medium" />

                <TextView
                    android:id="@+id/tv_menu_status"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    style="@style/Widget.App.Badge.Success" />
            </LinearLayout>

            <TextView
                android:id="@+id/tv_menu_desc"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.App.Body.Small"
                android:maxLines="2"
                android:ellipsize="end"
                android:layout_marginBottom="@dimen/spacing_3" />

            <TextView
                android:id="@+id/tv_menu_price"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.App.Title.Large"
                android:textColor="@color/primary" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>`);

console.log('[KIOSK] 5 layouts fixed');
console.log('=== ALL ID FIXES DONE ===');
