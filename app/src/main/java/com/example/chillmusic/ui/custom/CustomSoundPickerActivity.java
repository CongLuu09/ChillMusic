package com.example.chillmusic.ui.custom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.adapter.CustomSoundAdapter;
import com.example.chillmusic.models.CustomSound;
import com.example.chillmusic.models.CustomSoundGroup;
import com.example.chillmusic.models.SoundItem;

import java.util.ArrayList;
import java.util.List;

public class CustomSoundPickerActivity extends AppCompatActivity implements CustomSoundAdapter.OnSoundClickListener {

    private RecyclerView recyclerViewCustomSounds;
    private CustomSoundAdapter adapter;
    private ImageView btnClose;
    private TextView tvTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_sound_picker);

        recyclerViewCustomSounds = findViewById(R.id.recyclerViewCustomSounds);
        btnClose = findViewById(R.id.btnClose);
        tvTitle = findViewById(R.id.tvTitle);

        setupRecyclerView();
        setupListeners();
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new CustomSoundAdapter(this, getAllCustomSounds(), this);

        recyclerViewCustomSounds.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewCustomSounds.setAdapter(adapter);
    }

    private List<SoundItem> getAllCustomSounds() {
        List<SoundItem> allItems = new ArrayList<>();
        for (CustomSoundGroup group : getCustomSoundList()) {

            for (CustomSound sound : group.getSounds()) {
                allItems.add(new SoundItem(sound.getImageResId(), false, sound.getTitle(), sound.getSoundResId()));
            }
        }
        return allItems;
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
        fireSounds.add(new CustomSound(R.drawable.crackling_fire, R.raw.fire, "Crackling Fire"));
        fireSounds.add(new CustomSound(R.drawable.fireplace_ambience, R.raw.fireplace, "Fireplace Ambience"));

        List<CustomSound> citySounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("City Sounds", citySounds));
        citySounds.add(new CustomSound(R.drawable.traffic_noise, R.raw.traffic, "Traffic Noise"));
        citySounds.add(new CustomSound(R.drawable.cafe_background, R.raw.cafe, "Cafe Background"));
        citySounds.add(new CustomSound(R.drawable.train_passing, R.raw.train, "Train Passing"));
        citySounds.add(new CustomSound(R.drawable.airplane_flying, R.raw.airplane, "Airplane Flying"));

        List<CustomSound> Cafeandchill = new ArrayList<>();
        groups.add(new CustomSoundGroup("Cafe & Chill", Cafeandchill));
        Cafeandchill.add(new CustomSound(R.drawable.cafe_music, R.raw.chill, "Cafe Music"));
        Cafeandchill.add(new CustomSound(R.drawable.chill_ambience, R.raw.chill_ambience, "Chill Ambience"));
        Cafeandchill.add(new CustomSound(R.drawable.chillfeel, R.raw.chillsound, "Chill Music"));
        Cafeandchill.add(new CustomSound(R.drawable.cafe_ambience, R.raw.pianochill, "Chill Relax"));
        Cafeandchill.add(new CustomSound(R.drawable.cafe_background, R.raw.gitarchill, "Chill Music"));

        List<CustomSound> homeSounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("Home Sounds", homeSounds));
        homeSounds.add(new CustomSound(R.drawable.fan_blowing, R.raw.fan, "Fan Blowing"));
        homeSounds.add(new CustomSound(R.drawable.washing_machine, R.raw.washing_machine, "Washing Machine"));
        homeSounds.add(new CustomSound(R.drawable.fridge_humming, R.raw.fridge, "Fridge Humming"));
        homeSounds.add(new CustomSound(R.drawable.dishwasher, R.raw.bowl, "Dishwasher"));

        List<CustomSound> instrumentSounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("Instrument Sounds", instrumentSounds));
        instrumentSounds.add(new CustomSound(R.drawable.ic_piano, R.raw.piano, "Piano"));
        instrumentSounds.add(new CustomSound(R.drawable.ic_guitar, R.raw.guitar, "Acoustic Guitar"));
        instrumentSounds.add(new CustomSound(R.drawable.ic_violin, R.raw.violin, "Violin"));
        instrumentSounds.add(new CustomSound(R.drawable.ic_flute, R.raw.flute, "Flute"));
        instrumentSounds.add(new CustomSound(R.drawable.ic_drum, R.raw.drums, "Drum"));
        instrumentSounds.add(new CustomSound(R.drawable.ic_saxophone, R.raw.saxophone, "Saxophone"));
        instrumentSounds.add(new CustomSound(R.drawable.ic_harp, R.raw.harp, "Harp"));
        instrumentSounds.add(new CustomSound(R.drawable.ic_clarinet, R.raw.clarinet, "Clarinet"));

        List<CustomSound> animalSounds = new ArrayList<>();
        groups.add(new CustomSoundGroup("Animal Sounds", animalSounds));
        animalSounds.add(new CustomSound(R.drawable.ic_dog, R.raw.dog_bark, "Dog Barking"));
        animalSounds.add(new CustomSound(R.drawable.ic_cat, R.raw.cat_purring, "Cat Meowing"));
        animalSounds.add(new CustomSound(R.drawable.ic_bird, R.raw.bird, "Bird Chirping"));
        animalSounds.add(new CustomSound(R.drawable.ic_horse, R.raw.horse, "Horse Neigh"));
        animalSounds.add(new CustomSound(R.drawable.ic_cow, R.raw.cow, "Cow Mooing"));
        animalSounds.add(new CustomSound(R.drawable.ic_frog, R.raw.frog, "Frog Croaking"));
        animalSounds.add(new CustomSound(R.drawable.ic_duck, R.raw.duck, "Duck Quack"));
        animalSounds.add(new CustomSound(R.drawable.ic_cricket, R.raw.cricket, "Crickets Chirping"));


        return groups;
    }

    @Override
    public void onSoundClick(SoundItem item) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("name", item.getName());
        resultIntent.putExtra("iconResId", item.getIconResId());
        resultIntent.putExtra("soundResId", item.getSoundResId());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}