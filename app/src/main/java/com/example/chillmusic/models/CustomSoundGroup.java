package com.example.chillmusic.models;

import java.util.List;

public class CustomSoundGroup {
    private String groupName;
    private List<CustomSound> sounds;

    public CustomSoundGroup(String groupName, List<CustomSound> sounds) {
        this.groupName = groupName;
        this.sounds = sounds;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<CustomSound> getSounds() {
        return sounds;
    }

    public void setSounds(List<CustomSound> sounds) {
        this.sounds = sounds;
    }
}
