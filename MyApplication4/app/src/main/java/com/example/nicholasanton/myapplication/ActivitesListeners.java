package com.example.nicholasanton.myapplication;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController.MediaPlayerControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ActivitesListeners extends AppCompatActivity implements MediaPlayerControl {
    private int accountid;
    private ArrayList<song> songList;
    private musicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicController controller;
    private boolean paused = false, playbackPaused=false;

    @Override
    protected void onPause(){
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }

    @Override
    protected void onStop(){
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        songList = new ArrayList<>();

        getSongList();

        Collections.sort(songList, new Comparator<song>() {
            @Override
            public int compare(song o1, song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        //SongAdapter songAdt = new SongAdapter(this, songList);
        //songView.setAdapter(songAdt);

        setController();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                accountid = 0;
            } else {
                accountid = extras.getInt("accountid");
            }
        }

        final Button walkingPolicy = findViewById(R.id.walkingPolicy);
        walkingPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartWalkingService();
            }
        });

        final Button walkingOptions = findViewById(R.id.walkingOptions);
        walkingOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartWalkingOptions();
            }
        });

        final Button runningPolicy = findViewById(R.id.runningPolicy);
        runningPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartRunningPolicy();
            }
        });
    }


    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService.MusicBinder binder = (musicService.MusicBinder)service;
            musicSrv = binder.getService();
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void getSongList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                long thisID = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new song(thisID, thisTitle, thisArtist));
            }
            while(musicCursor.moveToNext());
        }
    }



    public void MakeNotifications(String str, String chanelID){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelID);
        builder.setContentText(str);
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setContentTitle( getString( R.string.app_name ) );
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }

    @Override
    public void onStart(){
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, musicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    @Override
    protected void onDestroy(){
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    public void StartWalkingService(){
        //create a new intent that will start walkingPolicy service
        //MakeNotifications("on Foot", "on Foot");
        Intent intent = new Intent(this, WalkingPolicy.class);
        startService(intent);
        musicSrv.setSong(0);
        musicSrv.playSong();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    public void StartWalkingOptions(){
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, WalkingOptions.class);
        intent.putExtra("accountid", accountid);
        try {
            startActivity(intent);
        } catch (Exception e){
            System.out.printf(e.toString());
        }
    }

    public void StartRunningPolicy(){
        //create a new intent that will start walkingPolicy service
        MakeNotifications("Running", "Running");
        //Running Policy class
        //Intent intent = new Intent(this, WalkingPolicy.class);
        //startService(intent);
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv != null && musicBound && musicSrv.isPng()){
            return musicSrv.getDur();
        }else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null  && musicBound && musicSrv.isPng()){
            return musicSrv.getPosn();
        }else {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound){
            return musicSrv.isPng();
        }else {
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void setController(){
        controller = new MusicController(this);

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }


}
