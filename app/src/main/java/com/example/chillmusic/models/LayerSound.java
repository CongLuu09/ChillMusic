package com.example.chillmusic.models;

import android.media.MediaPlayer;

public class LayerSound {
    private long id = -1;

    private String name;
    private final int iconResId;
    private final int soundResId;
    private String fileUrl;
    private String imageUrl;
    private String iconUrl;
    private float volume;
    private MediaPlayer mediaPlayer;

    // ✅ Constructor cho local
    public LayerSound(int iconResId, String name, int soundResId, MediaPlayer mediaPlayer, float volume) {
        this.iconResId = iconResId;
        this.name = name;
        this.soundResId = soundResId;
        this.volume = volume;
        this.mediaPlayer = mediaPlayer;
        this.fileUrl = null;
        this.imageUrl = null;
        this.iconUrl = null;
    }

    // ✅ Constructor mở rộng cho online (có cả imageUrl)
    public LayerSound(int iconResId, String name, int soundResId, String fileUrl, float volume, String imageUrl) {
        this.iconResId = iconResId;
        this.name = name;
        this.soundResId = soundResId;
        this.fileUrl = fileUrl;
        this.volume = volume;
        this.imageUrl = imageUrl;
        this.iconUrl = null; // Không sử dụng iconUrl trong trường hợp này
        this.mediaPlayer = null;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getIconResId() { return iconResId; }
    public int getSoundResId() { return soundResId; }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
    public void setMediaPlayer(MediaPlayer mediaPlayer) { this.mediaPlayer = mediaPlayer; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getVolume() { return volume; }
    public void setVolume(float volume) { this.volume = volume; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return "LayerSound{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", iconResId=" + iconResId +
                ", soundResId=" + soundResId +
                ", fileUrl='" + fileUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", volume=" + volume +
                ", mediaPlayer=" + mediaPlayer +
                '}';
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
