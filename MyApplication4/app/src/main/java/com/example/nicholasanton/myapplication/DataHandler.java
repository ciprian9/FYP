package com.example.nicholasanton.myapplication;/*
Need to implemeant a dataHandler that will create a SQLite database
This will be used to store settings and any other data we may require
functions below may require more or less parameters and different return types this is just a mock up
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "FYP.db";
    private static final String SETTINGS_TABLE_NAME = "Settings";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_STATUS = "Status";

    private static final String PLAYLIST_TABLE_NAME = "Playlist";
    private static final String COLUMN_POLICY_ID = "PolicyID";
    private static final String COLUMN_LOCATION = "Location";

    private static final String SETTINGS_CREATE = "CREATE TABLE " + SETTINGS_TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " + COLUMN_STATUS + " BIT)";

    private static final String PLAYLIST_TABLE = "CREATE TABLE " + PLAYLIST_TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " +
            COLUMN_POLICY_ID + " INTEGER, " + COLUMN_LOCATION + " TEXT)";

    DataHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void CreateTable(String TableName){
        //Allow the creation of a new table easily, the code should be easily adapted to create any table, most likeyly will need to pass an array of sorts to specify the columns
        //Need to research if this function is possible
    }

    public Cursor SelectSettingsQuery(){
        //This will most likely need to be thought out since we don't knwo what we will return, could be anything
        //Posibly pass in the query
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + SETTINGS_TABLE_NAME;
        Cursor result = db.rawQuery(query, null);
        return result;
    }

    public Cursor SelectPlaylistQuery(int PolicyID){
        //This will most likely need to be thought out since we don't knwo what we will return, could be anything
        //Posibly pass in the query
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + PLAYLIST_TABLE_NAME + " WHERE PolicyID = " + PolicyID;
        Cursor result = db.rawQuery(query, null);
        return result;
    }

    public boolean updateSettingsData(String id, Boolean status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_STATUS, status);
        db.update(SETTINGS_TABLE_NAME, contentValues, "id = ?", new String[]{id});
        return true;
    }

    public void DeletePlaylists(){
        //Need it just in case, we shouldnt have to delete anything but for example if using the music on the device the user might delete a song so we cannot play it in that case delete it from the playlist
        SQLiteDatabase db = this.getWritableDatabase();
        int temp = db.delete(PLAYLIST_TABLE_NAME, "1", null);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SETTINGS_CREATE);
        db.execSQL(PLAYLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE_NAME);
        onCreate(db);
    }

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

    public boolean insertSettingsData(String name, boolean status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_STATUS, status);
        long result = db.insert(SETTINGS_TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }
}
