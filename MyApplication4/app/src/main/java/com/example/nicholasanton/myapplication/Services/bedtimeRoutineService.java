package com.example.nicholasanton.myapplication.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;

import com.example.nicholasanton.myapplication.DataHandler;

public class bedtimeRoutineService extends Service {

    private DataHandler db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new DataHandler(this);
        db.insertLog("Start Night Routine");
        turnOnDoNotDisturb();
        return super.onStartCommand(intent, flags, startId);
    }

    private void turnOnDoNotDisturb() {
        db.insertLog("Silent ON");
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
}
