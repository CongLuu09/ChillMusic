package com.example.chillmusic.service;

public class ApiResponse<T> {
    private boolean status;
    private T data;
    private String message;

    public boolean isStatus() { return status; }
    public T getData() { return data; }
    public String getMessage() { return message; }
}
