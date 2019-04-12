package com.example.ciprian.myapplication.Services;

/**
 * Will speak whatever message the user has received by sms
 * */

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.example.ciprian.myapplication.DataHandler;

import java.util.Locale;

import static com.example.ciprian.myapplication.Views.ActivitesListeners.autoReply;
import static com.example.ciprian.myapplication.Views.ActivitesListeners.notificationTTS;

public class TextToSpeechService extends Service {

    private String TextMessage = "";
    private TextToSpeech repeatTTS;
    private String sender="";
    private String smsMessage ="";
    private DataHandler db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        db = new DataHandler(getApplicationContext());
        //read the phone number and message from the intent passed to this service
        if (intent.getStringExtra("sender") != null){
            sender = intent.getStringExtra("sender");
        }

        if (intent.getStringExtra("message") != null){
            smsMessage = intent.getStringExtra("message");
        }
        speakTheText();
        return START_STICKY;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        //check if the activities that allow this service to run are actually running or not
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Will Start speaking the text and auto reply if the user has the setting selected
    private void speakTheText() {
        if (!sender.equals("") && !smsMessage.equals("")){
            //if one of the following is running
            if (isMyServiceRunning(Running_Policy_Service.class) || isMyServiceRunning(Cycling_Policy_Service.class) || isMyServiceRunning(Driving_Policy_Service.class)) {
                //and the user setting is set to on
                if (notificationTTS) {
                    //build the message string
                    TextMessage = TextMessage + sender + " says " + smsMessage;
                    //call to set speaker preparing the TTS
                    SetSpeaker();
                }
                //If user preference for auto reply is on
                if (autoReply) {
                    //Start auto reply service
                    Intent i = new Intent(this, AutoReplyService.class);
                    i = i.putExtra("sender", sender);
                    startService(i);
                }
            }
        }
    }

    //Speaks the text
    private void speak(){
        db.insertLog("Speaking Text Message");
        //build up the voice for the TTS
        float pitchOfVoice = (float) 0.7;
        //set the pitch of the voice
        repeatTTS.setPitch(pitchOfVoice);
        int speedOfVoice = 1;
        //set the speed of the voice
        repeatTTS.setSpeechRate(speedOfVoice);
        //use TTS to speak out the message
        repeatTTS.speak(TextMessage, TextToSpeech.QUEUE_FLUSH, null);
        //run indefinatly while the TTS is speaking
        while (repeatTTS.isSpeaking()){

        }
        //once it is done speaking
        if (!repeatTTS.isSpeaking()){
            Toast.makeText(this, "Stop Talking", Toast.LENGTH_SHORT).show();
            //stop TTS
            stopSelf();
        }
    }

    //Sets some parameters required for TTS to work properly
    private void SetSpeaker(){
        repeatTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = repeatTTS.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("tts :", "Language not supported");
                    }
                    else{
                        speak();
                    }
                }
                else{
                    Log.e("tts :", "Initialisation Failed");
                }
            }
        });
    }
}
