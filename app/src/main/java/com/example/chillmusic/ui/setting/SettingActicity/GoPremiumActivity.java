package com.example.chillmusic.ui.setting.SettingActicity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chillmusic.R;

public class GoPremiumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_premium);

        setupPlanClickListeners();
    }

    private void setupPlanClickListeners() {
        LinearLayout planMonthly = findViewById(R.id.planMonthly);
        LinearLayout planYearly = findViewById(R.id.planYearly);
        LinearLayout planLifetime = findViewById(R.id.planLifetime);

        planMonthly.setOnClickListener(v -> showPremiumDialog("monthly"));
        planYearly.setOnClickListener(v -> showPremiumDialog("yearly"));
        planLifetime.setOnClickListener(v -> showPremiumDialog("lifetime"));
    }

    private void showPremiumDialog(String plan) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_premium_detail);


        TextView title = dialog.findViewById(R.id.premiumTitle);
        TextView desc = dialog.findViewById(R.id.premiumDesc);
        TextView start = dialog.findViewById(R.id.premiumStart);
        TextView price = dialog.findViewById(R.id.premiumPrice);
        TextView tvNotice = dialog.findViewById(R.id.tvnotice);
        Button btnSubscribe = dialog.findViewById(R.id.btnSubscribe);


        ImageView iconToggle = dialog.findViewById(R.id.iconToggle);
        LinearLayout layoutPaymentHeader = dialog.findViewById(R.id.layoutPaymentHeader);
        LinearLayout layoutPaymentMethods = dialog.findViewById(R.id.layoutPaymentMethods);


        LinearLayout itemMobifone = dialog.findViewById(R.id.itemMobifone);
        LinearLayout itemMomo = dialog.findViewById(R.id.itemMomo);
        ImageView checkMobifone = dialog.findViewById(R.id.checkMobifone);
        ImageView checkMomo = dialog.findViewById(R.id.checkMomo);

        TextView tvPlanPrice = findViewById(R.id.tvPlanPrice);
        TextView tvPlanDiscount = findViewById(R.id.tvPlanDiscount);
        TextView tvMonthlyPrice = findViewById(R.id.tvMonthlyPrice);
        TextView tvMonthlyDiscount = findViewById(R.id.tvMonthlyDiscount);



        boolean hasDiscount = true;
        int discountPercent = 70;

        switch (plan) {
            case "monthly":
                title.setText("Premium for one month");
                desc.setText("Sleep Sounds - relaxing sounds");

                if (hasDiscount) {
                    price.setText("7.000 đ/tháng + thuế");
                    tvMonthlyDiscount.setVisibility(View.VISIBLE);
                    tvMonthlyDiscount.setText("-" + discountPercent + "%");
                } else {
                    price.setText("23.000 đ/tháng + thuế");
                    tvMonthlyDiscount.setVisibility(View.GONE);
                }
                break;

            case "yearly":
                title.setText("Premium for one year");
                desc.setText("Sleep Sounds - relaxing sounds");

                if (hasDiscount) {
                    price.setText("81.000 đ/năm + thuế");
                    tvPlanDiscount.setVisibility(View.VISIBLE);
                    tvPlanDiscount.setText("-" + discountPercent + "%");
                } else {
                    price.setText("270.000 đ/năm + thuế");
                    tvPlanDiscount.setVisibility(View.GONE);
                }
                break;

            case "lifetime":
                title.setText("Premium trọn đời");
                desc.setText("Sleep Sounds - relaxing sounds");

                if (hasDiscount) {
                    price.setText("353.000 đ/mua 1 lần");
                    tvPlanDiscount.setVisibility(View.VISIBLE);
                    tvPlanDiscount.setText("-" + discountPercent + "%");
                } else {
                    price.setText("500.000 đ/mua 1 lần");
                    tvPlanDiscount.setVisibility(View.GONE);
                }
                break;
        }

        start.setText("Ngày bắt đầu hôm nay");
        tvNotice.setText(Html.fromHtml(getString(R.string.subscription_notice)));

        start.setText("Ngày bắt đầu hôm nay");
        tvNotice.setText(Html.fromHtml(getString(R.string.subscription_notice)));


        layoutPaymentHeader.setOnClickListener(v -> {
            boolean isVisible = layoutPaymentMethods.getVisibility() == View.VISIBLE;
            layoutPaymentMethods.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            iconToggle.setRotation(isVisible ? 0f : 180f);
        });


        itemMobifone.setOnClickListener(v -> {
            checkMobifone.setImageResource(android.R.drawable.checkbox_on_background);
            checkMomo.setImageResource(android.R.drawable.checkbox_off_background);
        });

        itemMomo.setOnClickListener(v -> {
            checkMobifone.setImageResource(android.R.drawable.checkbox_off_background);
            checkMomo.setImageResource(android.R.drawable.checkbox_on_background);
        });


        btnSubscribe.setOnClickListener(v -> {
            Toast.makeText(this, "Đăng ký gói " + title.getText().toString(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}
