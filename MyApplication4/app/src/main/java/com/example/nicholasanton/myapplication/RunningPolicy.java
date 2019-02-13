package com.example.nicholasanton.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class RunningPolicy extends Service {

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
        Intent i = new Intent(this, PedometerActivity.class);
        startActivity(i);
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