package com.example.nicholasanton.myapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class musicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer player;
    private ArrayList<song> songs;
    private int songPosn;
    private String songTitle="";
    private static final int NOTIFY_ID=1;
    private AudioManager am;


    private final IBinder musicBind = new MusicBinder();

    public void onCreate(){
        super.onCreate();
        songPosn=0;
        player = new MediaPlayer();
        initMusicPlayer();
        am=(AudioManager)getSystemService(AUDIO_SERVICE);
    }

    public void setList(ArrayList<song> theSongs){
        songs=theSongs;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        player.pause();
    }

    class MusicBinder extends Binder{
        musicService getService(){
            return musicService.this;
        }
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            if(player.getCurrentPosition() == songs.size()){
                mp.reset();
                setSong(0);
                playSong();
            }else {
                mp.reset();
                playNext();
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mp.start();
            Intent notIntent = new Intent(this, ActivitesListeners.class);
            notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentIntent(pendInt)
                    .setSmallIcon(R.drawable.ic_action_play)
                    .setTicker(songTitle)
                    .setOngoing(true)
                    .setContentTitle("Playing")
                    .setContentText(songTitle);
            Notification not = builder.build();

            startForeground(NOTIFY_ID, not);
        }else{
            pausePlayer();
        }
    }

    public void playSong(){
        player.reset();
        if(songPosn > songs.size() -1){
            songPosn = 0;
        }
        song playSong = songs.get(songPosn);
        songTitle = playSong.getTitle();
        long currSong = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source");
        }

        player.prepareAsync();
    }

    public void setSong(int songIndex){
        songPosn = songIndex;
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        songPosn--;
        if(songPosn < 0){
            songPosn=songs.size()-1;
        }
        playSong();
    }

    public void playNext(){
        songPosn++;
        if (songPosn > songs.size()) {
            songPosn = 0;
        }
        playSong();
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
    }

}
