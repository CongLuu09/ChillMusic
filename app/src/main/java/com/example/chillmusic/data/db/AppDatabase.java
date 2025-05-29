package com.example.chillmusic.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;

import com.example.chillmusic.models.LayerSound;
import com.example.chillmusic.models.MixDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sounds.db";
    private static final int DATABASE_VERSION = 3; // Tăng phiên bản DB

    // Bảng sounds hiện tại
    public static final String TABLE_SOUNDS = "sounds";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ICON = "iconResId";
    public static final String COLUMN_SOUND = "soundResId";
    public static final String COLUMN_SCENE = "sceneName";

    // Bảng mới: mix_sounds
    public static final String TABLE_MIXS = "mix_sounds";
    public static final String COLUMN_MIX_ID = "_id";
    public static final String COLUMN_MIX_NAME = "name";
    public static final String COLUMN_DEVICE_ID = "deviceId";
    public static final String COLUMN_SOUND_IDS = "soundIds"; // JSON string
    public static final String COLUMN_CREATED_AT = "createdAt";

    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SOUNDS_TABLE = "CREATE TABLE " + TABLE_SOUNDS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ICON + " INTEGER, " +
                COLUMN_SOUND + " INTEGER, " +
                COLUMN_SCENE + " TEXT)";
        db.execSQL(CREATE_SOUNDS_TABLE);

        String CREATE_MIXS_TABLE = "CREATE TABLE " + TABLE_MIXS + " (" +
                COLUMN_MIX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MIX_NAME + " TEXT, " +
                COLUMN_DEVICE_ID + " TEXT, " +
                COLUMN_SOUND_IDS + " TEXT, " +
                COLUMN_CREATED_AT + " TEXT)";
        db.execSQL(CREATE_MIXS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Tạo bảng mới khi nâng cấp lên version 3
            String CREATE_MIXS_TABLE = "CREATE TABLE " + TABLE_MIXS + " (" +
                    COLUMN_MIX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MIX_NAME + " TEXT, " +
                    COLUMN_DEVICE_ID + " TEXT, " +
                    COLUMN_SOUND_IDS + " TEXT, " +
                    COLUMN_CREATED_AT + " TEXT)";
            db.execSQL(CREATE_MIXS_TABLE);
        }

        // Nếu cần giữ nguyên bảng sounds hiện tại
        // Hoặc thêm logic nâng cấp nếu thay đổi bảng sounds
    }

    // --- Các hàm của bảng sounds ---

    public void insertSound(String name, int iconResId, int soundResId, String sceneName) {
        if (isSoundExists(name, sceneName)) return;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ICON, iconResId);
        values.put(COLUMN_SOUND, soundResId);
        values.put(COLUMN_SCENE, sceneName);
        db.insert(TABLE_SOUNDS, null, values);
        db.close();
    }

    public boolean isSoundExists(String name, String sceneName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SOUNDS, null,
                COLUMN_NAME + "=? AND " + COLUMN_SCENE + "=?",
                new String[]{name, sceneName}, null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public List<LayerSound> getAllSavedSounds(String sceneName) {
        List<LayerSound> sounds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SOUNDS, null,
                COLUMN_SCENE + "=?", new String[]{sceneName}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                int iconResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ICON));
                int soundResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SOUND));

                LayerSound layer = new LayerSound(iconResId, name, soundResId, (MediaPlayer) null, 0.1f);
                sounds.add(layer);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return sounds;
    }

    public void deleteSoundByName(String name, String sceneName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SOUNDS, COLUMN_NAME + "=? AND " + COLUMN_SCENE + "=?",
                new String[]{name, sceneName});
        db.close();
    }

    public void updateSoundName(String oldName, String newName, String sceneName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        db.update(TABLE_SOUNDS, values,
                COLUMN_NAME + "=? AND " + COLUMN_SCENE + "=?",
                new String[]{oldName, sceneName});
        db.close();
    }

    // --- Các hàm của bảng mix_sounds ---

    public void insertMix(MixDto mix) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MIX_NAME, mix.getName());
        values.put(COLUMN_DEVICE_ID, mix.getDeviceId());
        String soundIdsJson = new Gson().toJson(mix.getSoundIds());
        values.put(COLUMN_SOUND_IDS, soundIdsJson);
        values.put(COLUMN_CREATED_AT, mix.getCreatedAt());
        db.insert(TABLE_MIXS, null, values);
        db.close();
    }

    public List<MixDto> getAllMixes() {
        List<MixDto> mixList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MIXS, null, null, null, null, null, COLUMN_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                MixDto mix = new MixDto();
                mix.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MIX_ID)));
                mix.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MIX_NAME)));
                mix.setDeviceId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEVICE_ID)));
                String soundIdsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOUND_IDS));
                List<Integer> soundIds = new Gson().fromJson(soundIdsJson, new com.google.gson.reflect.TypeToken<List<Integer>>(){}.getType());
                mix.setSoundIds(soundIds);
                mix.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                mixList.add(mix);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mixList;
    }
}
