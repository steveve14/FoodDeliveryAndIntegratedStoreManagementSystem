package com.fooddelivery.kiosk;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 키오스크 설정 관리 클래스.
 * SharedPreferences를 통해 매장 ID 등 설정값을 영속 관리합니다.
 */
public class KioskConfig {
    private static final String PREFS_NAME = "kiosk_config";
    private static final String KEY_STORE_ID = "store_id";
    private static final String DEFAULT_STORE_ID = ""; // 미설정 시 빈 문자열

    private static KioskConfig instance;
    private SharedPreferences prefs;

    private KioskConfig(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized KioskConfig getInstance(Context context) {
        if (instance == null) {
            instance = new KioskConfig(context);
        }
        return instance;
    }

    /** 설정된 매장 ID를 반환합니다. 미설정 시 null 반환. */
    public String getStoreId() {
        String storeId = prefs.getString(KEY_STORE_ID, DEFAULT_STORE_ID);
        return storeId.isEmpty() ? null : storeId;
    }

    /** 매장 ID를 설정합니다. */
    public void setStoreId(String storeId) {
        prefs.edit().putString(KEY_STORE_ID, storeId == null ? "" : storeId).apply();
    }

    /** 매장 ID가 설정되었는지 확인합니다. */
    public boolean isConfigured() {
        return getStoreId() != null;
    }
}
