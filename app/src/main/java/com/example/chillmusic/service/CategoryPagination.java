package com.example.chillmusic.service;

import com.example.chillmusic.models.Category;

import java.util.List;

public class CategoryPagination {
    private int current_page;
    private List<Category> data;

    public int getCurrentPage() { return current_page; }
    public List<Category> getData() { return data; }
}