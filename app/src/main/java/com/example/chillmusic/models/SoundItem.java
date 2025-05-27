package com.example.chillmusic.models;

public class SoundItem {

    private long id = -1;
    private String name;
    private int iconResId;
    private int soundResId;
    private boolean locked;


    public SoundItem(long id, int iconResId, boolean locked, String name, int soundResId) {
        this.id = id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
