package com.example.chillmusic.ui.player.Forest;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.Timer.TimerCallBack;
import com.example.chillmusic.Timer.TimerDialog;
import com.example.chillmusic.Timer.TimerViewModel;
import com.example.chillmusic.adapter.PlayLayerAdapter;
import com.example.chillmusic.data.db.AppDatabase;
import com.example.chillmusic.models.LayerSound;
import com.example.chillmusic.ui.custom.CustomSoundPickerActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ForestActivity extends AppCompatActivity {
    private ImageView btnBack, btnPlayPause, btnAddLayer, btnSaveSound;
    private TextView tvTitle, tvTimer;
    private RecyclerView recyclerViewLayers;
    private boolean isPlaying = false;
    private MediaPlayer mainPlayer;
    private final List<LayerSound> layers = new ArrayList<>();
    private PlayLayerAdapter LayerAdapter;

    private TimerViewModel timerViewModel;
    private final Handler timerHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forest);

        btnBack = findViewById(R.id.btnBack);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnSaveSound = findViewById(R.id.btnSaveSound);
        btnAddLayer = findViewById(R.id.btnAddLayer);
        tvTitle = findViewById(R.id.tvTitle);
        tvTimer = findViewById(R.id.tvTimer);
        recyclerViewLayers = findViewById(R.id.recyclerViewLayers);

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        btnSaveSound.setOnClickListener(v -> {
            if (!layers.isEmpty()) {
                AppDatabase db = new AppDatabase(this);

                new Thread(() -> {
            for (LayerSound layer : layers) {
                        db.insertSound(layer.getName(), layer.getIconResId(), layer.getSoundResId(), "forest");
                        Log.d("OceanActivity", "✅ Saved sound to DB: " + layer.getName());
            }
                }).start();
                    } else {
                Log.d("OceanActivity", "⚠️ No layers to save.");
                }
        });

        setupListeners();
        setupLayerSounds();
        setupTimerObserver();
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
                if (LayerAdapter != null) LayerAdapter.releaseAllPlayers();
            }
        });
    }

    private void setupLayerSounds() {
        LayerAdapter = new PlayLayerAdapter(this, layers);
        recyclerViewLayers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLayers.setAdapter(LayerAdapter);
        loadSavedSoundsFromDb();
    }

    private void loadSavedSoundsFromDb() {
        new Thread(() -> {
            AppDatabase db = new AppDatabase(this);
            List<LayerSound> savedLayers = db.getAllSavedSounds("forest");

            for (LayerSound l : savedLayers) {
                MediaPlayer player = MediaPlayer.create(this, l.getSoundResId());
                player.setLooping(true);
                l.setMediaPlayer(player);
            }

            runOnUiThread(() -> {
                layers.clear();
                layers.addAll(savedLayers);
                LayerAdapter.notifyDataSetChanged();
                Log.d("OceanActivity", "✅ Loaded " + savedLayers.size() + " saved sounds from DB");
            });
        }).start();
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
            if (!layers.isEmpty()) {
                String currentSoundName = layers.get(layers.size() - 1).getName();
                intent.putExtra("selectedName", currentSoundName);
            }
            customSoundLauncher.launch(intent);
        });

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
                        if (LayerAdapter != null) LayerAdapter.releaseAllPlayers();
                    });
                }
            });
            dialog.show(getSupportFragmentManager(), "TimerDialog");
        });
    }

    private void playMainSound() {
        if (mainPlayer == null) {
            mainPlayer = MediaPlayer.create(this, R.raw.forest);
            mainPlayer.setLooping(true);
        }
        mainPlayer.start();
        isPlaying = true;
        btnPlayPause.setImageResource(R.drawable.ic_play);
    }

    private void pauseMainSound() {
        if (mainPlayer != null) {
            if (mainPlayer.isPlaying()) {
                mainPlayer.pause();
            }
            mainPlayer.stop();
            mainPlayer.release();
            mainPlayer = null;
        }
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_pause);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainPlayer != null) {
            mainPlayer.stop();
            mainPlayer.release();
        }
        if (LayerAdapter != null) {
            LayerAdapter.releaseAllPlayers();
        }
    }
    private MediaPlayer createMediaPlayerFromUrl(String url) {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(url);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setLooping(true);
            player.setOnPreparedListener(MediaPlayer::start);
            player.prepareAsync();
        } catch (IOException e) {
            Log.e("OceanActivity", "❌ Failed to load sound from URL: " + url, e);
            return null;
        }
        return player;
    }

    private final ActivityResultLauncher<Intent> customSoundLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    Log.d("OceanActivity", "❌ No data returned from CustomSoundPickerActivity");
                    return;
                }

                Intent data = result.getData();

                boolean remove = data.getBooleanExtra("remove", false);
                if (remove && !layers.isEmpty()) {
                    LayerSound last = layers.get(layers.size() - 1);
                    LayerAdapter.removeLayer(last);
                    Log.d("OceanActivity", "🗑 Removed last layer: " + last.getName());
                    return;
                }

                long soundId = data.getLongExtra("soundId", -1);
                long mixId = data.getLongExtra("mixId", -1);
                String fileUrl = data.getStringExtra("fileUrl");
                String name = data.getStringExtra("name");
                int icon = data.getIntExtra("iconResId", 0);
                int sound = data.getIntExtra("soundResId", 0);

                if (name == null || icon == 0) {
                    Log.d("OceanActivity", "⚠️ Incomplete sound data received. Skipping layer creation.");
                    return;
                }

                LayerSound newLayer = new LayerSound(icon, name, sound, fileUrl, 0.1f, null);
                newLayer.setId(soundId);
                MediaPlayer player = (fileUrl != null && !fileUrl.isEmpty()) ?
                        createMediaPlayerFromUrl(fileUrl) : MediaPlayer.create(this, sound);

                newLayer.setMediaPlayer(player);

                LayerAdapter.addLayer(newLayer, player);
                Log.d("OceanActivity", "✅ Added new layer: " + name + " (soundId: " + soundId + ", mixId: " + mixId + ")");
            }
    );
    }