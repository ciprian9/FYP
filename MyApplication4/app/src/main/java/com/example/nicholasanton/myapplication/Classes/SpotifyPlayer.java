package com.example.nicholasanton.myapplication.Classes;

/**
 * Class that plays the right spotify playlist based on what activity the user is performing
 * */

import android.util.Log;
import java.util.Random;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistsPager;

public class SpotifyPlayer{

    //Creates a thread to be able to run this at the same time as other things
    final class SecondThread implements Runnable{
        @Override
        public void run() {
            synchronized (this) {
                doInBackground();
            }
        }
    }

    private final SpotifyAppRemote mSpotifyAppRemote;
    private final String mAccessToken;
    public final String ActivityTypeString;

    public SpotifyPlayer(SpotifyAppRemote spotifyAppRemote, String accessToken, String activityTypeString){
        this.mSpotifyAppRemote = spotifyAppRemote;
        this.mAccessToken = accessToken;
        this.ActivityTypeString = activityTypeString;
    }

    //Procedure that will play the appropriate playlist based on the activity of the user
    private void doInBackground() {
        try {
            SpotifyApi api = new SpotifyApi();
            //Set the access token to be able to connect to the spotify
            api.setAccessToken(mAccessToken);
            SpotifyService spotify = api.getService();
            PlaylistsPager walking = null;
            // Checks if the current activity is an action being performed and if so it will play
            // appropriate music
            if(ActivityTypeString.equalsIgnoreCase("WALK")) {
                //Will use spotify to search for the right playlist
                walking = spotify.searchPlaylists("walking%music");
            } else if(ActivityTypeString.equalsIgnoreCase("RUN")) {
                walking = spotify.searchPlaylists("running%music");
            } else if(ActivityTypeString.equalsIgnoreCase("BIKE")) {
                walking = spotify.searchPlaylists("cycling");
            } else if(ActivityTypeString.equalsIgnoreCase("CAR")) {
                walking = spotify.searchPlaylists("driving%music");
            }
            //Used to get a random number
            Random rand = new Random();
            int n = 0;
            if (walking != null) {
                n = rand.nextInt(walking.playlists.items.size());
            }
            if (!ActivityTypeString.isEmpty()) {
                if (walking != null) {
                    //Will play a random playlist depending on what number is randomly picked
                    mSpotifyAppRemote.getPlayerApi().play(walking.playlists.items.get(n).uri);
                }
            } else {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        } catch (Exception e){
            Log.d("HELP1234", e.toString());
        }
    }

    //Pause the current playing song
    public void pause(){
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    //Start the thread to initialize the spotify player
    public void init(){
        Thread theThread = new Thread(new SecondThread());
        theThread.start();
    }
}
