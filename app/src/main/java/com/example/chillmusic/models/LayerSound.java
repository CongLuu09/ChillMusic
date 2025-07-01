package com.example.chillmusic.models;

import android.media.MediaPlayer;

import com.google.gson.annotations.SerializedName;

public class LayerSound {
    private long id = -1;               // ID từ backend
    private String name;               // Tên âm thanh

    // Âm thanh từ API
    @SerializedName("fileSoundUrl")
    private String fileUrl;

    @SerializedName("fileImageUrl")
    private String imageUrl;

    // Âm thanh local từ res/raw
    private int soundResId = 0;

    // Icon local (nếu dùng)
    private int iconResId = 0;

    private float volume = 0.5f;       // Mặc định 50%
    private transient MediaPlayer mediaPlayer;
    private transient Runnable updateRunnable;

    // === Constructors ===

    // Âm local
    // Dùng cho âm thanh local (res/raw)
    public LayerSound(int iconResId, String name, int soundResId, MediaPlayer mediaPlayer, float volume) {
        this.iconResId = iconResId;
        this.name = name;
        this.soundResId = soundResId;
        this.mediaPlayer = mediaPlayer;
        this.volume = volume;
        this.fileUrl = null;
        this.imageUrl = null;
    }


    // Âm từ API
    public LayerSound(long id, String name, String fileUrl, String imageUrl, float volume) {
        this.id = id;
        this.name = name;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
        this.volume = volume;
    }


    // === Getters & Setters ===

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


    public int getSoundResId() {
        return soundResId;
    }

    public int getIconResId() {
        return iconResId;
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

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    // === Utility ===

    public boolean isFromApi() {
        return fileUrl != null && !fileUrl.isEmpty();
    }

    public boolean isLocal() {
        return soundResId != 0;
    }

    public void release() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }

    @Override
    public String toString() {
        return "LayerSound{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", soundResId=" + soundResId +
                ", iconResId=" + iconResId +
                ", volume=" + volume +
                '}';
    }
}
