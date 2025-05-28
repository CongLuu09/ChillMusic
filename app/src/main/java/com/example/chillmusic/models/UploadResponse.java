package com.example.chillmusic.models;

public class UploadResponse {
    private long soundId;
    private String fileSoundUrl;
    private String fileImageUrl;
    private String name;

    public UploadResponse(long soundId, String fileSoundUrl, String fileImageUrl, String name) {
        this.soundId = soundId;
        this.fileSoundUrl = fileSoundUrl;
        this.fileImageUrl = fileImageUrl;
        this.name = name;
    }

    public long getSoundId() {
        return soundId;
    }

    public void setSoundId(long soundId) {
        this.soundId = soundId;
    }

    public String getFileSoundUrl() {
        return fileSoundUrl;
    }

    public void setFileSoundUrl(String fileSoundUrl) {
        this.fileSoundUrl = fileSoundUrl;
    }

    public String getFileImageUrl() {
        return fileImageUrl;
    }

    public void setFileImageUrl(String fileImageUrl) {
        this.fileImageUrl = fileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
