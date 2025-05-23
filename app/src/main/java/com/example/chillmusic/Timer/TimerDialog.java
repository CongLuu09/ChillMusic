package com.example.chillmusic.Timer;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chillmusic.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TimerDialog extends BottomSheetDialogFragment {

    private TimerCallBack callback;

    public void setCallback(TimerCallBack callback) {
        this.callback = callback;
    }

    public static TimerDialog newInstance() {
        return new TimerDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_timer, container, false);

        setupPreset(view, R.id.btn5min, 5);
        setupPreset(view, R.id.btn15min, 15);
        setupPreset(view, R.id.btn30min, 30);
        setupPreset(view, R.id.btn45min, 45);
        setupPreset(view, R.id.btn1h, 60);
        setupPreset(view, R.id.btn1h30, 90);
        setupPreset(view, R.id.btn2h, 120);

        Button btnCancel = view.findViewById(R.id.btnCancelTimer);
        btnCancel.setOnClickListener(v -> {
            if (callback != null) callback.onTimerCancelled();
            dismiss();
        });

        Button btnCustom = view.findViewById(R.id.btnCustom);
        btnCustom.setOnClickListener(v -> openCustomTimePicker());

        return view;
    }

    private void setupPreset(View root, int btnId, int minutes) {
        Button btn = root.findViewById(btnId);
        btn.setOnClickListener(v -> {
            if (callback != null) callback.onTimerSet(minutes * 60 * 1000L);
            dismiss();
        });
    }

    private void openCustomTimePicker() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_custom_time);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        NumberPicker npHour = dialog.findViewById(R.id.npHour);
        NumberPicker npMinute = dialog.findViewById(R.id.npMinute);
        Button btnSet = dialog.findViewById(R.id.btnSetCustomTime);

        npHour.setMinValue(0);
        npHour.setMaxValue(23);
        npMinute.setMinValue(0);
        npMinute.setMaxValue(59);

        btnSet.setOnClickListener(v -> {
            int h = npHour.getValue();
            int m = npMinute.getValue();
            long duration = (h * 60L + m) * 60_000L;
            if (duration > 0 && callback != null) {
                callback.onTimerSet(duration);
                dismiss();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}