package com.example.nicholasanton.myapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.nicholasanton.myapplication.Interfaces.Constants;

public class Driving_Policy_Service extends Service {

    private boolean showSpeed;
    private boolean recomendDestinations;
    private boolean musicPlayer;
    private boolean notificationTTS;
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

        if( extras != null ) {
            accountid = extras.getInt(Constants.ACCOUNTID_INTENT);
            musicPlayer = extras.getBoolean(Constants.MUSIC_INTENT);
            showSpeed = extras.getBoolean(Constants.SPEED_INTENT);
            recomendDestinations = extras.getBoolean(Constants.RECOMEND_INTENT);
            notificationTTS = extras.getBoolean(Constants.TEXT_TO_SPEECH_SETTING);
        }


        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        Thread theThread = new Thread(new Driving_Policy_Service.TheThread(startId));
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
        //
    }
}