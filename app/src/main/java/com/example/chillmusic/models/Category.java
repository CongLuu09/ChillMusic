package com.example.chillmusic.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    private long id;

    private String name;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("soundUrl")
    private String soundUrl;

    public Category(int id, String name, String imageUrl, String soundUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.soundUrl = soundUrl;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }
}
