package com.example.chillmusic.utils;

import com.example.chillmusic.models.Sound;

public class SoundOnline extends Sound {
    private String fileUrl;
    public SoundOnline(String title, int imageResId, String fileUrl) {
        super(imageResId, title, 0);
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
