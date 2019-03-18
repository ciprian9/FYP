package com.example.nicholasanton.myapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.LinkedList;

public class DebugService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LinkedList<String> myStrings = new LinkedList<>();
        return super.onStartCommand(intent, flags, startId);
    }
}
