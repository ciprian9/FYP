package com.example.nicholasanton.myapplication;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service {
    final class SecondThread implements Runnable{
        int serviceId;

        SecondThread(int serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run() {
            synchronized (this) {
                if (stopped){
                    stopSelf(this.serviceId);
                }
                playMusic();
            }
        }
    }
    public static MediaPlayer mediaPlayer;
    private int stopp = 0;
    private boolean stopped = false;
    private ArrayList<String> list;
    private Thread theThread;

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent.getIntExtra("stop", stopp) != 0) {
            intent.getIntExtra("stop", stopp);
        }
        theThread = new Thread(new SecondThread(startId));
        theThread.start();
        return START_STICKY;
    }

    private void playMusic() {
        DataHandler db = new DataHandler(getApplicationContext());
        Cursor res = db.SelectPlaylistQuery(1);
        list = new ArrayList<String>();
        res.moveToFirst();
        for (int i=0;i<res.getCount();i++) {
            list.add(res.getString(Constants.COLUMN_PLAYLIST_LOCATION));
            res.moveToNext();
        }


        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        Uri uri = null;
        for (int i=0;i<list.size();i++) {
            if(mediaPlayer == null){
                break;
            }
            String media_path = list.get(i);
            uri = Uri.parse(media_path);
            try {

                while (mediaPlayer.isPlaying()) {
                    if (stopped) {
                        stopSelf();
                        break;
                    }
                }
                if (!mediaPlayer.isPlaying()) {
                    if (stopped) {
                        stopSelf();
                        break;
                    }
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        theThread.interrupt();
        list.clear();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();


        //stopped = true;
    }
}
