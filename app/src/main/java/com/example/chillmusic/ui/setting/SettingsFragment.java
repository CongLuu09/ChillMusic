package com.example.chillmusic.ui.setting;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.adapter.SettingsAdapter;
import com.example.chillmusic.models.SettingItem;
import com.example.chillmusic.ui.setting.SettingActicity.CreditsActivity;
import com.example.chillmusic.ui.setting.SettingActicity.FaqActivity;
import com.example.chillmusic.ui.setting.SettingActicity.GoPremiumActivity;
import com.example.chillmusic.ui.setting.SettingActicity.MoreFreeAppsActivity;
import com.example.chillmusic.ui.setting.SettingActicity.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SettingsAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewSettings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SettingsAdapter(getSettingItems(), getContext(), item -> {
            switch (item.getTitle()) {
                case "Rate Us":
                    openPlayStore();
                    break;

                case "Playback Stops Unexpectedly?":
                    showPlaybackHelpDialog();
                    break;

                case "Share":
                    shareApp();
                    break;

                case "Go Premium":
                    openPremiumScreen();
                    break;

                case "App Version":
                    break;

                case "FAQ":
                    openWeb("https://yourapp.com/faq");
                    break;

                case "Feedback":
                    sendFeedbackEmail();
                    break;

                case "Credit":
                    showCredits();
                    break;

                case "Manage Subscriptions":
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/account/subscriptions")));
                    } catch (Exception e) {
                        showToast("Unable to open subscriptions.");
                    }
                    break;


                case "Privacy Policy":
                    PrivacyPolicy();
                    break;

                case "More Free Apps":
                    startActivity(new Intent(requireContext(), MoreFreeAppsActivity.class));
                    break;

                default:
                    showToast(item.getTitle());
                    break;
            }
        });

        recyclerView.setAdapter(adapter);
        return view;
    }

    private void PrivacyPolicy() {
        Intent intent = new Intent(requireContext(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, "https://yourapp.com/privacy");
        intent.putExtra(WebViewActivity.EXTRA_TITLE, "Privacy Policy");
        startActivity(intent);
    }

    private void showCredits() {
        Intent intent = new Intent(getContext(), CreditsActivity.class);
        startActivity(intent);
    }

    private List<SettingItem> getSettingItems() {
        List<SettingItem> items = new ArrayList<>();
        items.add(new SettingItem("Go Premium"));
        items.add(new SettingItem("Playback Stops Unexpectedly?"));
        items.add(new SettingItem("Rate Us" ));
        items.add(new SettingItem("Share" ));
        items.add(new SettingItem("App Version"));
        items.add(new SettingItem("FAQ"));
        items.add(new SettingItem("Feedback"));
        items.add(new SettingItem("Credit"));
        items.add(new SettingItem("Manage Subscriptions"));
        items.add(new SettingItem("Privacy Policy"));
        items.add(new SettingItem("More Free Apps"));
        return items;
    }

    private void showPlaybackHelpDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Troubleshooting Playback")
                .setMessage("If the sound stops unexpectedly, try the following:\n\n" +
                        "• Disable battery optimization for this app\n" +
                        "• Allow background activity\n" +
                        "• Lock the app in recent apps\n" +
                        "• Avoid using 'Battery Saver' mode\n\n" +
                        "Still having issues? Contact us at support@yourapp.com")
                .setPositiveButton("Got it", null)
                .show();
    }

    private void sendFeedbackEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:feedback@yourapp.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sleep Sound Feedback");
        startActivity(Intent.createChooser(intent, "Send Feedback"));
    }

    private void openWeb(String url) {
        Intent intent = new Intent(getContext(), FaqActivity.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void openPremiumScreen() {
        Intent intent = new Intent(requireContext(), GoPremiumActivity.class);
        startActivity(intent);
    }

    private void shareApp() {
        String packageName = requireContext().getPackageName();
        String shareText = "Try Sleep Sound app to relax and sleep better:\n" +
                "https://play.google.com/store/apps/details?id=" + packageName;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sleep Sound App");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void openPlayStore() {
        String packageId = "relax.sleep.relaxation.sleepingsounds";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageId)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageId + "&hl=vi")));
        }
    }
}