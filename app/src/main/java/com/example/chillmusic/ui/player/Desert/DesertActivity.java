package com.example.chillmusic.ui.player.Desert;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.chillmusic.models.MixCreateRequest;
import com.example.chillmusic.models.MixDto;
import com.example.chillmusic.models.SoundDto;
import com.example.chillmusic.service.ApiService;
import com.example.chillmusic.service.RetrofitClient;
import com.example.chillmusic.ui.custom.CustomSoundPickerActivity;
import com.example.chillmusic.ui.player.Underwater.UnderwaterActivity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DesertActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_desert);

        btnBack = findViewById(R.id.btnBack);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnSaveSound = findViewById(R.id.btnSaveSound);
        btnAddLayer = findViewById(R.id.btnAddLayer);
        tvTitle = findViewById(R.id.tvTitle);
        tvTimer = findViewById(R.id.tvTimer);
        recyclerViewLayers = findViewById(R.id.recyclerViewLayers);

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        btnSaveSound.setOnClickListener(v -> {

            List<Long> soundIds = new ArrayList<>();
            for (LayerSound layer : layers) {
                Log.d("OceanActivity", "Layer: " + layer.getName() + ", id=" + layer.getId());
                if (layer.getId() != -1) {
                    soundIds.add(layer.getId());
                }
            }


            if (soundIds.isEmpty()) {
                Log.d("OceanActivity", "⚠️ No valid sound IDs to save.");
                Toast.makeText(DesertActivity.this, "Không có âm thanh hợp lệ để lưu.", Toast.LENGTH_SHORT).show();
                return;
            }


            String mixName = "DesertMix_" + System.currentTimeMillis();


            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


            MixCreateRequest request = new MixCreateRequest(deviceId, mixName, soundIds);


            ApiService api = RetrofitClient.getApiService();
            api.createMix(request).enqueue(new Callback<MixDto>() {
                @Override
                public void onResponse(Call<MixDto> call, Response<MixDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MixDto savedMix = response.body();


                        new Thread(() -> {
                            AppDatabase db = new AppDatabase(DesertActivity.this);
                            db.insertMix(savedMix);
                        }).start();

                        runOnUiThread(() ->
                                Toast.makeText(DesertActivity.this, "Lưu thành công!", Toast.LENGTH_SHORT).show()
                        );
                    } else {

                    }
                }


                @Override
                public void onFailure(Call<MixDto> call, Throwable t) {
                    Log.e("OceanActivity", "❌ API error saving mix", t);
                    runOnUiThread(() ->
                            Toast.makeText(DesertActivity.this, "Lỗi khi lưu mix: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            });

        });

        setupListeners();
        setupLayerSounds();
        setupTimerObserver();
        loadSavedMixesFromApi();
    }


    private void loadSavedMixesFromApi() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        ApiService api = RetrofitClient.getApiService();

        api.getMixesByDevice(deviceId).enqueue(new Callback<List<MixDto>>() {
            @Override
            public void onResponse(Call<List<MixDto>> call, Response<List<MixDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MixDto> mixList = response.body();


                    String prefix = "DesertMix_";
                    List<MixDto> filteredMixes = new ArrayList<>();
                    for (MixDto mix : mixList) {
                        if (mix.getName() != null && mix.getName().startsWith(prefix)) {
                            filteredMixes.add(mix);
                        }
                    }


                    filteredMixes.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

                    runOnUiThread(() -> {
                        if (!filteredMixes.isEmpty()) {
                            Log.d("OceanActivity", "Filtered mixes count: " + filteredMixes.size());

                            MixDto newestMix = filteredMixes.get(0);


                            List<Long> soundIdsLong = new ArrayList<>();
                            for (Integer id : newestMix.getSoundIds()) {
                                soundIdsLong.add(id.longValue());
                            }

                            Log.d("OceanActivity", "Loading sounds by IDs: " + soundIdsLong);

                            loadSoundsByIds(soundIdsLong, layerSounds -> {
                                layers.clear();
                                layers.addAll(layerSounds);
                                LayerAdapter.notifyDataSetChanged();
                            });

                        } else {
                            Log.d("OceanActivity", "No mixes matching prefix " + prefix);
                        }
                    });

                } else {
                    Log.e("API", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MixDto>> call, Throwable t) {
                Log.e("API", "Call failed", t);
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

        Log.d("OceanActivity", "Loading sounds by IDs: " + commaSeparatedIds);

        ApiService api = RetrofitClient.getApiService();
        api.getSoundsByIds(commaSeparatedIds).enqueue(new Callback<List<SoundDto>>() {
            @Override
            public void onResponse(Call<List<SoundDto>> call, Response<List<SoundDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("OceanActivity", "Sounds returned: " + response.body().size());
                    List<LayerSound> layerSounds = new ArrayList<>();
                    String baseUrl = "http://10.0.2.2:3000/";

                    for (SoundDto dto : response.body()) {
                        Log.d("OceanActivity", "SoundDto loaded: " + dto.getName() + ", id: " + dto.getId());

                        String fullFileUrl = (dto.getFileUrl() != null && !dto.getFileUrl().isEmpty())
                                ? (dto.getFileUrl().startsWith("http") ? dto.getFileUrl() : baseUrl + dto.getFileUrl())
                                : null;

                        String fullImageUrl = (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty())
                                ? (dto.getImageUrl().startsWith("http") ? dto.getImageUrl() : baseUrl + dto.getImageUrl())
                                : null;

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

                    Log.d("OceanActivity", "LayerSounds count sent to adapter: " + layerSounds.size());
                    callback.accept(layerSounds);
                } else {
                    Log.e("OceanActivity", "Error loading sounds by IDs: " + response.code());
                    callback.accept(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<SoundDto>> call, Throwable t) {
                Log.e("OceanActivity", "Failed loading sounds by IDs", t);
                callback.accept(new ArrayList<>());
            }
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
            List<LayerSound> savedLayers = db.getAllSavedSounds("desert");

            for (LayerSound l : savedLayers) {
                MediaPlayer player;
                if (l.getFileUrl() != null && !l.getFileUrl().isEmpty()) {
                    player = createMediaPlayerFromUrl(l.getFileUrl());
                } else {
                    player = MediaPlayer.create(this, l.getSoundResId());
                }
                if (player != null) {
                    player.setLooping(true);
                    l.setMediaPlayer(player);
                }
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
            mainPlayer = MediaPlayer.create(this, R.raw.desert);
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

    final ActivityResultLauncher<Intent> customSoundLauncher = registerForActivityResult(
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
                int soundResId = data.getIntExtra("soundResId", 0);
                String imageUrl = data.getStringExtra("imageUrl");

                Log.d("OceanActivity", "Received sound data: name=" + name + ", icon=" + icon + ", fileUrl=" + fileUrl + ", imageUrl=" + imageUrl + ", soundResId=" + soundResId);


                if (name == null || (icon == 0 && (fileUrl == null || fileUrl.isEmpty()) && (imageUrl == null || imageUrl.isEmpty()))) {
                    Log.d("OceanActivity", "⚠️ Incomplete sound data received. Skipping layer creation.");
                    return;
                }


                LayerSound newLayer = new LayerSound(
                        icon,
                        name,
                        soundResId,
                        fileUrl,
                        0.1f,
                        imageUrl
                );

                newLayer.setId(soundId);

                MediaPlayer player;
                if (fileUrl != null && !fileUrl.isEmpty()) {
                    player = createMediaPlayerFromUrl(fileUrl);
                } else if (soundResId != 0) {
                    player = MediaPlayer.create(this, soundResId);
                } else {
                    player = null;
                    Log.e("OceanActivity", "❌ No valid sound source");
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
                URL url = new URL("http://10.0.2.2:3000/api/Sound/Upload");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setDoOutput(true);

                DataOutputStream output = new DataOutputStream(connection.getOutputStream());


                output.writeBytes("--" + boundary + "\r\n");
                output.writeBytes("Content-Disposition: form-data; name=\"name\"\r\n\r\n");
                output.writeBytes(name + "\r\n");


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


                output.writeBytes("--" + boundary + "--\r\n");
                output.flush();
                output.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    SoundDto dto = new Gson().fromJson(response.toString(), SoundDto.class);
                    onSuccess.accept(dto);
                } else {
                    Log.e("OceanActivity", "❌ Upload failed. Response code: " + responseCode);
                }

            } catch (Exception e) {
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
            Log.e("OceanActivity", "❌ Error converting raw to file", e);
            return null;
        }
    }
}