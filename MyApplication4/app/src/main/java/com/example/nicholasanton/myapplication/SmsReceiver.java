package com.example.nicholasanton.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            String senderNumber = null;
            for (int i = 0; i < pdus.length; i++){
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

                senderNumber = sms.getOriginatingAddress();

                String message = sms.getDisplayMessageBody();

                Toast.makeText(context, "From: "+ senderNumber+" Message: " + message, Toast.LENGTH_LONG).show();
            }

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(senderNumber, null, "Sorry I'm kind of busy", null, null);

        }
    }
}