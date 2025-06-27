package com.example.chillmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthPreferences {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_XSRF = "XSRF-TOKEN";
    private static final String KEY_SESSION = "SESSION";

    private final SharedPreferences prefs;

    public AuthPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTokens(String xsrfToken, String sessionToken) {
        prefs.edit()
                .putString(KEY_XSRF, xsrfToken)
                .putString(KEY_SESSION, sessionToken)
                .apply();
    }

    public String getXsrfToken() {
        return prefs.getString(KEY_XSRF, "");
    }

    public String getSessionToken() {
        return prefs.getString(KEY_SESSION, "");
    }
    public void saveXsrfToken(String xsrfToken) {
        prefs.edit().putString(KEY_XSRF, xsrfToken).apply();
    }

    public void saveSessionToken(String sessionToken) {
        prefs.edit().putString(KEY_SESSION, sessionToken).apply();
    }
}