package com.example.chillmusic.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryData {

    @SerializedName("current_page")
    private int currentPage;

    private List<Category> data;

    @SerializedName("last_page")
    private int lastPage;

    @SerializedName("next_page_url")
    private String nextPageUrl;

    @SerializedName("per_page")
    private int perPage;

    private int total;

    // Getter & Setter
    public int getCurrentPage() {
        return currentPage;
    }

    public List<Category> getData() {
        return data;
    }

    public int getLastPage() {
        return lastPage;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getTotal() {
        return total;
    }
}
