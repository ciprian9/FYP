package com.example.ciprian.myapplication.Services;

/**
 * Will reply to a text or call if it is enabled and if any services are running or if the user is inside of a meeting
 * */

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.example.ciprian.myapplication.DataHandler;

import static com.example.ciprian.myapplication.Views.ActivitesListeners.inMeeting;

public class AutoReplyService extends Service {

	//Creates a thread so this is able to run in the background while other proccesses are running
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
    private String message="";
    private DataHandler db;

    //Only replies to a number if one of the policies are running and if the user is in a meeting and will respond with the appropiate message
    private void autoReply() {
        if (isMyServiceRunning(Running_Policy_Service.class) || isMyServiceRunning(Cycling_Policy_Service.class) || isMyServiceRunning(Driving_Policy_Service.class) || inMeeting) {
            db.insertLog("Replied to Text");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sender, null, message, null, null);
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

    //Used to check if any of the policies are running and if so it will return true or false
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Will recieve the phone number and the appropiate message and will then start the thread to proceed to send the message
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        db = new DataHandler(this);
        db.insertLog("Started AutoReply");
        //Checks if it got data back and if so it will put values inside of variables
        if (intent.getStringExtra("sender") != null){
            sender = intent.getStringExtra("sender");
            message = intent.getStringExtra("message");
        }
        //Create and start the thread
        theThread = new Thread(new SecondThread(startId));
        theThread.start();
        return START_STICKY;
    }
}