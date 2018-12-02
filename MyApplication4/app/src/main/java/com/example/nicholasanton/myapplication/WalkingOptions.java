package com.example.nicholasanton.myapplication;

/*
    This class needs to be used to set the options for the walking policy. If the application is running and the current activity is walking the policy will come into action, this page will decide what the policy will do
    the following boolean values will be used to store the value of the options. This together with the playlist button will be used to config this policy
    This class needs to implement a datahandler class that will write each of these settings to the database, the values will then be read and set again when the user will reopen the page
    functions needed are VarsToForm and FormToVars


    TODO
    Add a battery percent level for the save resources
    Add Pedometer count the ammount of steps made
    possibly add distance walked aswell
    add a map that will record the route walked so you can view it on a map

    Issues to be fixed
    Database connection lost need to fix it ✓
    Need to add the media player to the Policy.class
    Need to inherit walking policy from policy to use the media player
    Need a loop to play each song (need to figure out how to only play once the audio file is finished playing)
    Need to start tracking usage (for now the ammount of time spent on each app according to android and record how many times we have opened the app)
    need to find a way to block incoming notifications e.g. text messages and turn the message to text-to-speech
    possibly allow the user to respond using voice


    TODO
    battery percent field ✓
    if we get track usage done we need a button to view the usage
    we need to rearage the window
    need to move to new policy
 */

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class WalkingOptions extends AppCompatActivity {

    private Switch playHeadphones;
    private Switch trackUsage;
    private Switch saveResources;
    private Switch notificationTTS;
    private Switch autoReply;
    private TextView BatteryMaxLevel;
    private DataHandler db;
    private Cursor results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_options);

        playHeadphones =  findViewById(R.id.swPlayHeadphones);
        trackUsage =  findViewById(R.id.swTrackUsage);
        saveResources =  findViewById(R.id.swSaveResources);
        notificationTTS =  findViewById(R.id.swNotificationTTS);
        autoReply = findViewById(R.id.swAutoReply);
        BatteryMaxLevel = findViewById(R.id.batteryPercent);

        VarsToForm();
        db = new DataHandler(WalkingOptions.this);
        Cursor set = db.SelectSettingsQuery(Constants.AUTO_REPLY_SETTING);
        if(set.getCount() == 0) {
            DataHandler dataHandler = new DataHandler(this);
            boolean isInserted = dataHandler.insertSettingsData("playHeadphones", false);
            dataHandler.insertSettingsData("trackUsage", false);
            dataHandler.insertSettingsData("saveResources", false);
            dataHandler.insertSettingsData("notificationTTS", false);
            dataHandler.insertSettingsData("autoReply", false);
            dataHandler.insertSettingsDataNumbers("battery", 20);
        }

        final Button WalkingPlaylist = findViewById(R.id.walkingPlaylist);
        WalkingPlaylist.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mApiClient.disconnect();
                openPlaylists();
            }
        });

        playHeadphones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db = new DataHandler(WalkingOptions.this);
                db.updateSettingsData("1", isChecked);
            }
        });

        trackUsage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db = new DataHandler(WalkingOptions.this);
                db.updateSettingsData("2", isChecked);
            }
        });

        saveResources.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db = new DataHandler(WalkingOptions.this);
                db.updateSettingsData("3", isChecked);
            }
        });

        notificationTTS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db = new DataHandler(WalkingOptions.this);
                db.updateSettingsData("4", isChecked);
            }
        });

        autoReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db = new DataHandler(WalkingOptions.this);
                db.updateSettingsData("5", isChecked);
            }
        });

        final Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SetBatteryLevel();
            }
        });

    }

    public void VarsToForm(){
        //Read the database values and update the activity to reflect those values
        db = new DataHandler(WalkingOptions.this);
        results = db.SelectSettingsQuery(Constants.HEADPHONE_SETTING);
        if(results.moveToFirst()){
            int temp = results.getInt(3);
            if(temp == 0) {
                playHeadphones.setChecked(false);
            } else {
                playHeadphones.setChecked(true);
            }
        }

        results = db.SelectSettingsQuery(Constants.USAGE_SETTING);
        if(results.moveToFirst()){
            int temp = results.getInt(3);
            if(temp == 0) {
                trackUsage.setChecked(false);
            } else {
                trackUsage.setChecked(true);
            }
        }

        results = db.SelectSettingsQuery(Constants.SAVE_RESOURCE_SETTING);
        if(results.moveToFirst()){
            int temp = results.getInt(3);
            if(temp == 0) {
                saveResources.setChecked(false);
            } else {
                saveResources.setChecked(true);
            }
        }

        results = db.SelectSettingsQuery(Constants.TEXT_TO_SPEECH_SETTING);
        if(results.moveToFirst()){
            int temp = results.getInt(3);
            if(temp == 0) {
                notificationTTS.setChecked(false);
            } else {
                notificationTTS.setChecked(true);
            }
        }

        results = db.SelectSettingsQuery(Constants.AUTO_REPLY_SETTING);
        if(results.moveToFirst()){
            int temp = results.getInt(3);
            if(temp == 0) {
                autoReply.setChecked(false);
            } else {
                autoReply.setChecked(true);
            }
        }

        results = db.SelectSettingsQuery(Constants.BATTERY_LEVEL);
        if(results.moveToFirst()){
            int temp = results.getInt(2);
            BatteryMaxLevel.setHint(Integer.toString(temp));
        }
    }

    public void openPlaylists(){
        Intent intent = new Intent(this, PlayLists.class);
        startActivity(intent);
    }

    public void SetBatteryLevel(){

        String s = BatteryMaxLevel.getText().toString();
        int level =  Integer.parseInt(s);

       if (level < 1){
           Toast.makeText(this, "Number is too small try again", Toast.LENGTH_SHORT).show();
       } else if (level > 100){
           Toast.makeText(this, "Number is too high try again", Toast.LENGTH_SHORT).show();
       } else{
           db.updateSettingsIntData("6", level);
       }
    }
}
