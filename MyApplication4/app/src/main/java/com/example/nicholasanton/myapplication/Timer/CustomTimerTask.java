package com.example.nicholasanton.myapplication.Timer;

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
        new Thread(new Runnable() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                        callBroadcast();
                    }
                });
            }
        }).start();
    }

    private void callBroadcast(){
        Intent i = new Intent("GET_EVENTS");
        context.sendBroadcast(i);
    }
}