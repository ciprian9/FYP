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
    //constants for DataHandler tables
    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "FYP.db";
    private static final String SETTINGS_TABLE_NAME = "Settings";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_DONE = "Done";
    private static final String COLUMN_STATUS = "Status";
    private static final String COLUMN_BATTERY_PERCENT = "Battery";

    private static final String PLAYLIST_TABLE_NAME = "Playlist";
    private static final String COLUMN_POLICY_ID = "PolicyID";
    private static final String COLUMN_LOCATION = "Location";

    private static final String USER_TABLE_NAME = "user";
    private static final String COLUMN_USERID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String PEDOMETER_TABLE_NAME = "pedometer";
    private static final String COLUMN_RECORD_ID = "id";
    private static final String COLUMN_START_DATETIME = "startdate";
    private static final String COLUMN_END_DATETIME = "enddate";
    private static final String COLUMN_STEP_COUNT = "steps";

    //Create queries for the tables
    private static final String SETTINGS_CREATE = "CREATE TABLE " + SETTINGS_TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_NAME + " TEXT, " + COLUMN_BATTERY_PERCENT + " INTEGER, " + COLUMN_STATUS + " BIT, " + COLUMN_DONE + " BIT)";

    private static final String PLAYLIST_TABLE = "CREATE TABLE " + PLAYLIST_TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " +
            COLUMN_POLICY_ID + " INTEGER, " + COLUMN_LOCATION + " TEXT)";

    private static final String REMEMBER_ME_TABLE = "CREATE TABLE " + USER_TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_USERNAME + " TEXT, " + COLUMN_PASSWORD + " TEXT)";

    private static final String STEPS_TABLE = "CREATE TABLE " + PEDOMETER_TABLE_NAME + " (" + COLUMN_RECORD_ID + " INTEGER PRIMARY KEY, " + COLUMN_START_DATETIME + " TEXT, " + COLUMN_END_DATETIME + " TEXT, "
        + COLUMN_STEP_COUNT + " INTEGER)";

    public DataHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Settings Table select statement with a where clause
    Cursor SelectSettingsQuery(String aString){
        //create a sqlite database object
        SQLiteDatabase db = this.getWritableDatabase();
        //preapare the where clause parameters
        String[] params = new String[]{ aString };
        //select query
        String query = "Select * from " + SETTINGS_TABLE_NAME + " WHERE " + COLUMN_NAME + " = ?";
        //run the query and receive the cursor filled with data
        Cursor result = db.rawQuery(query, params);
        //return the cursor
        return result;
    }

    //Settings Table select statement with a where clause
    public Cursor SelectUser(){
        //create a sqlite database object
        SQLiteDatabase db = this.getWritableDatabase();
        //select query
        String query = "Select * from " + USER_TABLE_NAME + " WHERE " + COLUMN_USERID + " = " + 1;
        //run the query and receive the cursor filled with data
        Cursor result = db.rawQuery(query, null);
        //return the cursor
        return result;
    }

    //Update Settings record
    public boolean updateUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        //The id of the setting is used, settings are added only once and updated thereafter
        db.update(USER_TABLE_NAME, contentValues, "id = ?", new String[]{"1"});
        return true;
    }

    public boolean insertUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERID, 1);
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        long result = db.insert(USER_TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public void DeleteUser(){
        //The name passed in is the name of a certain audio file the user chooses to delete from the DataBase
        SQLiteDatabase db = this.getWritableDatabase();
        int temp = db.delete(USER_TABLE_NAME, COLUMN_USERID + " =?", new String[]{"1"});
    }

    //Similar to the settings select
    public Cursor SelectPlaylistQuery(int PolicyID){
        //Parameter is not set since PolicyID is not a string meaning it does not need to be quoted
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + PLAYLIST_TABLE_NAME + " WHERE PolicyID = " + PolicyID;
        Cursor result = db.rawQuery(query, null);
        return result;
    }

    //Update Settings record
    public boolean updateSettingsData(String id, Boolean status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_STATUS, status);
        //The id of the setting is used, settings are added only once and updated thereafter
        db.update(SETTINGS_TABLE_NAME, contentValues, "id = ?", new String[]{id});
        return true;
    }

    //second query for updating settings numerical values : need to consider using a generic type to combine the two, this code could prove redundant
    public boolean updateSettingsIntData(String id, int aBatteryLvl){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_BATTERY_PERCENT, aBatteryLvl);
        db.update(SETTINGS_TABLE_NAME, contentValues, "id = ?", new String[]{id});
        return true;
    }

    //Delete query for audio files in the playlist
    public void DeletePlaylists(String name){
        //The name passed in is the name of a certain audio file the user chooses to delete from the DataBase
        SQLiteDatabase db = this.getWritableDatabase();
        int temp = db.delete(PLAYLIST_TABLE_NAME, COLUMN_NAME + " =?", new String[]{name});
    }

    //Create the two tables : todo need to add more tables to create
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SETTINGS_CREATE);
        db.execSQL(PLAYLIST_TABLE);
        db.execSQL(REMEMBER_ME_TABLE);
        db.execSQL(STEPS_TABLE);
    }

    //remove existing tables when schema changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PEDOMETER_TABLE_NAME);
        onCreate(db);
    }


    //Insert Query for the playlist
    public boolean insertPlaylistData(String name, int policyID, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_POLICY_ID, policyID);
        contentValues.put(COLUMN_LOCATION, location);
        long result = db.insert(PLAYLIST_TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }


    //Insert Query for the Settings
    public boolean insertSettingsData(String name, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_STATUS, status);
        long result = db.insert(SETTINGS_TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }


    //Similar to above this could prove redundant : todo try using generic type in order to avoid code repetition
    public boolean insertSettingsDataNumbers(String name, int Number){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_BATTERY_PERCENT, Number);
        long result = db.insert(SETTINGS_TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }
}
