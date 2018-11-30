package com.example.nicholasanton.myapplication;

/*
    This class needs to be used to set the options for the walking policy. If the application is running and the current activity is walking the policy will come into action, this page will decide what the policy will do
    the following boolean values will be used to store the value of the options. This together with the playlist button will be used to config this policy
    This class needs to implement a datahandler class that will write each of these settings to the database, the values will then be read and set again when the user will reopen the page
    functions needed are VarsToForm and FormToVars
 */

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class WalkingOptions extends AppCompatActivity {

    private Switch playHeadphones;
    private Switch trackUsage;
    private Switch saveResources;
    private Switch notificationTTS;
    private DataHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_options);

        playHeadphones =  findViewById(R.id.swPlayHeadphones);
        trackUsage =  findViewById(R.id.swTrackUsage);
        saveResources =  findViewById(R.id.swSaveResources);
        notificationTTS =  findViewById(R.id.swNotificationTTS);
        VarsToForm();

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
    }

    public void VarsToForm(){
        //Read the database values and update the activity to reflect those values
        db = new DataHandler(WalkingOptions.this);
        Cursor results = db.SelectSettingsQuery();
        if(results.moveToFirst()){
            int temp = results.getInt(2);
            if(temp == 0) {
                playHeadphones.setChecked(false);
            } else {
                playHeadphones.setChecked(true);
            }
        }
        if(results.moveToNext()){
            int temp = results.getInt(2);
            if(temp == 0) {
                trackUsage.setChecked(false);
            } else {
                trackUsage.setChecked(true);
            }
        }
        if(results.moveToNext()){
            int temp = results.getInt(2);
            if(temp == 0) {
                saveResources.setChecked(false);
            } else {
                saveResources.setChecked(true);
            }
        }
        if(results.moveToLast()){
            int temp = results.getInt(2);
            if(temp == 0) {
                notificationTTS.setChecked(false);
            } else {
                notificationTTS.setChecked(true);
            }
        }

    }

    public void openPlaylists(){
        Intent intent = new Intent(this, PlayLists.class);
        startActivity(intent);
    }
}
