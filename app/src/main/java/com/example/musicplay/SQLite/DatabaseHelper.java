package com.example.musicplay.SQLite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột
    public static final String TABLE_NAME = "recentMusic";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_SONG = "id_song";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    // Câu truy vấn tạo bảng
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ID_SONG + " Long);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nếu có phiên bản mới hơn, ta có thể thực hiện thêm các thay đổi tại đây
    }
    public long addData(long id_song) {
        System.out.println("===="+getRecentMusicCount());
        if(getRecentMusicCount()<5) {
            if (checkSongExist(id_song) == true){
                deleteDataByAge(id_song);
            }
        }
        else
        {
            System.out.println("====1");
            if(checkSongExist(id_song) == true){
                deleteDataByAge(id_song);
            }
            else {
                System.out.println("====3");
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
    public boolean checkSongExist(Long songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_SONG + " = " + songId;
        Cursor cursor = db.rawQuery(query, null);
        boolean exist = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exist;
    }
    public int getRecentMusicCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void deleteDataByAge(long id_song) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID_SONG + " = ?", new String[] { String.valueOf(id_song) });
        db.close();
    }
    public void deleteFirstRecord() {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "ROWID = (SELECT MIN(ROWID) FROM " + TABLE_NAME + ")";
        db.delete(TABLE_NAME, whereClause, null);
        db.close();
    }
    public List<Long> getAllData() {
        List<Long> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID_SONG}, null, null, null, null, null);
        if(cursor!=null){
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") long age = cursor.getLong(cursor.getColumnIndex("id_song"));
                    dataList.add(age);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return dataList;
    }
}
