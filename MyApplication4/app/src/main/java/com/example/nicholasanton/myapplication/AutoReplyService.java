package com.example.nicholasanton.myapplication;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

public class AutoReplyService extends Service {

    final class SecondThread implements Runnable{
        int serviceId;

        SecondThread(int serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run() {
            synchronized (this) {
                autoReply();
            }
        }
    }

    private Thread theThread;
    private String sender="";

    private void autoReply() {
        if (isMyServiceRunning(RunningPolicy.class)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sender, null, "Sorry I'm kind of busy", null, null);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        theThread.interrupt();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent.getStringExtra("sender") != null){
            sender = intent.getStringExtra("sender");
        }
        theThread = new Thread(new SecondThread(startId));
        theThread.start();
        return START_STICKY;
    }
}