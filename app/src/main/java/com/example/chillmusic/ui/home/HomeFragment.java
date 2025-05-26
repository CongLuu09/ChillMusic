package com.example.chillmusic.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chillmusic.R;
import com.example.chillmusic.adapter.SoundAdapter;
import com.example.chillmusic.models.Sound;
import com.example.chillmusic.ui.player.AirTravel.AirTravelActivity;
import com.example.chillmusic.ui.player.Cafe.CafeChillActivity;
import com.example.chillmusic.ui.player.Desert.DesertActivity;
import com.example.chillmusic.ui.player.Farm.FarmActivity;
import com.example.chillmusic.ui.player.Fire.FireActivity;
import com.example.chillmusic.ui.player.Forest.ForestActivity;
import com.example.chillmusic.ui.player.Lake.LakeActivity;
import com.example.chillmusic.ui.player.Night.NightActivity;
import com.example.chillmusic.ui.player.OceanActivity;
import com.example.chillmusic.ui.player.Rain.RainActivity;
import com.example.chillmusic.ui.player.Train.TrainJourneyActivity;
import com.example.chillmusic.ui.player.Underwater.UnderwaterActivity;
import com.example.chillmusic.ui.player.WaterFall.WaterfallActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private SoundAdapter soundAdapter;

    private final Map<String, Class<?>> soundActivityMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.rv_sounds);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        initSoundActivityMap();

        soundAdapter = new SoundAdapter(getSoundList(), sound -> {
            if (sound.getTitle().equals("Ocean")) {
                startActivity(new Intent(getContext(), OceanActivity.class));
            } else if (sound.getTitle().equals("Forest")) {
                startActivity(new Intent(getContext(), ForestActivity.class));
            } else if (sound.getTitle().equals("Rain")) {
                startActivity(new Intent(getContext(), RainActivity.class));
            } else if (sound.getTitle().equals("Night")) {
                startActivity(new Intent(getContext(), NightActivity.class));
            } else if (sound.getTitle().equals("Fire")) {
                startActivity(new Intent(getContext(), FireActivity.class));
            } else if (sound.getTitle().equals("Lake")) {
                startActivity(new Intent(getContext(), LakeActivity.class));
            } else if (sound.getTitle().equals("Farm")) {
                startActivity(new Intent(getContext(), FarmActivity.class));
            } else if (sound.getTitle().equals("Waterfall")) {
                startActivity(new Intent(getContext(), WaterfallActivity.class));
            } else if (sound.getTitle().equals("Underwater")) {
                startActivity(new Intent(getContext(), UnderwaterActivity.class));
            } else if (sound.getTitle().equals("Desert")) {
                startActivity(new Intent(getContext(), DesertActivity.class));
            } else if (sound.getTitle().equals("Train Journey")) {
                startActivity(new Intent(getContext(), TrainJourneyActivity.class));
            } else if (sound.getTitle().equals("Air Travel")) {
                startActivity(new Intent(getContext(), AirTravelActivity.class));
            } else if (sound.getTitle().equals("Cafe & Chill")) {
                startActivity(new Intent(getContext(), CafeChillActivity.class));
            } else {
                Toast.makeText(getContext(), "Clicked: " + sound.getImageResId(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(soundAdapter);
        return view;
    }

    private void initSoundActivityMap() {

    }

    private List<Sound> getSoundList() {
        List<Sound> list = new ArrayList<>();
        list.add(new Sound( R.drawable.sound_ocean, "Ocean", R.raw.ocean_main));
        list.add(new Sound( R.drawable.sound_forest, "Forest", R.raw.forest));
        list.add(new Sound( R.drawable.sound_rain, "Rain", R.raw.light_rain));
        list.add(new Sound( R.drawable.sound_night, "Night", R.raw.main_night));
        list.add(new Sound( R.drawable.sound_fire, "Fire", R.raw.fire));
        list.add(new Sound( R.drawable.sound_lake, "Lake", R.raw.lake));
        list.add(new Sound( R.drawable.sound_farm, "Farm", R.raw.farm));
        list.add(new Sound( R.drawable.sound_waterfall, "Waterfall", R.raw.waterfall));
        list.add(new Sound( R.drawable.sound_underwater, "Underwater", R.raw.underwater));
        list.add(new Sound( R.drawable.sound_desert, "Desert", R.raw.desert));
        list.add(new Sound( R.drawable.sound_train, "Train Journey", R.raw.train));
        list.add(new Sound( R.drawable.sound_airplane, "Air Travel", R.raw.airplane));
        list.add(new Sound( R.drawable.sound_cafe, "Cafe & Chill", R.raw.cafe));
        return list;
    }
}
