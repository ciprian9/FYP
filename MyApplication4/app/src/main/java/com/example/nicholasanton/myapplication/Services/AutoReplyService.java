package com.example.nicholasanton.myapplication.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.example.nicholasanton.myapplication.DataHandler;

public class AutoReplyService extends Service {

    final class SecondThread implements Runnable{
        final int serviceId;

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
    private DataHandler db;

    private void autoReply() {
        if (isMyServiceRunning(Running_Policy_Service.class) || isMyServiceRunning(Cycling_Policy_Service.class) || isMyServiceRunning(Driving_Policy_Service.class)) {
            db.insertLog("Replied to Text");
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
        db = new DataHandler(this);
        db.insertLog("Started AutoReply");
        if (intent.getStringExtra("sender") != null){
            sender = intent.getStringExtra("sender");
        }
        theThread = new Thread(new SecondThread(startId));
        theThread.start();
        return START_STICKY;
    }
}