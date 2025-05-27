package com.example.chillmusic.models;

import android.media.MediaPlayer;

public class LayerSound {
    private long id = -1;

    private String name;
    private final int iconResId;
    private final int soundResId;
    private float volume;
    private MediaPlayer mediaPlayer;

    public LayerSound(int iconResId, String name, int soundResId, MediaPlayer mediaPlayer, float volume) {
        this.iconResId = iconResId;
        this.name = name;
        this.soundResId = soundResId;
        this.mediaPlayer = mediaPlayer;
        this.volume = volume;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIconResId() {
        return iconResId;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
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

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "LayerSound{" +
                "iconResId=" + iconResId +
                ", id=" + id +
                ", name='" + name + '\'' +
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
