package com.example.chillmusic.ui.setting.SettingActicity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.adapter.MoreAppsAdapter;
import com.example.chillmusic.models.FreeApp;

import java.util.Arrays;
import java.util.List;

public class MoreFreeAppsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MoreAppsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_free_apps);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("More Free Apps");

        btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerMoreApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<FreeApp> apps = Arrays.asList(
                new FreeApp("Rainy Mood", "Relax with authentic rain ambiance for better sleep.", R.drawable.light_rain, "https://play.google.com/store/apps/details?id=com.example.rainymood"),
                new FreeApp("Ocean Waves", "Sleep to the calming sound of the sea.", R.drawable.ocean_waves, "https://play.google.com/store/apps/details?id=com.example.oceanwaves"),
                new FreeApp("Lullaby", "Baby lullaby, soft sleep music for infants.", R.drawable.ic_lullaby, "https://play.google.com/store/apps/details?id=com.example.lullaby"),
                new FreeApp("White Noise", "Block out distractions and focus or sleep deeply.", R.drawable.traffic_noise, "https://play.google.com/store/apps/details?id=com.example.whitenoise"),
                new FreeApp("Binaural Beats", "Improve focus, meditation, and sleep with brainwave audio.", R.drawable.ic_brainwave, "https://play.google.com/store/apps/details?id=com.example.binauralbeats"),
                new FreeApp("Sleepy Sounds", "Custom mixes of ambient sounds to help you sleep.", R.drawable.sound_clock, "https://play.google.com/store/apps/details?id=com.example.sleepysounds"),
                new FreeApp("Nature Ambience", "Experience forest, river, fire, and jungle sounds.", R.drawable.backcricket, "https://play.google.com/store/apps/details?id=com.example.natureambience"),
                new FreeApp("Meditation Music", "Guided and ambient meditation music tracks.", R.drawable.ic_meditation, "https://play.google.com/store/apps/details?id=com.example.meditationmusic"),
                new FreeApp("Relaxing Piano", "Soft piano melodies for rest and concentration.", R.drawable.ic_piano, "https://play.google.com/store/apps/details?id=com.example.relaxpiano"),
                new FreeApp("Focus Timer", "Use Pomodoro method with ambient background loops.", R.drawable.ic_timer, "https://play.google.com/store/apps/details?id=com.example.focustimer")
        );



        adapter = new MoreAppsAdapter(apps, this);
        recyclerView.setAdapter(adapter);
    }
}