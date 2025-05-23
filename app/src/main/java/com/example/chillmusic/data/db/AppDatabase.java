package com.example.chillmusic.data.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sounds.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SOUNDS = "sounds";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ICON = "iconResId";
    public static final String COLUMN_SOUND = "soundResId";

    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public AppDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SOUNDS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ICON + " INTEGER, " +
                COLUMN_SOUND + " INTEGER)";
        db.execSQL(CREATE_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOUNDS);
        onCreate(db);
    }
    public void insertSound(String name, int iconResId, int soundResId) {
        if (isSoundExists(name)) return;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ICON, iconResId);
        values.put(COLUMN_SOUND, soundResId);
        db.insert(TABLE_SOUNDS, null, values);
        db.close();
    }


    public boolean isSoundExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SOUNDS, null, COLUMN_NAME + "=?",
                new String[]{name}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }


    public List<LayerSound> getAllSavedSounds() {
        List<LayerSound> sounds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SOUNDS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                int iconResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ICON));
                int soundResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SOUND));

                LayerSound layer = new LayerSound(iconResId, name, soundResId, null, 0.1f);
                sounds.add(layer);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return sounds;
    }
}
