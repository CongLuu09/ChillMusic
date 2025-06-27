package com.example.chillmusic.ui.custom;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.adapter.CustomSoundAdapter;
import com.example.chillmusic.models.SoundDto;
import com.example.chillmusic.models.SoundItem;
import com.example.chillmusic.service.ApiService;
import com.example.chillmusic.service.RetrofitClient;
import com.example.chillmusic.utils.AuthPreferences;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomSoundPickerActivity extends AppCompatActivity implements CustomSoundAdapter.OnSoundClickListener {

    private RecyclerView recyclerViewCustomSounds;
    private CustomSoundAdapter adapter;
    private ImageView btnClose;
    private TextView tvTitle;
    private final List<Object> allItems = new ArrayList<>();
    private final List<SoundDto> onlineSounds = new ArrayList<>();
    private RewardedAd rewardedAd;
    private boolean isLoadingAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_sound_picker);

        recyclerViewCustomSounds = findViewById(R.id.recyclerViewCustomSounds);
        btnClose = findViewById(R.id.btnClose);
        tvTitle = findViewById(R.id.tvTitle);

        setupRecyclerView();
        setupListeners();
        fetchOnlineSounds();


        MobileAds.initialize(this, initializationStatus -> {});
        loadRewardedAd();
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        adapter = new CustomSoundAdapter(this, allItems, this);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == CustomSoundAdapter.VIEW_TYPE_HEADER ? 3 : 1;
            }
        });
        recyclerViewCustomSounds.setLayoutManager(layoutManager);
        recyclerViewCustomSounds.setAdapter(adapter);
    }

    private void fetchOnlineSounds() {
        AuthPreferences authPrefs = new AuthPreferences(this);
        String xsrfToken = authPrefs.getXsrfToken();
        String sessionToken = authPrefs.getSessionToken();

        ApiService api = RetrofitClient.getApiService(this);


        api.getAllSounds().enqueue(new Callback<List<SoundDto>>() {
            @Override
            public void onResponse(Call<List<SoundDto>> call, Response<List<SoundDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onlineSounds.clear();
                    onlineSounds.addAll(response.body());
                    updateCombinedSoundItems();
                }
            }

            @Override
            public void onFailure(Call<List<SoundDto>> call, Throwable t) {
                Log.e("API", "❌ Failed to fetch sounds from server", t);
            }
        });
    }

    private void updateCombinedSoundItems() {
        allItems.clear();
        String baseUrl = "https://sleepchills.kenhtao.site/";

        if (!onlineSounds.isEmpty()) {
            Map<String, List<SoundItem>> groupedByCategory = new LinkedHashMap<>();
            for (SoundDto dto : onlineSounds) {
                String fullFileUrl = dto.getFileUrl() != null ? baseUrl + dto.getFileUrl().replaceFirst("^/", "") : null;
                String fullImageUrl = dto.getImageUrl() != null ? baseUrl + dto.getImageUrl().replaceFirst("^/", "") : null;

                SoundItem soundItem = new SoundItem(
                        dto.getId(), 0, true,
                        dto.getName(), 0,
                        fullFileUrl, fullImageUrl
                );

                String category = dto.getCategory() == null || dto.getCategory().isEmpty() ? "Others" : dto.getCategory();
                groupedByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(soundItem);
            }

            for (String category : groupedByCategory.keySet()) {
                allItems.add(category);
                allItems.addAll(groupedByCategory.get(category));
            }
        }

        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    @Override
    public void onSoundClick(SoundItem item) {
        if (item == null) return;

        if (SoundUnlockManager.isSoundUnlocked(this, (int) item.getId())) {
            returnResult(item);
        } else {
            showRewardedAd(item);
        }

    }

    private void returnResult(SoundItem item) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("name", item.getName());
        resultIntent.putExtra("iconResId", item.getIconResId());
        resultIntent.putExtra("soundResId", item.getSoundResId());
        resultIntent.putExtra("soundId", item.getId());
        resultIntent.putExtra("fileUrl", item.getFileUrl());
        resultIntent.putExtra("imageUrl", item.getImageUrl());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void loadRewardedAd() {
        if (isLoadingAd || rewardedAd != null) return;

        isLoadingAd = true;
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                isLoadingAd = false;
            }

            @Override
            public void onAdFailedToLoad(@NonNull com.google.android.gms.ads.LoadAdError adError) {
                rewardedAd = null;
                isLoadingAd = false;
                Log.e("AD", "❌ Ad load failed: " + adError.getMessage());
            }
        });
    }

    private void showRewardedAd(SoundItem item) {
        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    rewardedAd = null;
                    loadRewardedAd();
                }
            });

            rewardedAd.show(this, rewardItem -> {
                SoundUnlockManager.unlockSound(this, (int) item.getId());
                Toast.makeText(this, "✅ Đã mở khóa trong 7 ngày!", Toast.LENGTH_SHORT).show();
                returnResult(item);
            });
        } else {
            Toast.makeText(this, "Quảng cáo chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
        }
    }


    public static class SoundUnlockManager {
        private static final String PREF_NAME = "SoundUnlockPrefs";
        private static final long UNLOCK_DURATION_MS = 7L * 24 * 60 * 60 * 1000;

        public static boolean isSoundUnlocked(Context context, int soundId) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            long expireAt = prefs.getLong("sound_" + soundId, 0);
            return System.currentTimeMillis() < expireAt;
        }

        public static void unlockSound(Context context, int soundId) {
            long expireAt = System.currentTimeMillis() + UNLOCK_DURATION_MS;
            SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putLong("sound_" + soundId, expireAt);
            editor.apply();
        }
    }
}
