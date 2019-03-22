package com.example.nicholasanton.myapplication.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Services.TextToSpeechService;

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        DataHandler db = new DataHandler(context);
        Bundle bundle = intent.getExtras();

        if (bundle != null){
           Object[] pdus = (Object[]) bundle.get("pdus");
            String senderNumber;
            if (pdus != null) {
                for (int i = 0; i < pdus.length; i++){
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    db.insertLog("Get the number and message from text\n");
                    senderNumber = sms.getOriginatingAddress();
                    String message = sms.getDisplayMessageBody();

                    db.insertLog("Starting Text To Speech Service\n");
                    Intent textIntent = new Intent(context, TextToSpeechService.class);
                    textIntent.putExtra("sender", senderNumber);
                    textIntent.putExtra("message", message);
                    context.startService(textIntent);

                    Toast.makeText(context, "From: "+ senderNumber+" Message: " + message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}