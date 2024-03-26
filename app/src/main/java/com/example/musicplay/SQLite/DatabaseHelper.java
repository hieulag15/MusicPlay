package com.example.musicplay.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //name vs version cua database
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;


    //Ten Bang va cot
    public static final String TABLE_NAME = "recentMusic";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_SONG = "id_song";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ID_SONG + " Long);";


    public long addData(Long id_song) {
        if (getRecentMusicCount() < 5) {
            if (checkSongExist(id_song) == true) {
                deleteDataByAge(id_song);
            }
        }
        else {
            if (checkSongExist(id_song) == true) {
                deleteDataByAge(id_song);
            }
            else {
                deleteFirstRecord();
            }
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID_SONG, id_song);
        long id = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    public int getRecentMusicCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public boolean checkSongExist(Long songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_SONG + " = " + songId;
        Cursor cursor = db.rawQuery(query, null);
        boolean result = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return result;
    }

    public void deleteDataByAge(long id_song) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID_SONG + " = ?", new String[] {String.valueOf(id_song)});
        db.close();
    }

    public void deleteFirstRecord() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "ROWID = (SELECT MIN(ROWID) FROM " + TABLE_NAME + ")";
        db.delete(TABLE_NAME, query, null);
        db.close();
    }

}
