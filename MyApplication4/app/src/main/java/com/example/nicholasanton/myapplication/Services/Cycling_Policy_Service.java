package com.example.nicholasanton.myapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.nicholasanton.myapplication.Interfaces.Constants;

public class Cycling_Policy_Service extends Service {

    final class TheThread implements Runnable{
        final int serviceId;

        TheThread(int serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run() {
            synchronized (this) {
                //doEverything();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if( extras != null ) {
            int accountid = extras.getInt(Constants.ACCOUNTID_INTENT);
            boolean musicPlayer = extras.getBoolean(Constants.MUSIC_INTENT);
            boolean showSpeed = extras.getBoolean(Constants.SPEED_INTENT);
            boolean recomendDestinations = extras.getBoolean(Constants.RECOMEND_INTENT);
            boolean notificationTTS = extras.getBoolean(Constants.TEXT_TO_SPEECH_SETTING);
        }

        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        Thread theThread = new Thread(new Cycling_Policy_Service.TheThread(startId));
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

//    private void doEverything() {
//        //
//    }
}