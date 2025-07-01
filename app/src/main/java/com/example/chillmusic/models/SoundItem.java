package com.example.chillmusic.models;

import com.google.gson.annotations.SerializedName;

public class SoundItem {

    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String name;

    @SerializedName("link_music")
    private String fileUrl;

    @SerializedName("avatar")
    private String imageUrl;

    @SerializedName("category")
    private String category;

    // Dành cho local sound (không dùng với API)
    private int iconResId = -1;
    private int soundResId = -1;

    // Trạng thái khóa (cho Rewarded Ad)
    private boolean locked = false;

    public SoundItem() {
    }

    // Constructor cho API
    public SoundItem(long id, String name, String fileUrl, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Constructor cho local sound
    public SoundItem(long id, String name, int iconResId, int soundResId) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.soundResId = soundResId;
        this.locked = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public int getSoundResId() {
        return soundResId;
    }

    public void setSoundResId(int soundResId) {
        this.soundResId = soundResId;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
