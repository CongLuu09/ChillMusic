package com.example.chillmusic.ui.player;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.example.chillmusic.ui.custom.CustomSoundPickerActivity;
import com.example.chillmusic.utils.MixLocalManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_player);

        initViews();
        getIntentData();
        setupBackgroundImage();

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        setupListeners();
        setupTimerObserver();
        setupLayerSounds();
        loadSavedMixFromLocal();
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
        categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);
        categoryTitle = getIntent().getStringExtra("CATEGORY_TITLE");
        categoryImageUrl = getIntent().getStringExtra("avatar");

        Log.d("MixDebug", "Received CATEGORY_ID: " + categoryId);

        tvTitle.setText(categoryTitle);
    }

    private void setupBackgroundImage() {
        if (categoryImageUrl != null && !categoryImageUrl.isEmpty()) {
            if (!categoryImageUrl.startsWith("http")) {
                categoryImageUrl = "https://sleepchills.kenhtao.site/storage/" + categoryImageUrl.replaceFirst("^/+", "");
            }

            Glide.with(this)
                    .load(categoryImageUrl)
                    .placeholder(R.drawable.backocean)
                    .error(R.drawable.backocean)
                    .into(imgBackground);
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
        btnSaveSound.setOnClickListener(v -> saveMixToLocal());
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

    private void saveMixToLocal() {
        MixLocalManager.saveMixFull(this, categoryId, layers);
        Log.d("MixDebug", "✅ Saved local LayerSound mix: " + layers.size());
        Toast.makeText(this, "✅ Đã lưu bản mix vào thiết bị!", Toast.LENGTH_SHORT).show();
    }

    private void loadSavedMixFromLocal() {
        List<LayerSound> savedLayers = MixLocalManager.loadMixFull(this, categoryId);
        if (!savedLayers.isEmpty()) {
            Log.d("MixDebug", "✅ Loaded LayerSound count: " + savedLayers.size());
            layers.clear();
            layers.addAll(savedLayers);
            layerAdapter.notifyDataSetChanged();
        } else {
            Log.d("MixDebug", "⚠️ Không có dữ liệu local");
        }
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
