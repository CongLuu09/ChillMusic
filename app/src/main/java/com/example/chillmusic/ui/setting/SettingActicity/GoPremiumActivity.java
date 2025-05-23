package com.example.chillmusic.ui.setting.SettingActicity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chillmusic.R;

public class GoPremiumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_premium);

        findViewById(R.id.planMonthly).setOnClickListener(view -> {
            Toast.makeText(this, "Monthly plan selected (Demo)", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.planYearly).setOnClickListener(view -> {
            Toast.makeText(this, "Yearly plan selected (Demo)", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.planLifetime).setOnClickListener(view -> {
            Toast.makeText(this, "Lifetime plan selected (Demo)", Toast.LENGTH_SHORT).show();
        });

    }
}