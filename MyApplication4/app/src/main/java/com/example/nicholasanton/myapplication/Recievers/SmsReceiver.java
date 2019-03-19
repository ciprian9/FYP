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
        //attempt to stop the notification from the default sms application
        //android blocked access to do this todo look for new method to block notfications

        //if the bundle is not null
        if (bundle != null){
           //we attepmt to take the pdus from the bundle as it contains the SMS data
           Object[] pdus = (Object[]) bundle.get("pdus");
            String senderNumber;
            //we will loop throguh the pdus as an object can only hold the first 126 characters of the text
            if (pdus != null) {
                for (int i = 0; i < pdus.length; i++){
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    //retrieving the sender number
                    db.insertLog("Get the number and message from text\n");
                    senderNumber = sms.getOriginatingAddress();
                    //retrieving the message
                    String message = sms.getDisplayMessageBody();


                    //START THE walking service and pass an intendt with extra data
                    db.insertLog("Starting Text To Speech Service\n");
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

}