package com.example.chillmusic.models;

import android.media.MediaPlayer;

import com.google.gson.annotations.SerializedName;

public class LayerSound {
    private long id = -1;
    private String name;

    @SerializedName("fileSoundUrl")
    private String fileUrl;

    @SerializedName("fileImageUrl")
    private String imageUrl;

    private int iconResId;
    private int soundResId;
    private float volume;
    private MediaPlayer mediaPlayer;


    public LayerSound(int iconResId, String name, int soundResId, MediaPlayer mediaPlayer, float volume) {
        this.iconResId = iconResId;
        this.name = name;
        this.soundResId = soundResId;
        this.mediaPlayer = mediaPlayer;
        this.volume = volume;
        this.fileUrl = null;
        this.imageUrl = null;
    }


    public LayerSound(int iconResId, String name, int soundResId, String fileUrl, float volume, String imageUrl) {
        this.iconResId = iconResId;
        this.name = name;
        this.soundResId = soundResId;
        this.fileUrl = fileUrl;
        this.volume = volume;
        this.imageUrl = imageUrl;
        this.mediaPlayer = null;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getIconResId() { return iconResId; }

    public int getSoundResId() { return soundResId; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public float getVolume() { return volume; }
    public void setVolume(float volume) { this.volume = volume; }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
    public void setMediaPlayer(MediaPlayer mediaPlayer) { this.mediaPlayer = mediaPlayer; }


    @Override
    public String toString() {
        return "LayerSound{" +
                "fileUrl='" + fileUrl + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", iconResId=" + iconResId +
                ", soundResId=" + soundResId +
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
