package com.example.chillmusic.models;

import com.google.gson.annotations.SerializedName;

public class SoundItem {

    private long id = -1;
    private String name;
    private int iconResId;
    private int soundResId;
    private boolean locked;

    @SerializedName("fileSoundUrl")
    private String fileUrl;

    @SerializedName("fileImageUrl")
    private String imageUrl;

    public SoundItem(long id, int iconResId, boolean locked, String name, int soundResId) {
        this(id, iconResId, locked, name, soundResId, null, null);
    }

    public SoundItem(long id, int iconResId, boolean locked, String name, int soundResId, String fileUrl, String imageUrl) {
        this.id = id;
        this.iconResId = iconResId;
        this.locked = locked;
        this.name = name;
        this.soundResId = soundResId;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }

    public int getSoundResId() { return soundResId; }
    public void setSoundResId(int soundResId) { this.soundResId = soundResId; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
