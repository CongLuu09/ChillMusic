package com.example.chillmusic.models;
import java.util.List;

public class SoundDto {
    private int id;
    private String name;
    private String fileUrl;
    private String imageUrl;
    private String iconUrl;
    private String category;
    private List<String> tags;

    public SoundDto(String category, String fileUrl, String imageUrl, String iconUrl, int id, String name, List<String> tags) {
        this.category = category;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
        this.iconUrl = iconUrl;
        this.id = id;
        this.name = name;
        this.tags = tags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
