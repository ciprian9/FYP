package com.example.nicholasanton.myapplication.Classes;

import android.util.Log;
import java.util.Random;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistsPager;

public class SpotifyPlayer{

    final class SecondThread implements Runnable{
        @Override
        public void run() {
            synchronized (this) {
                doInBackground();
            }
        }
    }

    private SpotifyAppRemote mSpotifyAppRemote;
    private String mAccessToken;
    public String ActivityTypeString;

    public SpotifyPlayer(SpotifyAppRemote spotifyAppRemote, String accessToken, String activityTypeString){
        this.mSpotifyAppRemote = spotifyAppRemote;
        this.mAccessToken = accessToken;
        this.ActivityTypeString = activityTypeString;
    }


    private void doInBackground() {
        try {
            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(mAccessToken);
            SpotifyService spotify = api.getService();
            PlaylistsPager walking = null;
            if(ActivityTypeString.equalsIgnoreCase("WALK")) {
                walking = spotify.searchPlaylists("walking%music");
            } else if(ActivityTypeString.equalsIgnoreCase("RUNNING")) {
                walking = spotify.searchPlaylists("running%music");
            } else if(ActivityTypeString.equalsIgnoreCase("BIKE")) {
                walking = spotify.searchPlaylists("cycling%music");
            } else if(ActivityTypeString.equalsIgnoreCase("CAR")) {
                walking = spotify.searchPlaylists("driving%music");
            }
            Random rand = new Random();
            int n = 0;
            if (walking != null) {
                n = rand.nextInt(walking.playlists.items.size());
            }
            if (!ActivityTypeString.isEmpty()) {
                if (walking != null) {
                    mSpotifyAppRemote.getPlayerApi().play(walking.playlists.items.get(n).uri);
                }
            } else {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        } catch (Exception e){
            Log.d("HELP1234", e.toString());
        }
    }

    public void init(){
        Thread theThread = new Thread(new SecondThread());
        theThread.start();
    }

//    private class LongOperation extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//
//        }
//    }
}
