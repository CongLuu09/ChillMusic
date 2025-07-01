package com.example.chillmusic.ui.custom;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.adapter.CustomAdapter;
import com.example.chillmusic.models.CustomSound;
import com.example.chillmusic.models.CustomSoundGroup;
import com.example.chillmusic.models.SoundDto;
import com.example.chillmusic.service.ApiService;
import com.example.chillmusic.service.RetrofitClient;
import com.example.chillmusic.service.SoundResponse;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomFragment extends Fragment {

    private static final String TAG = "CustomFragment";
    private static final String IMAGE_HOST = "https://sleepchills.kenhtao.site/storage/";

    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;

    private final Map<Long, MediaPlayer> playingSounds = new HashMap<>();
    private final Set<Long> activeSoundIds = new HashSet<>();
    private final Map<Long, Float> volumeLevels = new HashMap<>();
    private final List<SoundDto> onlineSounds = new ArrayList<>();

    // ==================== Lifecycle ====================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewCustom);
        setupRecyclerView();
        fetchOnlineSounds();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        for (MediaPlayer player : playingSounds.values()) {
            if (player != null && player.isPlaying()) {
                player.pause();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (MediaPlayer player : playingSounds.values()) {
            if (player != null) {
                try {
                    if (player.isPlaying()) player.stop();
                } catch (IllegalStateException ignored) {}
                player.release();
            }
        }
        playingSounds.clear();
        activeSoundIds.clear();
        volumeLevels.clear();
    }

    // ==================== Setup RecyclerView ====================
    private void setupRecyclerView() {
        customAdapter = new CustomAdapter(new ArrayList<>(), getContext(), new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CustomSound sound) {
                handleSoundClick(sound);
            }

            @Override
            public void onVolumeChange(CustomSound sound, float volume) {
                updateSoundVolume(sound, volume);
            }

            @Override
            public float getSoundVolume(CustomSound sound) {
                return volumeLevels.getOrDefault(sound.getId(), 1.0f);
            }

            @Override
            public boolean isSoundActive(CustomSound sound) {
                return activeSoundIds.contains(sound.getId());
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return customAdapter.getItemViewType(position) == CustomAdapter.TYPE_GROUP ? 3 : 1;
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(customAdapter);
    }

    // ==================== API Call ====================
    private void fetchOnlineSounds() {
        ApiService api = RetrofitClient.getApiService();
        api.getAllSounds().enqueue(new Callback<SoundResponse>() {
            @Override
            public void onResponse(Call<SoundResponse> call, Response<SoundResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<SoundDto> sounds = response.body().getData().getSounds();
                    onlineSounds.clear();
                    onlineSounds.addAll(sounds);
                    updateSoundItems();
                } else {
                    Log.e(TAG, "❌ API trả về lỗi hoặc dữ liệu null");
                }
            }

            @Override
            public void onFailure(Call<SoundResponse> call, Throwable t) {
                Log.e(TAG, "❌ Không thể gọi API: " + t.getMessage(), t);
            }
        });
    }

    // ==================== UI Update ====================
    private void updateSoundItems() {
        Map<String, List<CustomSound>> groupedMap = new LinkedHashMap<>();

        for (SoundDto dto : onlineSounds) {
            String soundUrl = dto.getLinkMusic();
            String imageUrl = dto.getAvatar();

            if (imageUrl != null && !imageUrl.startsWith("http")) {
                imageUrl = IMAGE_HOST + imageUrl.replaceFirst("^/", "");
            }

            CustomSound sound = new CustomSound(
                    dto.getId(),
                    dto.getTitle(),
                    soundUrl,
                    imageUrl
            );

            String category = dto.getCategory() != null ? dto.getCategory() : "Others";
            groupedMap.computeIfAbsent(category, k -> new ArrayList<>()).add(sound);
        }

        List<CustomSoundGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<CustomSound>> entry : groupedMap.entrySet()) {
            groups.add(new CustomSoundGroup(entry.getKey(), entry.getValue()));
        }

        if (isAdded()) {
            requireActivity().runOnUiThread(() -> customAdapter.setData(groups));
        }
    }

    // ==================== MediaPlayer Handling ====================
    private void handleSoundClick(CustomSound sound) {
        long id = sound.getId();
        String url = sound.getFileUrl();
        if (url == null) return;

        if (playingSounds.containsKey(id)) {
            MediaPlayer player = playingSounds.get(id);
            if (player != null) {
                try {
                    if (player.isPlaying()) player.stop();
                } catch (IllegalStateException ignored) {}
                player.release();
            }
            playingSounds.remove(id);
            activeSoundIds.remove(id);
        } else {
            try {
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(url);
                player.setLooping(true);
                float volume = volumeLevels.getOrDefault(id, 1.0f);
                player.setVolume(volume, volume);
                player.prepare();
                player.start();
                playingSounds.put(id, player);
                activeSoundIds.add(id);
            } catch (Exception e) {
                Log.e(TAG, "❌ Lỗi phát âm thanh: " + url, e);
            }
        }

        customAdapter.notifyDataSetChanged();
    }

    private void updateSoundVolume(CustomSound sound, float volume) {
        long id = sound.getId();
        volumeLevels.put(id, volume);
        MediaPlayer player = playingSounds.get(id);
        if (player != null) {
            player.setVolume(volume, volume);
        }
    }

    // ==================== Public Utilities ====================
    public void playSoundByName(String name) {
        SoundDto dto = findSoundByName(name);
        if (dto != null) {
            CustomSound sound = new CustomSound(
                    dto.getId(),
                    dto.getTitle(),
                    dto.getLinkMusic(),
                    dto.getAvatar()
            );
            handleSoundClick(sound);
        } else {
            Log.w(TAG, "⚠ Không tìm thấy sound có tên: " + name);
        }
    }

    private SoundDto findSoundByName(String name) {
        for (SoundDto dto : onlineSounds) {
            if (dto.getTitle() != null && dto.getTitle().equalsIgnoreCase(name)) {
                return dto;
            }
        }
        return null;
    }
}
