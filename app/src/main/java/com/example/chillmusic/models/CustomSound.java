package com.example.chillmusic.models;

public class CustomSound {
    // Dùng cho local
    private int imageResId;
    private int soundResId;

    // Dùng chung
    private String title;

    // ✅ Dùng cho online
    private long id = -1;
    private String fileUrl;
    private String imageUrl;


    public CustomSound(int imageResId, int soundResId, String title) {
        this.imageResId = imageResId;
        this.soundResId = soundResId;
        this.title = title;
    }


    public CustomSound(long id, String title, String fileUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
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


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
