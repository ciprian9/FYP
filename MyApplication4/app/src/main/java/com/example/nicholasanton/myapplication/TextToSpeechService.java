package com.example.nicholasanton.myapplication;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class TextToSpeechService extends Service {

    final class TTSThread implements Runnable{
        int serviceId;

        TTSThread(int serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run(){
            synchronized (this){
                speakTheText();
            }
        }
    }

    private Cursor cursor;
    private String TextMessage = "";
    private TextToSpeech repeatTTS;
    private String sender="";
    private String smsMessage ="";
    private DataHandler db;
    private Thread thread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        thread = new Thread(new TTSThread(startId));
        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        db = new DataHandler(getApplicationContext());
        if (intent.getStringExtra("sender") != null){
            sender = intent.getStringExtra("sender");
        }

        if (intent.getStringExtra("message") != null){
            smsMessage = intent.getStringExtra("message");
        }
        thread.start();
        return START_STICKY;
    }

    private void speakTheText() {
        if (sender != "" && smsMessage != ""){
            cursor = db.SelectSettingsQuery(Constants.TEXT_TO_SPEECH_SETTING);
            if (cursor.moveToFirst()) {
                int temp = cursor.getInt(Constants.COLUMN_SETTINGS_STATUS);
                if (temp == 1) {
                    TextMessage = TextMessage + sender + " says " + smsMessage;
                    SetSpeaker();
                }
            }
        }

        cursor = db.SelectSettingsQuery(Constants.AUTO_REPLY_SETTING);
        if (cursor.moveToFirst()) {
            int temp = cursor.getInt(Constants.COLUMN_SETTINGS_STATUS);
            if (temp == 1 && sender != "") {
                autoReply();
            }
        }
    }

    public void autoReply(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sender, null, "Sorry I'm kind of busy", null, null);
    }

    private void speak(){
        float pitchOfVoice = (float) 0.7;
        repeatTTS.setPitch(pitchOfVoice);
        int speedOfVoice = 1;
        repeatTTS.setSpeechRate(speedOfVoice);
        repeatTTS.speak(TextMessage, TextToSpeech.QUEUE_FLUSH, null);
        while (repeatTTS.isSpeaking()){

        }
        if (!repeatTTS.isSpeaking()){
            Toast.makeText(this, "Stop Talking", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }

    public void SetSpeaker(){
        repeatTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = repeatTTS.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("tts", "Language not supported");
                    }
                    else{
                        speak();
                    }
                }
                else{
                    Log.e("tts", "Initialisation Failed");
                }
            }
        });
    }
}