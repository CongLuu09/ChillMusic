package com.example.chillmusic.service;

import com.example.chillmusic.models.CategoryData;

public class CategoryResponse {
    private boolean status;
    private CategoryData data;
    private String message;

    public boolean isStatus() {
        return status;
    }

    public CategoryData getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}