package com.example.chillmusic.ui.player.Rain;

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
import com.example.chillmusic.models.SoundDto;
import com.example.chillmusic.ui.custom.CustomSoundPickerActivity;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class RainActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_rain);

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
                        db.insertSound(layer.getName(), layer.getIconResId(), layer.getSoundResId(), "rain");
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
            List<LayerSound> savedLayers = db.getAllSavedSounds("rain");

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
            mainPlayer = MediaPlayer.create(this, R.raw.main_rain);
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
                String imageUrl = data.getStringExtra("imageUrl");
                String name = data.getStringExtra("name");
                int icon = data.getIntExtra("iconResId", 0);
                int soundResId = data.getIntExtra("soundResId", 0);

                if (name == null || icon == 0) {
                    Log.d("OceanActivity", "⚠️ Incomplete sound data received. Skipping layer creation.");
                    return;
                }

                // Nếu là âm thanh local chưa có fileUrl → convert và upload trước
                if ((fileUrl == null || fileUrl.isEmpty()) && soundResId > 0) {
                    File localFile = convertRawToFile(soundResId, name + ".mp3");
                    if (localFile != null && localFile.exists()) {
                        uploadSoundToApi(localFile, name, uploadedDto -> {
                            runOnUiThread(() -> {
                                LayerSound newLayer = new LayerSound(
                                        icon,
                                        uploadedDto.getName(),
                                        -1,
                                        "http://10.0.2.2:5000" + uploadedDto.getFileUrl(),
                                        0.1f,
                                        "http://10.0.2.2:5000" + uploadedDto.getImageUrl()
                                );
                                newLayer.setId(uploadedDto.getId());

                                MediaPlayer player = createMediaPlayerFromUrl("http://10.0.2.2:5000" + uploadedDto.getFileUrl());
                                newLayer.setMediaPlayer(player);

                                LayerAdapter.addLayer(newLayer, player);
                                Log.d("OceanActivity", "✅ Uploaded & added new layer (local): " + uploadedDto.getName());
                            });
                        });
                    } else {
                        Log.e("OceanActivity", "❌ Failed to convert raw resource to file");
                    }
                    return;
                }

                // Nếu là online sound hoặc local đã được upload → sử dụng trực tiếp
                LayerSound newLayer = new LayerSound(icon, name, soundResId, fileUrl, 0.1f, null);
                newLayer.setId(soundId);

                MediaPlayer player;
                if (fileUrl != null && !fileUrl.isEmpty()) {
                    player = createMediaPlayerFromUrl(fileUrl);
                } else if (soundResId != 0) {
                    player = MediaPlayer.create(this, soundResId);
                } else {
                    Log.e("OceanActivity", "❌ Invalid sound: no fileUrl or raw resource");
                    return; // Dừng nếu cả 2 đều null
                }

                newLayer.setMediaPlayer(player);
                LayerAdapter.addLayer(newLayer, player);
                Log.d("OceanActivity", "✅ Added new layer: " + name + " (soundId: " + soundId + ", mixId: " + mixId + ")");
            }
    );

    private void uploadSoundToApi(File file, String name, Consumer<SoundDto> onSuccess) {
        new Thread(() -> {
            try {
                String boundary = "===" + System.currentTimeMillis() + "===";
                URL url = new URL("http://10.0.2.2:5000/api/Sound/Upload"); // Đổi nếu URL khác
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setDoOutput(true);

                DataOutputStream output = new DataOutputStream(connection.getOutputStream());

                // Field: name
                output.writeBytes("--" + boundary + "\r\n");
                output.writeBytes("Content-Disposition: form-data; name=\"name\"\r\n\r\n");
                output.writeBytes(name + "\r\n");

                // Field: file
                output.writeBytes("--" + boundary + "\r\n");
                output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
                output.writeBytes("Content-Type: audio/mpeg\r\n\r\n");

                FileInputStream fileInput = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.writeBytes("\r\n");
                fileInput.close();

                // Kết thúc multipart
                output.writeBytes("--" + boundary + "--\r\n");
                output.flush();
                output.close();

                // Nhận phản hồi
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Giải mã JSON về SoundDto (giả định dùng Gson)
                    SoundDto dto = new Gson().fromJson(response.toString(), SoundDto.class);
                    onSuccess.accept(dto);
                } else {
                    Log.e("OceanActivity", "❌ Upload failed. Response code: " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("OceanActivity", "❌ Upload exception: ", e);
            }
        }).start();
    }


    private File convertRawToFile(int resId, String fileName) {
        try {
            InputStream inputStream = getResources().openRawResource(resId);
            File outFile = new File(getCacheDir(), fileName);

            FileOutputStream outputStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            Log.d("OceanActivity", "📁 File created: " + outFile.getAbsolutePath());
            return outFile;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("OceanActivity", "❌ Error converting raw to file", e);
            return null;
        }
    }


}