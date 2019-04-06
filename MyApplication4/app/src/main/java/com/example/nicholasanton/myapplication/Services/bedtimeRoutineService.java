package com.example.nicholasanton.myapplication.Services;

/**
 * This service will only be run when the night time is reached
 * */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;

import com.example.nicholasanton.myapplication.DataHandler;

public class bedtimeRoutineService extends Service {

    private DataHandler db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Will call the procedure to turn do not disturb on
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new DataHandler(this);
        db.insertLog("Start Night Routine");
        turnOnDoNotDisturb();
        return super.onStartCommand(intent, flags, startId);
    }

    //Will access the audio manager of the device and then turn the ringer off
    private void turnOnDoNotDisturb() {
        db.insertLog("Silent ON");
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
}
