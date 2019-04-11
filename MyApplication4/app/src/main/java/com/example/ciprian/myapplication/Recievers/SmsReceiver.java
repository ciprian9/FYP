package com.example.ciprian.myapplication.Recievers;

/**
 * Will listen for incoming messages and if enabled will start text to speech service.
 * */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.ciprian.myapplication.DataHandler;
import com.example.ciprian.myapplication.Services.TextToSpeechService;

public class SmsReceiver extends BroadcastReceiver {

    //Will listen for the users mobile phone to perform what is needed
    @Override
    public void onReceive(Context context, Intent intent) {
        DataHandler db = new DataHandler(context);
        Bundle bundle = intent.getExtras();

        //Check if there is data recieved to avoid
        if (bundle != null){
           Object[] pdus = (Object[]) bundle.get("pdus");
            String senderNumber;
            if (pdus != null) {
                for (int i = 0; i < pdus.length; i++){
                    //Will get info for the message and use it
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    //Gets the number and message
                    db.insertLog("Get the number and message from text\n");
                    senderNumber = sms.getOriginatingAddress();
                    String message = sms.getDisplayMessageBody();

                    //Starts text to speechh
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