package com.example.nicholasanton.myapplication;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        //attempt to stop the notification from the default sms application
        //android blocked access to do this todo look for new method to block notfications

        //if the bundle is not null
        if (bundle != null){
           //we attepmt to take the pdus from the bundle as it contains the SMS data
           Object[] pdus = (Object[]) bundle.get("pdus");
            String senderNumber = null;
            //we will loop throguh the pdus as an object can only hold the first 126 characters of the text
            for (int i = 0; i < pdus.length; i++){
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

                //retrieving the sender number
                senderNumber = sms.getOriginatingAddress();
                //retrieving the message
                String message = sms.getDisplayMessageBody();


                //START THE walking service and pass an intendt with extra data
                Intent textIntent = new Intent(context, TextToSpeechService.class);
                textIntent.putExtra("sender", senderNumber);
                textIntent.putExtra("message", message);
                //context.startService(walkingIntent);
                context.startService(textIntent);

                Toast.makeText(context, "From: "+ senderNumber+" Message: " + message, Toast.LENGTH_LONG).show();
            }
        }
    }

}