package com.example.chillmusic.service;

public class SoundResponse {
    private boolean status;
    private SoundData data;
    private String message;

    public boolean isStatus() { return status; }
    public SoundData getData() { return data; }
    public String getMessage() { return message; }
}

