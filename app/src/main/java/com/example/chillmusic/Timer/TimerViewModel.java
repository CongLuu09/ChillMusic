package com.example.chillmusic.Timer;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TimerViewModel extends AndroidViewModel {
    private CountDownTimer countDownTimer;
    private final MutableLiveData<Long> timeLeftMillis = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);
    public TimerViewModel(@NonNull Application application) {
        super(application);
    }

    public void startTimer(long durationMillis) {
        cancelTimer();

        isRunning.setValue(true);
        timeLeftMillis.setValue(durationMillis);

        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis.postValue(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timeLeftMillis.postValue(0L);
                isRunning.postValue(false);
            }
        }.start();
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isRunning.setValue(false);
        timeLeftMillis.setValue(0L);
    }

    public LiveData<Long> getTimeLeftMillis() {
        return timeLeftMillis;
    }

    public LiveData<Boolean> getIsRunning() {
        return isRunning;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cancelTimer();
    }
}