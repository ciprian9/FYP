package com.example.nicholasanton.myapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.LinkedList;

public class DebugService extends Service {
    LinkedList<String> myStrings;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myStrings = new LinkedList<String>();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
