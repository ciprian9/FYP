package com.example.nicholasanton.myapplication;/*
Need to implemeant a dataHandler that will create a SQLite database
This will be used to store settings and any other data we may require
functions below may require more or less parameters and different return types this is just a mock up


TODO
Need to add PolicyID to the Settings Table
Need to create a Policy Table

 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_NAME = "FYP.db";
    private static final String SETTINGS_TABLE_NAME = "Settings";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_NAME = "Name";

    private static final String PLAYLIST_TABLE_NAME = "Playlist";

    private static final String USER_TABLE_NAME = "user";
    private static final String COLUMN_USERID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String PEDOMETER_TABLE_NAME = "pedometer";

    private static final String LOGS_TABLE_NAME = "logs";
    private static final String COLUMN_LINE_ID = "id";
    private static final String COLUMN_LINE_TEXT = "message";

    private static final String REMEMBER_ME_TABLE = "CREATE TABLE " + USER_TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_USERNAME + " TEXT, " + COLUMN_PASSWORD + " TEXT)";


    private static final String LOGS_TABLE = "CREATE TABLE " + LOGS_TABLE_NAME + " (" + COLUMN_LINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + COLUMN_LINE_TEXT  + ")";

    public DataHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor SelectUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + USER_TABLE_NAME + " WHERE " + COLUMN_USERID + " = " + 1;
        return db.rawQuery(query, null);
    }

    public void DeleteLogs(){
        SQLiteDatabase db = this.getWritableDatabase();
        int temp = db.delete(LOGS_TABLE_NAME, "1", null);
    }

    //Update Settings record
    public void updateUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        db.update(USER_TABLE_NAME, contentValues, "id = ?", new String[]{"1"});
    }

    public void insertUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERID, 1);
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        long result = db.insert(USER_TABLE_NAME, null, contentValues);
        db.close();
    }

    public void DeleteUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        int temp = db.delete(USER_TABLE_NAME, COLUMN_USERID + " =?", new String[]{"1"});
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REMEMBER_ME_TABLE);
        db.execSQL(LOGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PEDOMETER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LOGS_TABLE_NAME);
        onCreate(db);
    }

    public void insertLog(String message){
        message = message + "\n";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LINE_TEXT, message);
        long result = db.insert(LOGS_TABLE_NAME, null, contentValues);
        db.close();
    }

    public Cursor SelectLogs(){
        //create a sqlite database object
        SQLiteDatabase db = this.getWritableDatabase();
        //select query
        String query = "Select * from " + LOGS_TABLE_NAME;
        //run the query and receive the cursor filled with data
        //return the cursor
        return db.rawQuery(query, null);
    }
}
