package com.example.ciprian.myapplication.Services;

/**
 *  Will start the service every time the user is running and if enabled will run the pedometer service
 *  */

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.ciprian.myapplication.DataHandler;
import com.example.ciprian.myapplication.Interfaces.Constants;

public class Running_Policy_Service extends Service {

    private boolean pedometer;
    private boolean time;
    private boolean dist_speed;

    final class TheThread implements Runnable{
        final int serviceId;
        //Crating the new thread to be ran in this service
        TheThread(int serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run() {
            synchronized (this) {
                startServices();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //create a bundle to read the extras passed with the intent starting the service
        Bundle extras = intent.getExtras();

        if( extras != null ) {
            //if there are values sent read the values below
            pedometer = extras.getBoolean(Constants.PEDOMETER_INTENT);
            time = extras.getBoolean(Constants.TIME_INTENT);
            dist_speed = extras.getBoolean(Constants.DISTANCE_INTENT);
        }

        //notification
        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        //Create the thread that will run the starting functionality
        Thread theThread = new Thread(new TheThread(startId));
        //start the thread
        theThread.start();
        //allow the service to stay started but do not keep the sent intent
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //end the service
        super.onDestroy();
        Toast.makeText(this, "Service Has Been Destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startServices() {
        try {
            //check user preferences
            if(pedometer || time || dist_speed) {
                DataHandler db = new DataHandler(this);
                //log this for debugging purposes
                db.insertLog("Starting Running Pedometer Service");
                //start the pedometer service
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