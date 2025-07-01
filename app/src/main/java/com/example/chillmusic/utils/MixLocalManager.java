package com.example.chillmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chillmusic.models.LayerSound;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MixLocalManager {
    private static final String PREF_NAME = "MIX_PREF";

    private static String keyMix(long categoryId) {
        return "mix_" + categoryId;
    }

    private static String keyMixFull(long categoryId) {
        return "mix_full_" + categoryId;
    }

    private static String keyCreatedAt(long categoryId) {
        return "createdAt_" + categoryId;
    }

    public static void saveMixFull(Context context, long categoryId, List<LayerSound> layers) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(layers);

        prefs.edit()
                .putString(keyMixFull(categoryId), json)
                .putLong(keyCreatedAt(categoryId), System.currentTimeMillis())
                .apply();
    }

    // ✅ NEW: Load danh sách LayerSound đầy đủ
    public static List<LayerSound> loadMixFull(Context context, long categoryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(keyMixFull(categoryId), "");
        if (json.isEmpty()) return new ArrayList<>();

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<LayerSound>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ Lưu mix theo categoryId và thời gian tạo
    public static void saveMix(Context context, long categoryId, List<Long> soundIds) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < soundIds.size(); i++) {
            sb.append(soundIds.get(i));
            if (i < soundIds.size() - 1) sb.append(",");
        }

        prefs.edit()
                .putString(keyMix(categoryId), sb.toString())
                .putLong(keyCreatedAt(categoryId), System.currentTimeMillis()) // ✅ Ghi thời gian
                .apply();
    }

    // ✅ Load mix theo categoryId
    public static List<Long> loadMix(Context context, long categoryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String saved = prefs.getString(keyMix(categoryId), "");
        List<Long> result = new ArrayList<>();
        if (!saved.isEmpty()) {
            String[] parts = saved.split(",");
            for (String part : parts) {
                try {
                    result.add(Long.parseLong(part));
                } catch (NumberFormatException ignored) {}
            }
        }
        return result;
    }

    // ✅ Lấy thời gian tạo mix local
    public static long getCreatedAt(Context context, long categoryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(keyCreatedAt(categoryId), 0);
    }

    // ✅ Xoá mix local
    public static void clearMix(Context context, long categoryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(keyMix(categoryId))
                .remove(keyCreatedAt(categoryId))
                .apply();
    }
}
