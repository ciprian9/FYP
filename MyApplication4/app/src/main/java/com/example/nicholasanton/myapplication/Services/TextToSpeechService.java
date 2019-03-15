package com.example.nicholasanton.myapplication.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.example.nicholasanton.myapplication.DataHandler;

import java.util.Locale;

import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.autoReply;
import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.notificationTTS;

public class TextToSpeechService extends Service {

//    final class TTSThread implements Runnable{
//        int serviceId;
//
//        TTSThread(int serviceId) {
//            this.serviceId = serviceId;
//        }
//
//        @Override
//        public void run(){
//            synchronized (this){
//                speakTheText();
//            }
//        }
//    }

    private Cursor cursor;
    private String TextMessage = "";
    private TextToSpeech repeatTTS;
    private String sender="";
    private String smsMessage ="";
    private DataHandler db;
    //private Thread thread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //thread = new Thread(new TTSThread(startId));
        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        db = new DataHandler(getApplicationContext());
        if (intent.getStringExtra("sender") != null){
            sender = intent.getStringExtra("sender");
        }

        if (intent.getStringExtra("message") != null){
            smsMessage = intent.getStringExtra("message");
        }
        //thread.start();
        speakTheText();
        return START_STICKY;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void speakTheText() {
        //FOR API 24 USER HAS TO GRANT ACCESS TO THIS
        //AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        if (sender != "" && smsMessage != ""){
            if (isMyServiceRunning(Running_Policy_Service.class) || isMyServiceRunning(Cycling_Policy_Service.class) || isMyServiceRunning(Driving_Policy_Service.class)) {
                if (notificationTTS) {
                    TextMessage = TextMessage + sender + " says " + smsMessage;
                    SetSpeaker();
                }
                if (autoReply) {
                    Intent i = new Intent(this, AutoReplyService.class);
                    i = i.putExtra("sender", sender);
                    startService(i);
                }
            }
        }
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
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            //audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

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
