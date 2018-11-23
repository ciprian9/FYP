package com.example.nicholasanton.myapplication;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.io.IOException;

public class WalkingPolicy {

    MediaPlayer mediaPlayer;
    boolean prepared = false;
    boolean started = false;
    String stream = "http://stream.radioreklama.bg:80/radio1rock128";

    public void StartMusic(boolean isHeadsetOn){
        if (!isHeadsetOn) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            new PlayerTask().execute(stream);
        }
    }

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
