package com.example.chillmusic.models;

import java.util.List;

public class MixDto {
    private int id;
    private String name;
    private String deviceId;
    private List<Integer> soundIds;
    private String createdAt;

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public List<Integer> getSoundIds() {
        return soundIds;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setSoundIds(List<Integer> soundIds) {
        this.soundIds = soundIds;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
