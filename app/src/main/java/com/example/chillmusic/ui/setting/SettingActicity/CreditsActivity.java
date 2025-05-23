package com.example.chillmusic.ui.setting.SettingActicity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chillmusic.R;

public class CreditsActivity extends AppCompatActivity {

    private String[][] contributors = {
            {"Chris L", "French"},
            {"MattJaskot, Jade", "Polish"},
            {"Patrick Carvalho", "Portuguese"},
            {"Reunice", "Filipino"},
            {"Kherneg A.Nour", "Arabic"},
            {"mbenceww", "Hungarian"},
            {"Adam Marcin", "Slovak"},
            {"Xenia L.", "German"},
            {"Nguyen T. An", "Vietnamese"},
            {"Haruki Tanaka", "Japanese"},
            {"Chen Wei", "Chinese"},
            {"Ivan Petrov", "Russian"},
            {"Maria Gomez", "Spanish"},
            {"Luca Bianchi", "Italian"},
            {"Johan Svensson", "Swedish"},
            {"Fatima Zahra", "Turkish"},
            {"Raj Patel", "Hindi"},
            {"Noah Müller", "Swiss German"},
            {"Ava Thompson", "English"},
            {"Kim Seo-yeon", "Korean"},
            {"André Silva", "Brazilian Portuguese"},
            {"Omar El-Sayed", "Egyptian Arabic"},
            {"Siti Nurhaliza", "Malay"},
            {"Jean Dupont", "Belgian French"},
            {"Thomas Dubois", "Canadian French"},
            {"Nina Kowalska", "Czech"},
            {"Ali Reza", "Persian"},
            {"Ravi Shankar", "Tamil"},
            {"Pieter van Dijk", "Dutch"},
            {"Katarina Horvat", "Croatian"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        LinearLayout container = findViewById(R.id.creditListContainer);

        for (String[] contributor : contributors) {
            TextView nameView = new TextView(this);
            nameView.setText(contributor[0]);
            nameView.setTextColor(getResources().getColor(android.R.color.white));
            nameView.setTextSize(16);
            nameView.setPadding(0, 12, 0, 0);

            TextView langView = new TextView(this);
            langView.setText(contributor[1]);
            langView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            langView.setTextSize(14);

            container.addView(nameView);
            container.addView(langView);
        }

        findViewById(R.id.btnOk).setOnClickListener(v -> finish());
    }
}