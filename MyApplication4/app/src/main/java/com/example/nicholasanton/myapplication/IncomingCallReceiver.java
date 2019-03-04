package com.example.nicholasanton.myapplication;

//https://github.com/Hitman666/AndroidCallBlockingTestDemo

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import static com.example.nicholasanton.myapplication.ActivitesListeners.callReply;
import static com.example.nicholasanton.myapplication.ActivitesListeners.drivingService;
import static com.example.nicholasanton.myapplication.ActivitesListeners.inMeeting;
import static com.example.nicholasanton.myapplication.ActivitesListeners.runningService;
import static com.example.nicholasanton.myapplication.ActivitesListeners.cyclingService;

public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ITelephony telephonyService;
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Method m = tm.getClass().getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    telephonyService = (ITelephony) m.invoke(tm);
                    if ((number != null)) {
                        if (runningService || cyclingService  || drivingService || inMeeting) {
                            if (callReply || inMeeting) {
                                telephonyService.endCall();
                                Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
                                Intent autoReplyIntent = new Intent(context, AutoReplyService.class);
                                autoReplyIntent.putExtra("sender", number);
                                context.startService(autoReplyIntent);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.printf(e.toString());
                }
            }
//            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
//            }
//            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
