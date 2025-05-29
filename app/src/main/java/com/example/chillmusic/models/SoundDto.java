package com.example.chillmusic.models;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SoundDto {
    private int id;
    private String name;

    @SerializedName("fileSoundUrl")
    private String fileUrl;      // URL âm thanh từ backend

    @SerializedName("fileImageUrl")
    private String imageUrl;     // URL ảnh từ backend

    private String category;
    private List<String> tags;

    // Constructor đầy đủ các trường, chỉnh thứ tự cho hợp lý
    public SoundDto(int id, String name, String fileUrl, String imageUrl, String category, List<String> tags) {
        this.id = id;
        this.name = name;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
        this.category = category;
        this.tags = tags;
    }

    // Getters và setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getFileUrl() {
        return fileUrl;
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
