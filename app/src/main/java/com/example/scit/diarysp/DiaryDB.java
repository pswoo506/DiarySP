package com.example.scit.diarysp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DiaryDB extends SQLiteOpenHelper {


    public DiaryDB(Context context) {
        super(context, "Diary", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE diary (" +
                "'code' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`title` TEXT," +
                "`date` TEXT," +
                "`contents` TEXT);";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists diary";
        db.execSQL(sql);

        onCreate(db);
    }
}



