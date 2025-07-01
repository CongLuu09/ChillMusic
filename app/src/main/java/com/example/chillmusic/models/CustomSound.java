package com.example.chillmusic.models;

import com.google.gson.annotations.SerializedName;

public class CustomSound {

    // ✅ Local Sound
    private int imageResId = -1;
    private int soundResId = -1;

    // ✅ Dùng chung
    @SerializedName("id")
    private long id = -1;

    @SerializedName("title")
    private String title;

    @SerializedName("link_music")
    private String fileUrl;

    @SerializedName("avatar")
    private String imageUrl;

    @SerializedName("category")
    private String category;

    private boolean locked = false;

    // 🔹 Constructor local
    public CustomSound(int imageResId, int soundResId, String title) {
        this.imageResId = imageResId;
        this.soundResId = soundResId;
        this.title = title;
    }

    // 🔹 Constructor online
    public CustomSound(long id, String title, String fileUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
    }

    // 🔹 Optional constructor đầy đủ
    public CustomSound(long id, String title, String fileUrl, String imageUrl, String category) {
        this.id = id;
        this.title = title;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getter/setter
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public int getSoundResId() { return soundResId; }
    public void setSoundResId(int soundResId) { this.soundResId = soundResId; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
}
