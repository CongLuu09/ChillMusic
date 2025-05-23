package com.example.chillmusic.Timer;

public interface TimerCallBack {
    void onTimerSet(long durationMillis);
    void onTimerCancelled();
    default void onTimerFinished() {}

}
