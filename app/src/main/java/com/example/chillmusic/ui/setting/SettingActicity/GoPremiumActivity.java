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
        setupPlanClickListeners();
    }
    private void setupPlanClickListeners() {
        setupPlan(R.id.planMonthly, "Monthly plan selected");
        setupPlan(R.id.planYearly, "Yearly plan selected");
        setupPlan(R.id.planLifetime, "Lifetime plan selected");
    }

    private void setupPlan(int viewId, String message) {
        findViewById(viewId).setOnClickListener(view -> {
            Toast.makeText(this, message + " (Demo)", Toast.LENGTH_SHORT).show();
        });
    }

}