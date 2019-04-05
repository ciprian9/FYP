package com.example.nicholasanton.myapplication.Services;

/**
 * Will only run if the user is driving
 * */

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.Views.ActivitesListeners;
import com.example.nicholasanton.myapplication.Views.LockedScreen;

public class Driving_Policy_Service extends Service {
    private boolean recordRoute;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if( extras != null ) {
            recordRoute = extras.getBoolean(Constants.RECORD_ROUTE);
        }
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

    private void startServices() {
        try {
            if (recordRoute) {
                Intent i = new Intent(getApplicationContext(), MapService.class);
                i.putExtra("temp", false);
                i.putExtra(Constants.POLICY_ID, 4);
                startService(i);
            }
            Intent myIntent = new Intent(getApplicationContext(), LockedScreen.class);
            getApplicationContext().startActivity(myIntent);
        } catch (Exception e){
            System.out.print(e.toString());
        }
    }
}