package com.example.chillmusic.models;

public class SoundItem {
    private String name;
    private int iconResId;
    private int soundResId;
    private boolean locked;


    public SoundItem(int iconResId, boolean locked, String name, int soundResId) {
        this.iconResId = iconResId;
        this.locked = locked;
        this.name = name;
        this.soundResId = soundResId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSoundResId() {
        return soundResId;
    }

    public void setSoundResId(int soundResId) {
        this.soundResId = soundResId;
    }
}
