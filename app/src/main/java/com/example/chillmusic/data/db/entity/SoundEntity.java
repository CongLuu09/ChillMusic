package com.example.chillmusic.data.db.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "sounds",
        indices = {@Index(value = {"name"}, unique = true)}
)
public class SoundEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;


    public String name;


    public int iconResId;


    public int soundResId;


    public float volume;


    public long createdAt;


    public SoundEntity(String name, int iconResId, int soundResId, float volume, long createdAt) {
        this.name = name;
        this.iconResId = iconResId;
        this.soundResId = soundResId;
        this.volume = volume;
        this.createdAt = createdAt;
    }


    public SoundEntity(String name, int iconResId, int soundResId) {
        this(name, iconResId, soundResId, 0.5f, System.currentTimeMillis());
    }
}
