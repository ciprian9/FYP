package com.example.ciprian.myapplication.Services;

/**
 * Service that will only run if the user is driving and will proceed to run the needed services if allowed
 * */

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.ciprian.myapplication.Interfaces.Constants;
import com.example.ciprian.myapplication.Views.LockedScreen;

public class Driving_Policy_Service extends Service {
    private boolean recordRoute;

    //Creates a thread so this is able to run in the background while other proccesses are running
    final class TheThread implements Runnable{
        final int serviceId;

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

    //Will recieve the boolean needed from the main application to decide wether the route should be recorded or not
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        //Checks if values have been passed and if so it will add the boolean passed to a global private variable
        if( extras != null ) {
            recordRoute = extras.getBoolean(Constants.RECORD_ROUTE);
        }
        //Will create and start the thread
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

    //Will check if the user wants to record route or not and will also start getting the speed and distance of the user
    private void startServices() {
        try {
            //Checks if the user allows to record the route and if so will pass the required fields to the map service and start it
            if (recordRoute) {
                Intent i = new Intent(getApplicationContext(), MapService.class);
                i.putExtra("temp", false);
                i.putExtra(Constants.POLICY_ID, 4);
                startService(i);
            }
            //Start the speed and distance service to get the speed and distance using the location of the user
            Intent myIntent = new Intent(getApplicationContext(), LockedScreen.class);
            getApplicationContext().startActivity(myIntent);
        } catch (Exception e){
            System.out.print(e.toString());
        }
    }
}