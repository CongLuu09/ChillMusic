package com.example.chillmusic.models;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SoundDto {

    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("link_music")
    private String link_music;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("category")
    private String category;

    public long getId() { return id; }

    public String getTitle() { return title; }

    public String getLinkMusic() { return link_music; }

    public String getAvatar() { return avatar; }

    public String getCategory() { return category; }
}
