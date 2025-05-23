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
                new FreeApp("Alarm Clock", "Trouble waking up in the morning? Try this.", R.drawable.ic_alarm, "https://play.google.com/store/apps/details?id=com.example.alarm"),
                new FreeApp("Lullaby", "Baby lullaby, baby sleeping music is a free offline...", R.drawable.ic_lullaby, "https://play.google.com/store/apps/details?id=com.example.lullaby"),
                new FreeApp("Binaural Beats", "Free study music and binaural beats app...", R.drawable.ic_brainwave, "https://play.google.com/store/apps/details?id=com.example.brainwave"),
                new FreeApp("Meditation Music", "Meditation Music app contains 17 different free...", R.drawable.ic_meditation, "https://play.google.com/store/apps/details?id=com.example.meditation")
        );


        adapter = new MoreAppsAdapter(apps, this);
        recyclerView.setAdapter(adapter);
    }
}