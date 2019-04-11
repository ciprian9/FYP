package com.example.ciprian.myapplication.Services;

/**
 *  Will start the service every time the user is walking and if enabled will run the pedometer service
 *  */

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.ciprian.myapplication.DataHandler;
import com.example.ciprian.myapplication.Interfaces.Constants;

public class Walking_Policy_Service extends Service {

    private boolean pedometer;
    private boolean time;
    private boolean dist_speed;
    private DataHandler db;

    final class TheThread implements Runnable{
        final int serviceId;

        TheThread(int serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run() {
            synchronized (this) {
                startPedometer();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        db = new DataHandler(this);
        //retrieve user preferences
        if( extras != null ) {
            pedometer = extras.getBoolean(Constants.PEDOMETER_INTENT);
            time = extras.getBoolean(Constants.TIME_INTENT);
            dist_speed = extras.getBoolean(Constants.DISTANCE_INTENT);
        }


        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        //create and start thread
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

    private void startPedometer() {
        try {
            //if these preferences are on either one or multiple then start the pedometer service
            if(pedometer || time || dist_speed) {
                db.insertLog("Starting Walking Pedometer Service\n");
                Intent i = new Intent(this, pedometerService.class);
                i.putExtra(Constants.PEDOMETER_INTENT, pedometer);
                i.putExtra(Constants.TIME_INTENT, time);
                i.putExtra(Constants.DISTANCE_INTENT, dist_speed);
                startService(i);
            }
        } catch (Exception e){
            System.out.print(e.toString());
        }
    }
}