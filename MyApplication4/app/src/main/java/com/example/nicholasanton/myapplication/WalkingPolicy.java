package com.example.nicholasanton.myapplication;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
After looking at the walking option need to be able to handle any of the options being turned on, or off. Need to consider better options and handle different scenarios.
Consider implementing the music player here. If most policies wil have a music player consider creating a super class Policy that will have the player and have classes inherit from that when the player is needed
 */

public class WalkingPolicy extends IntentService {

    MediaPlayer mediaPlayer;
    boolean prepared = false;
    String stream = "http://stream.radioreklama.bg:80/radio1rock128";
    private Cursor cursor;
    private boolean isHeadsetOn;
    private String TextMessage = "";
    private TextToSpeech repeatTTS;

    public WalkingPolicy(){
        super("My_Walking_Policy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Has Been Destroyed", Toast.LENGTH_SHORT).show();
        //super.onDestroy();
    }

    public void StartMusic(){
        if(cursor.moveToFirst()) {
            int temp = cursor.getInt(2);
            if (isHeadsetOn && temp == 1) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                new PlayerTask().execute(stream);
            }
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void SpeakTextMessage() {
//        Toast.makeText(this, "Voice started", Toast.LENGTH_SHORT).show();
//        if (cursor.moveToLast()) {
//            int temp = cursor.getInt(2);
//            if (temp == 1) {
//                Intent intent = new Intent(getApplicationContext(), WalkingPolicy.class);
//                Bundle bundle = intent.getBundleExtra("mySMS");
//
//                if (bundle != null) {
//                    Object[] pdus = (Object[]) bundle.get("pdus");
//                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);
//                    Log.i("mobile.cs.fsu.edu", "smsActivity : SMS is <" + sms.getMessageBody() + ">");
//
//                    //strip flag
//                    String message = sms.getMessageBody();
//                    while (message.contains("FLAG"))
//                        message = message.replace("FLAG", "");
//
//                    TextMessage = message;
//                } else
//                    Log.i("mobile.cs.fsu.edu", "smsActivity : NULL SMS bundle");
//
//                repeatTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//                    @Override
//                    public void onInit(int status) {
//                        if (status == TextToSpeech.SUCCESS) {
//                            int result = repeatTTS.setLanguage(Locale.ENGLISH);
//
//                            if (result == TextToSpeech.LANG_MISSING_DATA
//                                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                                Log.e("TTS", "Language Not Supported");
//                            } else {
//                                speak();
//                            }
//                        } else {
//                            Log.e("TTS", "Initialization Failed");
//                        }
//                    }
//                });
//            }
//        }
//        Toast.makeText(this, "Voice finished", Toast.LENGTH_SHORT).show();
//    }

    private void speak(){
        if (TextMessage.equals("")) {
            float pitchOfVoice = (float) 0.7;
            repeatTTS.setPitch(pitchOfVoice);
            int speedOfVoice = 1;
            repeatTTS.setSpeechRate(speedOfVoice);
            repeatTTS.speak(TextMessage, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void SaveResources(){
        if (cursor.moveToFirst()) {
            cursor.moveToNext();
            cursor.moveToNext();
            int temp = cursor.getInt(2);
            if (temp == 1) {
                IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = this.registerReceiver(null, iFilter);

                int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;

                Toast.makeText(this, "BATTERY", Toast.LENGTH_SHORT).show();

                if (level <= 97) {
                    List<ApplicationInfo> packages;
                    PackageManager pm;
                    pm = getPackageManager();
                    //get a list of installed apps.
                    packages = pm.getInstalledApplications(0);

                    ActivityManager mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                    String myPackage = getApplicationContext().getPackageName();
                    for (ApplicationInfo packageInfo : packages) {
                        if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
                        if (packageInfo.packageName.equals(myPackage)) continue;
                        mActivityManager.killBackgroundProcesses(packageInfo.packageName);
                        //                pm.setApplicationEnabledSetting(packageInfo.packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 0);
                        //                pm.setApplicationEnabledSetting(packageInfo.packageName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
                    }

                    Toast.makeText(this, "DIE", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onHandleIntent(Intent intent) {
        DataHandler db = new DataHandler(getApplicationContext());
        cursor = db.SelectSettingsQuery();
        getApplicationContext();
        AudioManager au = (AudioManager)getSystemService(AUDIO_SERVICE);
        isHeadsetOn = au.isWiredHeadsetOn();
        if (mediaPlayer == null){
            StartMusic();
        }

        SaveResources();
        //SpeakTextMessage();
    }

    @SuppressLint("StaticFieldLeak")
    class PlayerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e){
                e.printStackTrace();
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean){
            super.onPostExecute(aBoolean);
            mediaPlayer.start();
        }
    }
}
