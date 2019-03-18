package com.example.nicholasanton.myapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Timer.CustomTimerTask;

import java.util.Timer;
import java.util.TimerTask;

public class Timer_Service extends Service{
    private DataHandler db;
    private final Timer timer = new Timer();
    private final TimerTask updateProfile = new CustomTimerTask(Timer_Service.this);

    public void onCreate() {

        super.onCreate();
        db = new DataHandler(this);
        db.insertLog("Timer Service Started\n");
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        timer.scheduleAtFixedRate(updateProfile, 0, 10000);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stubs
        super.onDestroy();
        db.insertLog("Timer Service Stopped\n");
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
