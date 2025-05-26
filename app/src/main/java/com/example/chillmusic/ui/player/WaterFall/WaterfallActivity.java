package com.example.chillmusic.ui.player.WaterFall;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WaterfallActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_waterfall);
    }
}