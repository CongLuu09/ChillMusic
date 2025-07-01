package com.example.chillmusic.ui.player;

import android.content.Intent;
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
import com.example.chillmusic.service.ApiResponse;
import com.example.chillmusic.service.ApiService;
import com.example.chillmusic.service.RetrofitClient;
import com.example.chillmusic.service.SoundResponse;
import com.example.chillmusic.ui.custom.CustomSoundPickerActivity;
import com.example.chillmusic.utils.DeviceUtils;

import java.io.IOException;
import java.util.*;
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
    private long categoryId;
    private String categoryTitle;
    private String categoryImageUrl;
    private int mainSoundResId = -1;

    private static final Map<String, Integer> categorySoundMap = new HashMap<>();
    static {
        categorySoundMap.put("Ocean", R.raw.ocean_main);
        categorySoundMap.put("Forest", R.raw.forest);
        categorySoundMap.put("Rain", R.raw.rain_forest);
        categorySoundMap.put("Desert", R.raw.desert);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_player);

        initViews();
        getIntentData();
        setupBackgroundImage();
        setupMediaPlayerRes();

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        setupListeners();
        setupTimerObserver();
        setupLayerSounds();
        loadSoundsFromApi();
        loadSavedMixIfExists();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnSaveSound = findViewById(R.id.btnSaveSound);
        btnAddLayer = findViewById(R.id.btnAddLayer);
        tvTitle = findViewById(R.id.tvTitle);
        tvTimer = findViewById(R.id.tvTimer);
        recyclerViewLayers = findViewById(R.id.recyclerViewLayers);
        imgBackground = findViewById(R.id.imgBackground);
    }

    private void getIntentData() {
        categoryId = getIntent().getLongExtra("categoryId", -1);
        categoryTitle = getIntent().getStringExtra("categoryTitle");
        categoryImageUrl = getIntent().getStringExtra("avatar");
        tvTitle.setText(categoryTitle);
    }

    private void setupBackgroundImage() {
        if (categoryImageUrl != null && !categoryImageUrl.isEmpty()) {

            if (!categoryImageUrl.startsWith("http")) {
                categoryImageUrl = "https://sleepchills.kenhtao.site/storage/" + categoryImageUrl.replaceFirst("^/+", "");
            }

            Log.d("GLIDE", "Image loading from: " + categoryImageUrl);

            Glide.with(this)
                    .load(categoryImageUrl)
                    .placeholder(R.drawable.backocean)
                    .error(R.drawable.backocean)
                    .into(imgBackground);
        }
    }





    private void setupMediaPlayerRes() {
        if (categoryTitle != null && categorySoundMap.containsKey(categoryTitle)) {
            mainSoundResId = categorySoundMap.get(categoryTitle);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) pauseMainSound(); else playMainSound();
        });
        btnAddLayer.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomSoundPickerActivity.class);
            customSoundLauncher.launch(intent);
        });
        btnSaveSound.setOnClickListener(v -> saveMixToApi());
        tvTimer.setOnClickListener(v -> {
            TimerDialog dialog = TimerDialog.newInstance();
            dialog.setCallback(new TimerCallBack() {
                public void onTimerSet(long durationMillis) { timerViewModel.startTimer(durationMillis); }
                public void onTimerCancelled() { timerViewModel.cancelTimer(); }
                public void onTimerFinished() {
                    pauseMainSound();
                    if (layerAdapter != null) layerAdapter.releaseAllPlayers();
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
            } else tvTimer.setText("Timer");
        });
    }

    private void setupLayerSounds() {
        layerAdapter = new PlayLayerAdapter(this, layers);
        recyclerViewLayers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLayers.setAdapter(layerAdapter);
    }

    private void loadSoundsFromApi() {
        ApiService api = RetrofitClient.getApiService();
        api.getSoundsByCategoryId((int) categoryId).enqueue(new Callback<ApiResponse<List<SoundDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SoundDto>>> call, Response<ApiResponse<List<SoundDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    List<SoundDto> soundList = response.body().getData();
                    List<LayerSound> loaded = new ArrayList<>();

                    for (SoundDto dto : soundList) {
                        loaded.add(new LayerSound(
                                dto.getId(),
                                dto.getTitle(),
                                dto.getLinkMusic(),
                                dto.getAvatar(),
                                0.1f
                        ));
                    }

                    layers.clear();
                    layers.addAll(loaded);
                    layerAdapter.notifyDataSetChanged();
                } else {
                    Log.e("CategoryPlayer", "❌ API failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SoundDto>>> call, Throwable t) {
                Log.e("CategoryPlayer", "❌ Failed to load sounds", t);
            }
        });

    }



    private void saveMixToApi() {
        List<Long> soundIds = new ArrayList<>();
        for (LayerSound layer : layers)
            if (layer.getId() != -1) soundIds.add(layer.getId());

        if (soundIds.isEmpty()) {
            Toast.makeText(this, "Không có âm thanh hợp lệ để lưu.", Toast.LENGTH_SHORT).show();
            return;
        }

        String mixName = categoryTitle + "_Mix_" + System.currentTimeMillis();
        MixCreateRequest request = new MixCreateRequest(DeviceUtils.getDeviceId(this), mixName, soundIds);

        RetrofitClient.getApiService().createMix(request).enqueue(new Callback<MixDto>() {
            public void onResponse(Call<MixDto> call, Response<MixDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CategoryPlayerActivity.this, "✅ Mix đã lưu thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CategoryPlayerActivity.this, "❌ Không lưu được mix (API lỗi)", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<MixDto> call, Throwable t) {
                Toast.makeText(CategoryPlayerActivity.this, "❌ Kết nối API thất bại khi lưu mix", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadSavedMixIfExists() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        RetrofitClient.getApiService().getMixesByDevice(deviceId).enqueue(new Callback<List<MixDto>>() {
            public void onResponse(Call<List<MixDto>> call, Response<List<MixDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String prefix = categoryTitle + "_Mix_";
                    List<MixDto> matching = new ArrayList<>();
                    for (MixDto mix : response.body()) if (mix.getName().startsWith(prefix)) matching.add(mix);
                    if (!matching.isEmpty()) {
                        Collections.sort(matching, (a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                        loadSoundsByIds(matching.get(0).getSoundIds(), layerSounds -> {
                            layers.clear();
                            layers.addAll(layerSounds);
                            layerAdapter.notifyDataSetChanged();
                        });
                    }
                }
            }
            public void onFailure(Call<List<MixDto>> call, Throwable t) {
                Log.e("CategoryMix", "❌ Failed to load mixes", t);
            }
        });
    }

    private void loadSoundsByIds(List<Integer> ids, Consumer<List<LayerSound>> callback) {
        if (ids == null || ids.isEmpty()) {
            callback.accept(new ArrayList<>());
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            sb.append(ids.get(i));
            if (i < ids.size() - 1) sb.append(",");
        }
        RetrofitClient.getApiService().getSoundsByIds(sb.toString()).enqueue(new Callback<List<SoundDto>>() {
            public void onResponse(Call<List<SoundDto>> call, Response<List<SoundDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LayerSound> list = new ArrayList<>();
                    for (SoundDto dto : response.body()) {
                        list.add(new LayerSound(
                                dto.getId(),
                                dto.getTitle(), // ✅ Dùng đúng getter theo model
                                dto.getLinkMusic(), // ✅ Nếu LayerSound cần URL file nhạc
                                dto.getAvatar(),     // ✅ Nếu LayerSound cần ảnh đại diện
                                0.1f
                        ));
                    }

                    callback.accept(list);
                } else callback.accept(new ArrayList<>());
            }
            public void onFailure(Call<List<SoundDto>> call, Throwable t) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    private void playMainSound() {
        if (mainPlayer == null) {
            if (!layers.isEmpty()) {
                String url = layers.get(0).getFileUrl();
                Log.d("MainSound", "▶️ Playing from URL: " + url);
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
                    mainPlayer.setOnErrorListener((mp, what, extra) -> {
                        Toast.makeText(this, "❌ Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
                        return true;
                    });
                    mainPlayer.prepareAsync();
                } catch (IOException e) {
                    Log.e("CategoryPlayer", "❌ MediaPlayer error", e);
                }
            } else if (mainSoundResId != -1) {
                mainPlayer = MediaPlayer.create(this, mainSoundResId);
                mainPlayer.setLooping(true);
                mainPlayer.start();
                isPlaying = true;
                btnPlayPause.setImageResource(R.drawable.stop);
            }
        } else {
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

    private String ensureFullUrl(String url) {
        if (url == null || url.isEmpty()) return "";
        return url.startsWith("http") ? url : "https://sleepchills.kenhtao.site/" + url.replaceFirst("^/+", "");
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
                    if (soundId != -1 && fileUrl != null) {
                        LayerSound newLayer = new LayerSound(soundId, name, fileUrl, imageUrl, 0.5f);

                        layers.add(newLayer);
                        layerAdapter.notifyItemInserted(layers.size() - 1);
                    } else {
                        Toast.makeText(this, "❌ Âm thanh không hợp lệ.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainPlayer != null) {
            mainPlayer.stop();
            mainPlayer.release();
        }
        if (layerAdapter != null) layerAdapter.releaseAllPlayers();
    }
}
