package com.example.nicholasanton.myapplication.Recievers;

/**
 * Used code from : https://github.com/Hitman666/AndroidCallBlockingTestDemo
 * Will listen for incoming calls and if enabled will end the call
 * */

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.Services.AutoReplyService;

import java.lang.reflect.Method;

import com.example.nicholasanton.myapplication.ITelephony;

import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.callReply;
import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.drivingService;
import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.inMeeting;
import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.runningService;
import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.cyclingService;

public class IncomingCallReceiver extends BroadcastReceiver {
    private TelephonyManager tm;
    private ITelephony telephonyService;
    private DataHandler db;
    //Listener for the call
    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DataHandler(context);

        db.insertLog("Getting Call\n");
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    @SuppressLint("PrivateApi") Method m = tm.getClass().getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    telephonyService = (ITelephony) m.invoke(tm);
                    if ((number != null)) {
                        //If one of the services are running then the call will end
                        if (runningService || cyclingService  || drivingService || inMeeting) {
                            if (callReply) {
                                SendMsg(context, number, Constants.CALL_REPLY_MESSAGE);
                            } else if (inMeeting){
                                SendMsg(context, number, Constants.MEETING_MESSAGE);
                            }
                        }
                    }
                } catch (Exception e) {
                    db.insertLog("Error Inside Call Code");
                    Log.e("TEST : ", e.getMessage());
                    System.out.print(e.toString());
                }
            }
        } catch (Exception e) {
            db.insertLog("Error Inside Calling Code");
            Log.e("TEST : ", e.getMessage());
        }
    }

    private void SendMsg(Context context, String number, String message){
        db.insertLog("Ending Call\n");
        telephonyService.endCall();
        Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
        Intent autoReplyIntent = new Intent(context, AutoReplyService.class);
        autoReplyIntent.putExtra("sender", number);
        autoReplyIntent.putExtra("message", message);
        context.startService(autoReplyIntent);
    }
}
