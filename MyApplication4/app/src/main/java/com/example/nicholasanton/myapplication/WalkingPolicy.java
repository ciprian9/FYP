package com.example.nicholasanton.myapplication;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
After looking at the walking option need to be able to handle any of the options being turned on, or off. Need to consider better options and handle different scenarios.
Consider implementing the music player here. If most policies wil have a music player consider creating a super class Policy that will have the player and have classes inherit from that when the player is needed

 TODO
 //Service ends too early not given the chance to speak we need to keep the service running until the whole run is over
 //Try creating code for tracking usage / screen time is enough for now


 */

public class WalkingPolicy extends Service {

    private boolean pedometer;
    private boolean time;
    private boolean dist_speed;
    private boolean musicPlayer;
    private int accountid;

    final class TheThread implements Runnable{
        int serviceId;

        TheThread(int serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run() {
            synchronized (this) {
                doEverything();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();

        //just checking
        if( extras != null ) {
            accountid = extras.getInt(Constants.ACCOUNTID_INTENT);
            musicPlayer = extras.getBoolean(Constants.MUSIC_INTENT);
            pedometer = extras.getBoolean(Constants.PEDOMETER_INTENT);
            time = extras.getBoolean(Constants.TIME_INTENT);
            dist_speed = extras.getBoolean(Constants.DISTANCE_INTENT);
        }


        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        Thread theThread = new Thread(new TheThread(startId));
        theThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Has Been Destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void doEverything() {

        try {
            if(pedometer || time || dist_speed) {
                Intent i = new Intent(this, pedometerService.class);
                i.putExtra(Constants.PEDOMETER_INTENT, pedometer);
                i.putExtra(Constants.TIME_INTENT, time);
                i.putExtra(Constants.DISTANCE_INTENT, dist_speed);
                startService(i);
            }
        } catch (Exception e){
            System.out.printf(e.toString());
        }
    }
}