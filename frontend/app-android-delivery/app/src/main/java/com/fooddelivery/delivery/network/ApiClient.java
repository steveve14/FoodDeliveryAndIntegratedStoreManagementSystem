package com.fooddelivery.delivery.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static Retrofit retrofit = null;
    private static final SessionCookieJar cookieJar = new SessionCookieJar();

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SessionCookieJar getCookieJar() {
        return cookieJar;
    }
}
