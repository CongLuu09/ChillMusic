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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomFragment extends Fragment {
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;


    private final Map<Long, MediaPlayer> playingSounds = new HashMap<>();
    private final Set<Long> activeSoundIds = new HashSet<>();
    private final Map<Long, Float> volumeLevels = new HashMap<>();
    private final List<SoundDto> onlineSounds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewCustom);

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
                Float v = volumeLevels.get(sound.getId());
                return v == null ? 1.0f : v;
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
                int type = customAdapter.getItemViewType(position);
                return type == CustomAdapter.TYPE_GROUP ? 3 : 1;
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(customAdapter);

        fetchOnlineSounds();
        return view;
    }

    private void fetchOnlineSounds() {
        ApiService api = RetrofitClient.getApiService();
        api.getAllSounds().enqueue(new Callback<List<SoundDto>>() {
            @Override
            public void onResponse(Call<List<SoundDto>> call, Response<List<SoundDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onlineSounds.clear();
                    onlineSounds.addAll(response.body());
                    updateSoundItems();
                }
            }

            @Override
            public void onFailure(Call<List<SoundDto>> call, Throwable t) {
                Log.e("CustomFragment", "❌ Failed to fetch sounds from server", t);
            }
        });
    }

    private void updateSoundItems() {
        List<CustomSoundGroup> groups = new ArrayList<>();
        Map<String, List<CustomSound>> groupedMap = new LinkedHashMap<>();
        String baseUrl = "http://192.168.1.7:3000/";

        for (SoundDto dto : onlineSounds) {
            String fullFileUrl = dto.getFileUrl() != null ? baseUrl + dto.getFileUrl().replaceFirst("/", "") : null;
            String fullImageUrl = dto.getImageUrl() != null ? baseUrl + dto.getImageUrl().replaceFirst("/", "") : null;


            CustomSound sound = new CustomSound(dto.getId(), dto.getName(), fullFileUrl, fullImageUrl);

            String category = dto.getCategory() != null ? dto.getCategory() : "Others";
            groupedMap.computeIfAbsent(category, k -> new ArrayList<>()).add(sound);
        }

        for (String groupName : groupedMap.keySet()) {
            groups.add(new CustomSoundGroup(groupName, groupedMap.get(groupName)));
        }

        requireActivity().runOnUiThread(() -> {
            customAdapter.setData(groups);
        });
    }

    private void handleSoundClick(CustomSound sound) {
        long id = sound.getId();
        String url = sound.getFileUrl();
        if (url == null) return;

        if (playingSounds.containsKey(id)) {
            MediaPlayer player = playingSounds.get(id);
            if (player != null) {
                try {
                    player.stop();
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
                Float v = volumeLevels.get(id);
                float volume = v == null ? 1.0f : v;
                player.setVolume(volume, volume);
                player.prepare();
                player.start();
                playingSounds.put(id, player);
                activeSoundIds.add(id);
            } catch (Exception e) {
                Log.e("CustomFragment", "❌ Error playing sound: " + url, e);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (MediaPlayer player : playingSounds.values()) {
            if (player != null) {
                try {
                    player.stop();
                } catch (IllegalStateException ignored) {}
                player.release();
            }
        }
        playingSounds.clear();
        activeSoundIds.clear();
        volumeLevels.clear();
    }
}