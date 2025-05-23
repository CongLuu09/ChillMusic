 package com.example.chillmusic.models;

public class CustomSound {
    private int imageResId;
    private int soundResId;
    private String title;

    public CustomSound(int imageResId, int soundResId, String title) {
        this.imageResId = imageResId;
        this.soundResId = soundResId;
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public int getSoundResId() {
        return soundResId;
    }

    public void setSoundResId(int soundResId) {
        this.soundResId = soundResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}