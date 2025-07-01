package com.example.chillmusic.service;

import com.example.chillmusic.models.SoundDto;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SoundData {
    @SerializedName("data")
    private List<SoundDto> sounds;

    public List<SoundDto> getSounds() { return sounds; }
}
