package com.example.nicholasanton.myapplication;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.io.IOException;

/*
After looking at the walking option need to be able to handle any of the options being turned on, or off. Need to consider better options and handle different scenarios.
Consider implemenating the music player here. If most policies wil have a music player consider creating a super class Policy that will have the player and have classes inherit from that when the player is needed
 */

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
