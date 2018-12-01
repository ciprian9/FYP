package com.example.nicholasanton.myapplication;

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

       if (SMS_RECEIVED.equals(intent.getAction())){
           this.abortBroadcast();
        }

        if (bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            String senderNumber = null;
            for (int i = 0; i < pdus.length; i++){
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

                senderNumber = sms.getOriginatingAddress();

                String message = sms.getDisplayMessageBody();

                Intent walkingIntent = new Intent(context, WalkingPolicy.class);
                walkingIntent.putExtra("sender", senderNumber);
                walkingIntent.putExtra("message", message);
                context.startService(walkingIntent);

                Toast.makeText(context, "From: "+ senderNumber+" Message: " + message, Toast.LENGTH_LONG).show();
            }
        }
    }
}