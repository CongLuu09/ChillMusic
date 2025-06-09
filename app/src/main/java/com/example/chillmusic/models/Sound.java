package com.example.chillmusic.models;

public class Sound {
    private String title;
    private int imageResId;
    private int audioResId;


    public Sound(int imageResId, String title, int audioResId) {
        this.imageResId = imageResId;
        this.title = title;
        this.audioResId = audioResId;
    }

    public int getAudioResId() {
        return audioResId;
    }

    public void setAudioResId(int audioResId) {
        this.audioResId = audioResId;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}