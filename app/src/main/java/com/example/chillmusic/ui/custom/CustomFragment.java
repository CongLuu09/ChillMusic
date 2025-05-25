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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomFragment extends Fragment {
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private final Map<Integer, MediaPlayer> playingSounds = new HashMap<>();
    private final Set<Integer> activeSoundIds = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewCustom);

        customAdapter = new CustomAdapter(getCustomSoundList(), getContext(), new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CustomSound sound) {
                handleSoundClick(sound);
            }
            @Override
            public void onVolumeChange(CustomSound sound, float volume) {
                updateSoundVolume(sound, volume);
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
        return view;
    }

    private void handleSoundClick(CustomSound sound) {
        int resId = sound.getSoundResId();
        if (playingSounds.containsKey(resId)) {
            MediaPlayer player = playingSounds.get(resId);
            if (player != null) {
                player.stop();
                player.release();
            }
            playingSounds.remove(resId);
            activeSoundIds.remove(resId);
        } else {
            MediaPlayer player = MediaPlayer.create(getContext(), resId);
            player.setLooping(true);
            player.start();
            playingSounds.put(resId, player);
            activeSoundIds.add(resId);
        }
        customAdapter.setActiveSounds(activeSoundIds);
    }

    private void updateSoundVolume(CustomSound sound, float volume) {
        int resId = sound.getSoundResId();
        MediaPlayer player = MediaPlayer.create(getContext(), resId);
        if (player == null) {
            Log.e("CustomFragment", "MediaPlayer create failed for resId: " + resId);
        }
    }

    private List<CustomSoundGroup> getCustomSoundList() {
        List<CustomSoundGroup> groups = new ArrayList<>();

        List<CustomSound> rainSounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("Rain Sounds", rainSounds));
        rainSounds.add(new CustomSound(R.drawable.heavy_rain, R.raw.heavy_rain, "Heavy Rain"));
        rainSounds.add(new CustomSound(R.drawable.light_rain, R.raw.light_rain, "Light Rain"));
        rainSounds.add(new CustomSound(R.drawable.storm, R.raw.thunder, "Storm"));
        rainSounds.add(new CustomSound(R.drawable.rain_on_tent, R.raw.rain_tent, "Rain on Tent"));
        rainSounds.add(new CustomSound(R.drawable.rain_on_window, R.raw.rain_window, "Rain on Window"));
        rainSounds.add(new CustomSound(R.drawable.rain_in_forest, R.raw.rain_forest, "Rain in Forest"));

        List<CustomSound> natureSounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("Nature Sounds", natureSounds));
        natureSounds.add(new CustomSound(R.drawable.forest_night, R.raw.forest, "Forest Night"));
        natureSounds.add(new CustomSound(R.drawable.ocean_waves, R.raw.drip, "Ocean Waves"));
        natureSounds.add(new CustomSound(R.drawable.birds_singing, R.raw.bird, "Birds Singing"));
        natureSounds.add(new CustomSound(R.drawable.frog_croaking, R.raw.frog, "Frogs Croaking"));
        natureSounds.add(new CustomSound(R.drawable.crickets_chirping, R.raw.cricket, "Crickets Chirping"));
        natureSounds.add(new CustomSound(R.drawable.wind_blowing, R.raw.wind, "Wind Blowing"));
        natureSounds.add(new CustomSound(R.drawable.snow_falling, R.raw.snow, "Snow Falling"));
        natureSounds.add(new CustomSound(R.drawable.ocean_waves, R.raw.wave, "Ocean Waves"));

        List<CustomSound> fireSounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("Fire Sounds", fireSounds));
        fireSounds.add(new CustomSound(R.drawable.ic_fire, R.raw.fire, "Crackling Fire"));
        fireSounds.add(new CustomSound(R.drawable.ic_fireplace, R.raw.fireplace, "Fireplace Ambience"));

        List<CustomSound> citySounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("City Sounds", citySounds));
        citySounds.add(new CustomSound(R.drawable.ic_traffic, R.raw.traffic, "Traffic Noise"));
        citySounds.add(new CustomSound(R.drawable.ic_cafe, R.raw.cafe, "Cafe Background"));
        citySounds.add(new CustomSound(R.drawable.ic_train, R.raw.train, "Train Passing"));
        citySounds.add(new CustomSound(R.drawable.ic_airplane, R.raw.airplane, "Airplane Flying"));

        List<CustomSound> Cafeandchill = new ArrayList<>();
        groups.add(new CustomSoundGroup("Cafe & Chill", Cafeandchill));
        Cafeandchill.add(new CustomSound(R.drawable.ic_chill, R.raw.chill, "Cafe Music"));
        Cafeandchill.add(new CustomSound(R.drawable.ic_chill_ambience, R.raw.chill_ambience, "Chill Ambience"));
        Cafeandchill.add(new CustomSound(R.drawable.ic_chillnigh, R.raw.chillsound, "Chill Music"));
        Cafeandchill.add(new CustomSound(R.drawable.ic_chillrelax, R.raw.pianochill, "Chill Relax"));
        Cafeandchill.add(new CustomSound(R.drawable.ic_chillwork, R.raw.gitarchill, "Chill Music"));

        List<CustomSound> homeSounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("Home Sounds", homeSounds));
        homeSounds.add(new CustomSound(R.drawable.fan_blowing, R.raw.fan, "Fan Blowing"));
        homeSounds.add(new CustomSound(R.drawable.washing_machine, R.raw.washing_machine, "Washing Machine"));
        homeSounds.add(new CustomSound(R.drawable.fridge_humming, R.raw.fridge, "Fridge Humming"));
        homeSounds.add(new CustomSound(R.drawable.dishwasher, R.raw.bowl, "Dishwasher"));

        List<CustomSound> instrumentSounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("Instrument Sounds", instrumentSounds));
        instrumentSounds.add(new CustomSound(R.drawable.piano, R.raw.piano, "Piano"));
        instrumentSounds.add(new CustomSound(R.drawable.acoustic_guitar, R.raw.guitar, "Acoustic Guitar"));
        instrumentSounds.add(new CustomSound(R.drawable.violin, R.raw.violin, "Violin"));
        instrumentSounds.add(new CustomSound(R.drawable.flute, R.raw.flute, "Flute"));
        instrumentSounds.add(new CustomSound(R.drawable.drum, R.raw.drums, "Drum"));
        instrumentSounds.add(new CustomSound(R.drawable.saxophone, R.raw.saxophone, "Saxophone"));
        instrumentSounds.add(new CustomSound(R.drawable.harp, R.raw.harp, "Harp"));
        instrumentSounds.add(new CustomSound(R.drawable.clarinet, R.raw.clarinet, "Clarinet"));

        List<CustomSound> animalSounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("Animal Sounds", animalSounds));
        animalSounds.add(new CustomSound(R.drawable.dog_barking, R.raw.dog_bark, "Dog Barking"));
        animalSounds.add(new CustomSound(R.drawable.cat_meowing, R.raw.cat_purring, "Cat Meowing"));
        animalSounds.add(new CustomSound(R.drawable.bird_chirping, R.raw.bird, "Bird Chirping"));
        animalSounds.add(new CustomSound(R.drawable.horse_neigh, R.raw.horse, "Horse Neigh"));
        animalSounds.add(new CustomSound(R.drawable.cow_mooing, R.raw.cow, "Cow Mooing"));
        animalSounds.add(new CustomSound(R.drawable.frog_croaking, R.raw.frog, "Frog Croaking"));
        animalSounds.add(new CustomSound(R.drawable.duck_quack, R.raw.duck, "Duck Quack"));
        animalSounds.add(new CustomSound(R.drawable.crickets_chirping, R.raw.cricket, "Crickets Chirping"));

        return groups;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (MediaPlayer player : playingSounds.values()) {
            if (player != null) {
                player.stop();
                player.release();
            }
        }
        playingSounds.clear();
    }
}