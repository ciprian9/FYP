package com.example.nicholasanton.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.TimerTask;

public class CustomTimerTask extends TimerTask {
    private Context context;
    private Handler mHandler = new Handler();

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
                        //Toast.makeText(context, "DISPLAY YOUR MESSAGE", Toast.LENGTH_SHORT).show();
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