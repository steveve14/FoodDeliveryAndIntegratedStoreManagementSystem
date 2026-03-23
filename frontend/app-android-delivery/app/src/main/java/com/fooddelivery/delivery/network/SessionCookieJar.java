package com.fooddelivery.delivery.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class SessionCookieJar implements CookieJar {
    private final Map<String, Cookie> cookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            cookieStore.put(cookie.name(), cookie);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return new ArrayList<>(cookieStore.values());
    }

    public void clear() {
        cookieStore.clear();
    }
}
