package com.example.nicholasanton.myapplication.Timer;

/**
 *  Will run every 10 second using a timer to get the events of the user
 *  */

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.TimerTask;

public class CustomTimerTask extends TimerTask {
    private final Context context;
    private final Handler mHandler = new Handler();

    public CustomTimerTask(Context con) {
        this.context = con;
    }

    @Override
    public void run() {
        //create the thread for the timer
        new Thread(new Runnable() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                        //call to callBroadcast
                        callBroadcast();
                    }
                });
            }
        }).start();
    }

    private void callBroadcast(){
        //send a broadcast to activities listener to download the events from google calendar
        Intent i = new Intent("GET_EVENTS");
        context.sendBroadcast(i);
    }
}