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
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
After looking at the walking option need to be able to handle any of the options being turned on, or off. Need to consider better options and handle different scenarios.
Consider implementing the music player here. If most policies wil have a music player consider creating a super class Policy that will have the player and have classes inherit from that when the player is needed

 TODO
 //Service ends too early not given the chance to speak we need to keep the service running until the whole run is over
 //Try creating code for tracking usage / screen time is enough for now


 */

public class  WalkingPolicy extends IntentService {

    MediaPlayer mediaPlayer;
    boolean prepared = false;
    String stream = "http://stream.radioreklama.bg:80/radio1rock128";
    private Cursor cursor;
    private boolean isHeadsetOn;
    private String TextMessage = "";
    private TextToSpeech repeatTTS;
    private String sender="";
    private String smsMessage ="";
    private DataHandler db;

    public WalkingPolicy(){
        super("My_Walking_Policy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Has Started", Toast.LENGTH_SHORT).show();
        if (intent.getStringExtra("sender") != null){
            sender = intent.getStringExtra("sender");
        }

        if (intent.getStringExtra("message") != null){
            smsMessage = intent.getStringExtra("message");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Has Been Destroyed", Toast.LENGTH_SHORT).show();
        //super.onDestroy();
    }

    public void StartMusic(){
        cursor = db.SelectSettingsQuery(Constants.HEADPHONE_SETTING);
        if(cursor.moveToFirst()) {
            int temp = cursor.getInt(Constants.COLUMN_SETTINGS_STATUS);
            if (isHeadsetOn && temp == 1) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                new PlayerTask().execute(stream);
            }
        }
    }

    public void SetSpeaker(){
        repeatTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = repeatTTS.setLanguage(Locale.GERMAN);
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


    //Service ends too early not given the chance to speak we need to keep the service running until the whole run is over
    //Notifications seem to be unable to be stoped need to do more research
    private void speak(){
        float pitchOfVoice = (float) 0.7;
        repeatTTS.setPitch(pitchOfVoice);
        int speedOfVoice = 1;
        repeatTTS.setSpeechRate(speedOfVoice);
        repeatTTS.speak(TextMessage, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void SaveResources(){
        cursor = db.SelectSettingsQuery(Constants.SAVE_RESOURCE_SETTING);
        if (cursor.moveToFirst()) {
            int temp = cursor.getInt(Constants.COLUMN_SETTINGS_STATUS);
            if (temp == 1) {
                IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = this.registerReceiver(null, iFilter);

                int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;

                Toast.makeText(this, "BATTERY", Toast.LENGTH_SHORT).show();

                cursor = db.SelectSettingsQuery(Constants.BATTERY_LEVEL);
                cursor.moveToFirst();
                if (level <= cursor.getInt(Constants.COLUMN_SETTINGS_BATTERY)) {
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

    @Override
    protected void onHandleIntent(Intent intent) {
        db = new DataHandler(getApplicationContext());
        getApplicationContext();
        AudioManager au = (AudioManager)getSystemService(AUDIO_SERVICE);
        isHeadsetOn = au.isWiredHeadsetOn();
        if (mediaPlayer == null){
            StartMusic();
        }

        SaveResources();
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
