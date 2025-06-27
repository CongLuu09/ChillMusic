package com.example.chillmusic.ui.player;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chillmusic.R;
import com.example.chillmusic.Timer.TimerCallBack;
import com.example.chillmusic.Timer.TimerDialog;
import com.example.chillmusic.Timer.TimerViewModel;
import com.example.chillmusic.adapter.PlayLayerAdapter;
import com.example.chillmusic.models.LayerSound;
import com.example.chillmusic.models.MixCreateRequest;
import com.example.chillmusic.models.MixDto;
import com.example.chillmusic.models.SoundDto;
import com.example.chillmusic.service.ApiService;
import com.example.chillmusic.service.RetrofitClient;
import com.example.chillmusic.ui.custom.CustomSoundPickerActivity;
import com.example.chillmusic.utils.AuthPreferences;
import com.example.chillmusic.utils.DeviceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryPlayerActivity extends AppCompatActivity {

    private ImageView btnBack, btnPlayPause, btnAddLayer, btnSaveSound, imgBackground;
    private TextView tvTitle, tvTimer;
    private RecyclerView recyclerViewLayers;
    private boolean isPlaying = false;
    private MediaPlayer mainPlayer;
    private final List<LayerSound> layers = new ArrayList<>();
    private PlayLayerAdapter layerAdapter;
    private TimerViewModel timerViewModel;
    private String categoryName;
    private String categoryImageUrl;
    private List<SoundDto> allSounds = new ArrayList<>();
    private int mainSoundResId = -1;


    private static final Map<String, Integer> categorySoundMap = new HashMap<>();
    static {
        categorySoundMap.put("Ocean", R.raw.ocean_main);
        categorySoundMap.put("Forest", R.raw.forest);
        categorySoundMap.put("Rain", R.raw.rain_forest);
        categorySoundMap.put("Midnight", R.raw.cicada);
        categorySoundMap.put("Fire", R.raw.fire);
        categorySoundMap.put("Lake", R.raw.lake);
        categorySoundMap.put("Farm", R.raw.farm);
        categorySoundMap.put("Water Fall", R.raw.waterfall);
        categorySoundMap.put("Under Water", R.raw.underwater);
        categorySoundMap.put("Desert", R.raw.desert);
        categorySoundMap.put("Train Journey", R.raw.train);
        categorySoundMap.put("Air Travel", R.raw.airplane);
        categorySoundMap.put("Cafe & Chill", R.raw.chill_ambience);
        categorySoundMap.put("Downtown Dreams", R.raw.city);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_player);

        btnBack = findViewById(R.id.btnBack);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnSaveSound = findViewById(R.id.btnSaveSound);
        btnAddLayer = findViewById(R.id.btnAddLayer);
        tvTitle = findViewById(R.id.tvTitle);
        tvTimer = findViewById(R.id.tvTimer);
        recyclerViewLayers = findViewById(R.id.recyclerViewLayers);
        imgBackground = findViewById(R.id.imgBackground);
        String imageUrl = getIntent().getStringExtra("imageUrl");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                imageUrl = "https://sleepchills.kenhtao.site/" + imageUrl;
            }

            Glide.with(this)
                    .load(imageUrl)
                    .into(imgBackground);
        }

        categoryName = getIntent().getStringExtra("categoryName");
        categoryImageUrl = getIntent().getStringExtra("categoryImage");

        tvTitle.setText(categoryName);

        if (categoryImageUrl != null && !categoryImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(categoryImageUrl)
                    .placeholder(R.drawable.backocean)
                    .into(imgBackground);
        }

        if (categoryName != null && categorySoundMap.containsKey(categoryName)) {
            mainSoundResId = categorySoundMap.get(categoryName);
        }

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        setupListeners();
        setupTimerObserver();
        setupLayerSounds();
        loadSoundsFromApi();
        loadSavedMixIfExists();
    }
    private void loadSoundsFromApi() {
        AuthPreferences authPrefs = new AuthPreferences(this);
        String xsrfToken = authPrefs.getXsrfToken();
        String sessionToken = authPrefs.getSessionToken();

        ApiService api = RetrofitClient.getApiService(this);




        api.getSoundsByCategory(categoryName).enqueue(new Callback<List<SoundDto>>() {
            @Override
            public void onResponse(Call<List<SoundDto>> call, Response<List<SoundDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LayerSound> loadedLayers = new ArrayList<>();
                    for (SoundDto dto : response.body()) {
                        LayerSound layer = new LayerSound(0, dto.getName(), 0,
                                dto.getFileUrl(), 0.1f, dto.getImageUrl());
                        layer.setId(dto.getId());
                        loadedLayers.add(layer);
                    }
                    layers.clear();
                    layers.addAll(loadedLayers);
                    layerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<SoundDto>> call, Throwable t) {
                Log.e("CategoryPlayer", "❌ Failed to load sounds", t);
            }
        });
    }

    private void setupLayerSounds() {
        layerAdapter = new PlayLayerAdapter(this, layers);
        recyclerViewLayers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLayers.setAdapter(layerAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMainSound();
            } else {
                playMainSound();
            }
        });

        btnAddLayer.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomSoundPickerActivity.class);
            customSoundLauncher.launch(intent);
        });

        btnSaveSound.setOnClickListener(v -> saveMixToApi());

        tvTimer.setOnClickListener(v -> {
            TimerDialog dialog = TimerDialog.newInstance();
            dialog.setCallback(new TimerCallBack() {
                @Override
                public void onTimerSet(long durationMillis) {
                    timerViewModel.startTimer(durationMillis);
                }
                @Override
                public void onTimerCancelled() {
                    timerViewModel.cancelTimer();
                }
                @Override
                public void onTimerFinished() {
                    runOnUiThread(() -> {
                        pauseMainSound();
                        if (layerAdapter != null) layerAdapter.releaseAllPlayers();
                    });
                }
            });
            dialog.show(getSupportFragmentManager(), "TimerDialog");
        });
    }

    private void setupTimerObserver() {
        timerViewModel.getTimeLeftMillis().observe(this, timeLeft -> {
            if (timeLeft > 0) {
                long minutes = (timeLeft / 1000) / 60;
                long seconds = (timeLeft / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            } else {
                tvTimer.setText("Timer");
            }
        });

        timerViewModel.getIsRunning().observe(this, isRunning -> {
            if (!isRunning) {
                pauseMainSound();
                if (layerAdapter != null) layerAdapter.releaseAllPlayers();
            }
        });
    }

    private void saveMixToApi() {
        List<Long> soundIds = new ArrayList<>();
        for (LayerSound layer : layers) {
            if (layer.getId() != -1) {
                soundIds.add(layer.getId());
            }
        }

        if (soundIds.isEmpty()) {
            Toast.makeText(this, "Không có âm thanh hợp lệ để lưu.", Toast.LENGTH_SHORT).show();
            return;
        }

        String deviceId = DeviceUtils.getDeviceId(this);
        String mixName = categoryName + "_Mix_" + System.currentTimeMillis();

        MixCreateRequest request = new MixCreateRequest(deviceId, mixName, soundIds);

        AuthPreferences authPrefs = new AuthPreferences(this);
        String xsrfToken = authPrefs.getXsrfToken();
        String sessionToken = authPrefs.getSessionToken();

        ApiService api = RetrofitClient.getApiService(this);



        api.createMix(request).enqueue(new Callback<MixDto>() {
            @Override
            public void onResponse(Call<MixDto> call, Response<MixDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CategoryPlayerActivity.this, "✅ Mix đã được lưu thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CategoryPlayerActivity.this, "❌ Không lưu được mix (API lỗi)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MixDto> call, Throwable t) {
                Toast.makeText(CategoryPlayerActivity.this, "❌ Kết nối API thất bại khi lưu mix", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSavedMixIfExists() {
        AuthPreferences authPrefs = new AuthPreferences(this);
        String xsrfToken = authPrefs.getXsrfToken();
        String sessionToken = authPrefs.getSessionToken();

        ApiService api = RetrofitClient.getApiService(this);


        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        api.getMixesByDevice(deviceId).enqueue(new Callback<List<MixDto>>() {
            @Override
            public void onResponse(Call<List<MixDto>> call, Response<List<MixDto>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                    String prefix = categoryName + "_Mix_";
                    List<MixDto> matchingMixes = new ArrayList<>();
                    for (MixDto mix : response.body()) {
                        if (mix.getName() != null && mix.getName().startsWith(prefix)) {
                            matchingMixes.add(mix);
                        }
                    }


                    if (!matchingMixes.isEmpty()) {
                        Collections.sort(matchingMixes, (a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                        MixDto latestMix = matchingMixes.get(0);

                        List<Long> soundIds = new ArrayList<>();
                        for (Integer id : latestMix.getSoundIds()) {
                            soundIds.add(id.longValue());
                        }


                        loadSoundsByIds(soundIds, layerSounds -> {
                            layers.clear();
                            layers.addAll(layerSounds);
                            layerAdapter.notifyDataSetChanged();
                            Log.d("CategoryMix", "✅ Loaded mix: " + latestMix.getName());
                        });
                    } else {
                        Log.d("CategoryMix", "ℹ️ Không tìm thấy mix nào cho category: " + categoryName);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MixDto>> call, Throwable t) {
                Log.e("CategoryMix", "❌ Failed to load mixes", t);
            }
        });
    }

    private void loadSoundsByIds(List<Long> ids, Consumer<List<LayerSound>> callback) {
        if (ids == null || ids.isEmpty()) {
            callback.accept(new ArrayList<>());
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            sb.append(ids.get(i));
            if (i < ids.size() - 1) sb.append(",");
        }
        String commaSeparatedIds = sb.toString();

        AuthPreferences authPrefs = new AuthPreferences(this);
        String xsrfToken = authPrefs.getXsrfToken();
        String sessionToken = authPrefs.getSessionToken();

        ApiService api = RetrofitClient.getApiService(this);



        api.getSoundsByIds(commaSeparatedIds).enqueue(new Callback<List<SoundDto>>() {
            @Override
            public void onResponse(Call<List<SoundDto>> call, Response<List<SoundDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LayerSound> layerSounds = new ArrayList<>();
                    String baseUrl = "https://sleepchills.kenhtao.site/";

                    for (SoundDto dto : response.body()) {
                        String fullFileUrl = (dto.getFileUrl() != null && !dto.getFileUrl().startsWith("http"))
                                ? baseUrl + dto.getFileUrl()
                                : dto.getFileUrl();

                        String fullImageUrl = (dto.getImageUrl() != null && !dto.getImageUrl().startsWith("http"))
                                ? baseUrl + dto.getImageUrl()
                                : dto.getImageUrl();

                        LayerSound ls = new LayerSound(
                                0,
                                dto.getName(),
                                0,
                                fullFileUrl,
                                0.1f,
                                fullImageUrl
                        );
                        ls.setId(dto.getId());
                        layerSounds.add(ls);
                    }

                    callback.accept(layerSounds);
                } else {
                    Log.e("CategoryMix", "❌ Failed to fetch sounds: " + response.code());
                    callback.accept(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<SoundDto>> call, Throwable t) {
                Log.e("CategoryMix", "❌ API error while loading sounds", t);
                callback.accept(new ArrayList<>());
            }
        });
    }


    private void playMainSound() {
        if (mainPlayer == null) {
            if (!layers.isEmpty()) {
                String url = layers.get(0).getFileUrl();
                mainPlayer = new MediaPlayer();
                try {
                    mainPlayer.setDataSource(url);
                    mainPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mainPlayer.setLooping(true);
                    mainPlayer.setOnPreparedListener(mp -> {
                        mp.start();
                        isPlaying = true;
                        btnPlayPause.setImageResource(R.drawable.stop);
                    });
                    mainPlayer.prepareAsync();
                } catch (IOException e) {
                    Log.e("CategoryPlayer", "❌ MediaPlayer error", e);
                }
            } else if (mainSoundResId != -1) {
                mainPlayer = MediaPlayer.create(this, mainSoundResId);
                mainPlayer.setLooping(true);
                mainPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    isPlaying = true;
                    btnPlayPause.setImageResource(R.drawable.stop);
                });
            } else {
                Toast.makeText(this, "❌ Không có âm thanh nào để phát", Toast.LENGTH_SHORT).show();
            }
        } else if (mainPlayer != null) {
            mainPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.stop);
        }
    }

    private void pauseMainSound() {
        if (mainPlayer != null) {
            if (mainPlayer.isPlaying()) mainPlayer.pause();
            mainPlayer.stop();
            mainPlayer.release();
            mainPlayer = null;
        }
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.play);
    }

    final ActivityResultLauncher<Intent> customSoundLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();

                    long soundId = data.getLongExtra("soundId", -1);
                    String name = data.getStringExtra("name");
                    String fileUrl = data.getStringExtra("fileUrl");
                    String imageUrl = data.getStringExtra("imageUrl");

                    Log.d("CategoryResult", "soundId=" + soundId + ", fileUrl=" + fileUrl);

                    if (soundId != -1 && fileUrl != null) {
                        LayerSound newLayer = new LayerSound(
                                0,
                                name,
                                0,
                                fileUrl,
                                0.5f,
                                imageUrl
                        );
                        newLayer.setId(soundId);
                        layers.add(newLayer);
                        layerAdapter.notifyItemInserted(layers.size() - 1);
                    } else {
                        Toast.makeText(this, "❌ Âm thanh không hợp lệ.", Toast.LENGTH_SHORT).show();
                    }
                }
            });



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainPlayer != null) {
            mainPlayer.stop();
            mainPlayer.release();
        }
        if (layerAdapter != null) {
            layerAdapter.releaseAllPlayers();
        }
    }
}
