package com.example.airportinfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "airport.db";
    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE flights (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "flight_number TEXT," +
                "destination TEXT," +
                "departure_time TEXT," +
                "arrival_time TEXT," +
                "airline TEXT," +
                "status TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS flights"); // Удаляем старую таблицу
        onCreate(db); // Создаём новую
    }
}
