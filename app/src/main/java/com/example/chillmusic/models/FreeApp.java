package com.example.chillmusic.models;

public class FreeApp {
    private final String title;
    private final String description;
    private final int iconResId;
    private final String url;

    public FreeApp(String description, String title, int iconResId, String url) {
        this.description = description;
        this.title = title;
        this.iconResId = iconResId;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
