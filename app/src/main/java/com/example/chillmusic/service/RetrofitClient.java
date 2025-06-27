package com.example.chillmusic.service;

import android.content.Context;
import android.util.Log;

import com.example.chillmusic.utils.AuthPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


import java.io.IOException;
import java.util.List;

public class RetrofitClient {
    private static final String BASE_URL = "https://sleepchills.kenhtao.site/api/v1/";

    public static Retrofit getRetrofitInstance(Context context) {
        AuthPreferences authPrefs = new AuthPreferences(context);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    // Gửi token hiện tại
                    String xsrfToken = authPrefs.getXsrfToken();
                    String sessionToken = authPrefs.getSessionToken();

                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Cookie", "XSRF-TOKEN=" + xsrfToken + "; laravel_session=" + sessionToken)
                            .header("X-XSRF-TOKEN", URLDecoder.decode(xsrfToken, StandardCharsets.UTF_8.toString()));


                    Response response = chain.proceed(builder.build());

                    // Nhận cookie mới từ response
                    List<String> cookies = response.headers("Set-Cookie");
                    if (cookies != null) {
                        String newXsrf = null;
                        String newSession = null;
                        for (String cookie : cookies) {
                            if (cookie.startsWith("XSRF-TOKEN=")) {
                                newXsrf = cookie.split(";")[0].split("=")[1];
                            } else if (cookie.startsWith("laravel_session=")) {
                                newSession = cookie.split(";")[0].split("=")[1];
                            }
                        }
                        // Sửa: Lưu từng token nếu có, không cần đủ cả 2 mới lưu
                        if (newXsrf != null) authPrefs.saveXsrfToken(newXsrf);
                        if (newSession != null) authPrefs.saveSessionToken(newSession);
                        if (newXsrf != null || newSession != null) {
                            Log.d("TOKEN_UPDATE", "✅ Token updated from server.");
                        }
                    }

                    return response;
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL + "api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiService getApiService(Context context) {
        return getRetrofitInstance(context).create(ApiService.class);
    }
}
