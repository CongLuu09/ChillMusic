package com.example.chillmusic.models;

import java.util.List;

public class MixCreateRequest {
    private String name;
    private String deviceId;
    private List<Long> soundIds;

    public MixCreateRequest(String deviceId, String name, List<Long> soundIds) {
        this.deviceId = deviceId;
        this.name = name;
        this.soundIds = soundIds;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getSoundIds() {
        return soundIds;
    }

    public void setSoundIds(List<Long> soundIds) {
        this.soundIds = soundIds;
    }
}
