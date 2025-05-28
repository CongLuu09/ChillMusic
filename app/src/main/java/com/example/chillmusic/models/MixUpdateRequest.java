package com.example.chillmusic.models;

import java.util.List;

public class MixUpdateRequest {
    private String name;
    private String deviceId;
    private List<Integer> soundIds;

    public MixUpdateRequest(String deviceId, String name, List<Integer> soundIds) {
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

    public List<Integer> getSoundIds() {
        return soundIds;
    }

    public void setSoundIds(List<Integer> soundIds) {
        this.soundIds = soundIds;
    }
}
