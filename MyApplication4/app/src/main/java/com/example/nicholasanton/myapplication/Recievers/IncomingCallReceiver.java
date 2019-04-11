package com.example.nicholasanton.myapplication.Recievers;

/**
 * Used code from : https://github.com/Hitman666/AndroidCallBlockingTestDemo
 * Will listen for incoming calls and if the user has enabled it, the code will end the call
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

import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.callReply;
import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.drivingService;
import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.inMeeting;
import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.runningService;
import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.cyclingService;



public class IncomingCallReceiver extends BroadcastReceiver {
    private TelephonyManager tm;
    private com.android.internal.telephony.ITelephony telephonyService;
    private DataHandler db;
    //Listener for the call
    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DataHandler(context);

        db.insertLog("Getting Call\n");
        try {
            //Getting the  number of the number that hasbeen caakku
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    @SuppressLint("PrivateApi") Method m = tm.getClass().getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    telephonyService = (com.android.internal.telephony.ITelephony) m.invoke(tm);
                    if ((number != null)) {
                        //If one of the services are running then the call will end
                        if (runningService || cyclingService  || drivingService || inMeeting) {
                            //Checks if meeting or options are on and if so then send the text message back to the number
                            if (callReply) {
                                SendMsg(context, number, Constants.CALL_REPLY_MESSAGE);
                            } else if (inMeeting){
                                SendMsg(context, number, Constants.MEETING_MESSAGE);
                            }
                        }
                    }
                } catch (Exception e) {
                    //Will run only if there is a problem inside of the code insude abce
                    db.insertLog("Error Inside Call Code");
                    Log.e("TEST : ", e.getMessage());
                    System.out.print(e.toString());
                }
            }
        } catch (Exception e) {
            //Will only run if there is a problem inside of the PHP Script
            db.insertLog("Error Inside Calling Code");
            Log.e("TEST : ", e.getMessage());
        }
    }

    private void SendMsg(Context context, String number, String message){
        db.insertLog("Ending Call\n");
        //Ends the users call
        telephonyService.endCall();
        Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
        //Start the service to return the custom text message using the number
        Intent autoReplyIntent = new Intent(context, AutoReplyService.class);
        autoReplyIntent.putExtra("sender", number);
        autoReplyIntent.putExtra("message", message);
        context.startService(autoReplyIntent);
    }
}
