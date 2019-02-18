package com.example.nicholasanton.myapplication;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController.MediaPlayerControl;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ActivitesListeners extends AppCompatActivity implements MediaPlayerControl {
    private int accountid;
    private ArrayList<song> songList;
    private musicService musicSrv;
    private Intent playIntent;
    private MusicController controller;
    private boolean paused = false, playbackPaused=false, musicBound = false,
                    musicPlayer = false, pedometer = false, timeRecord = false,
                    dist_speed = false, isPlaying=false, recordRoute = false;
    static boolean notificationTTS;
    static boolean autoReply;
    static boolean callReply;
    static boolean runningService = false;
    static boolean cyclingService = false;
    static boolean drivingService = false;

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

        setController();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                accountid = extras.getInt(Constants.ACCOUNTID_INTENT);
            }
        }

        final Button walkingPolicy = findViewById(R.id.walkingPolicy);
        walkingPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getWalkingSettings(Constants.WALKING_POLICY);
            }
        });

        final Button walkingOptions = findViewById(R.id.walkingOptions);
        walkingOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartWalkingOptions();
            }
        });

        final Button runnningPolicy = findViewById(R.id.runningPolicy);
        runnningPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getWalkingSettings(Constants.RUNNING_POLICY);
            }
        });

        final Button runningOptions = findViewById(R.id.runningOptions);
        runningOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartRunningOptions();
            }
        });

        final Button cyclingPolicy = findViewById(R.id.cyclingPolicy);
        cyclingPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getWalkingSettings(Constants.CYCLING_POLICY);
            }
        });

        final Button cyclingOptions = findViewById(R.id.cyclingOptions);
        cyclingOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartCyclingOptions();
            }
        });

        final Button drivingPolicy = findViewById(R.id.drivingPolicy);
        drivingPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getWalkingSettings(Constants.DRIVING_POLICY);
            }
        });

        final Button drivingOptions = findViewById(R.id.drivingOptions);
        drivingOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartDrivingOptions();
            }
        });

        final Button StopBtn = findViewById(R.id.StopBtn);
        StopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StopWalkingPolicy();
                StopRunningPolicy();
                StopCyclingPolicy();
                StopDrivingPolicy();
            }
        });
    }

    public void StartDrivingOptions(){
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, DrivingOptions.class);
        intent.putExtra("accountid", accountid);
        try {
            startActivity(intent);
        } catch (Exception e){
            System.out.printf(e.toString());
        }
    }

    public void StartCyclingOptions(){
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, CyclingOptions.class);
        intent.putExtra("accountid", accountid);
        try {
            startActivity(intent);
        } catch (Exception e){
            System.out.printf(e.toString());
        }
    }

    private void requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp() {
        //TO SUPPRESS API ERROR MESSAGES IN THIS FUNCTION, since Ive no time to figrure our Android SDK suppress stuff
        if( Build.VERSION.SDK_INT < 23 ) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if ( notificationManager.isNotificationPolicyAccessGranted()) {
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else{
            // Ask the user to grant access
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult( intent, 2 );
        }
    }

    private void turnOffDoNotDisturb() {
        //TO SUPPRESS API ERROR MESSAGES IN THIS FUNCTION, since Ive no time to figrure our Android SDK suppress stuff
        if( Build.VERSION.SDK_INT < 23 ) {
            return;
        }

        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void StartDrivingService(){
        if (recordRoute) {
            Intent i = new Intent(getApplicationContext(), MapService.class);
            i.putExtra("temp", false);
            i.putExtra(Constants.POLICY_ID, 4);
            startService(i);
        }
        requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();
        isPlaying = true;
        Intent intent = new Intent(this, DrivingPolicy.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        intent.putExtra(Constants.MUSIC_INTENT, musicPlayer);
        intent.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        intent.putExtra(Constants.TIME_INTENT, timeRecord);
        intent.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        startService(intent);
        startService(new Intent(this, musicService.class));
        if(musicPlayer) {
            if (controller.getVisibility() == View.GONE) {
                controller.setVisibility(View.VISIBLE);
            }
            musicSrv.setSong(0);
            musicSrv.playSong();
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
            controller.show(0);
        }
    }

    public void StartCyclingService(){
        if (recordRoute){
            Intent i = new Intent(getApplicationContext(), MapService.class);
            i.putExtra("temp", false);
            i.putExtra(Constants.POLICY_ID, 3);
            startService(i);
        }
        Intent speed = new Intent(this, SpeedAndDistance.class);
        startService(speed);
        isPlaying = true;
        Intent intent = new Intent(this, CyclingPolicy.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        intent.putExtra(Constants.MUSIC_INTENT, musicPlayer);
        intent.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        intent.putExtra(Constants.TIME_INTENT, timeRecord);
        intent.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        startService(intent);
        startService(new Intent(this, musicService.class));
        if(musicPlayer) {
            if (controller.getVisibility() == View.GONE) {
                controller.setVisibility(View.VISIBLE);
            }
            musicSrv.setSong(0);
            musicSrv.playSong();
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
            controller.show(0);
        }
    }

    private void StopWalkingPolicy() {
        isPlaying = false;
        if (musicSrv != null) {
            if(musicSrv.isPng()) {
                musicSrv.pausePlayer();
            }
            controller.setVisibility(View.GONE);
            stopService(new Intent(this, musicService.class));
        }
        stopService(new Intent(this, pedometerService.class));
        stopService(new Intent(this, MapService.class));
    }

    private void StopRunningPolicy() {
        isPlaying = false;
        if (musicSrv != null) {
            if(musicSrv.isPng()) {
                musicSrv.pausePlayer();
            }
            controller.setVisibility(View.GONE);
            stopService(new Intent(this, musicService.class));
        }
        stopService(new Intent(this, pedometerService.class));
        stopService(new Intent(this, MapService.class));
    }

    private void StopCyclingPolicy() {
        isPlaying = false;
        if (musicSrv != null) {
            if(musicSrv.isPng()) {
                musicSrv.pausePlayer();
            }
            controller.setVisibility(View.GONE);
            stopService(new Intent(this, musicService.class));
        }
        stopService(new Intent(this, pedometerService.class));
        stopService(new Intent(this, MapService.class));
    }

    private void StopDrivingPolicy() {
        isPlaying = false;
        if (musicSrv != null) {
            if(musicSrv.isPng()) {
                musicSrv.pausePlayer();
            }
            controller.setVisibility(View.GONE);
            stopService(new Intent(this, musicService.class));
        }
        stopService(new Intent(this, pedometerService.class));
        stopService(new Intent(this, MapService.class));
        turnOffDoNotDisturb();
    }

    private void getWalkingSettings(int aPolicyID) {
        readSettings(Constants.MUSIC_SETTING, aPolicyID);
        readSettings(Constants.PEDOMETER_SETTING, aPolicyID);
        readSettings(Constants.TIME_SETTING, aPolicyID);
        readSettings(Constants.DISTANCE_SETTING, aPolicyID);
        readSettings(Constants.RECORD_ROUTE, aPolicyID);
        readSettings(Constants.TEXT_TO_SPEECH_SETTING, aPolicyID);
        readSettings(Constants.AUTO_REPLY_SETTING, aPolicyID);
        readSettings(Constants.CALL_REPLY_SETTING, aPolicyID);
    }

    public void readSettings(final String aName, final int aPolicyID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_READ_SETTING,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (aName) {
                                case Constants.MUSIC_SETTING:
                                    musicPlayer = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.PEDOMETER_SETTING:
                                    pedometer = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.TIME_SETTING:
                                    timeRecord = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.DISTANCE_SETTING:
                                    dist_speed = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.RECORD_ROUTE:
                                    recordRoute = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.TEXT_TO_SPEECH_SETTING:
                                    notificationTTS = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.AUTO_REPLY_SETTING:
                                    autoReply = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.CALL_REPLY_SETTING:
                                    callReply = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    switch (aPolicyID){
                                        case 1:
                                            StartWalkingService();
                                            break;
                                        case 2:
                                            StartRunningService();
                                            runningService = true;
                                            break;
                                        case 3:
                                            StartCyclingService();
                                            cyclingService = true;
                                            break;
                                        case 4:
                                            StartDrivingService();
                                            drivingService = true;
                                            break;
                                    }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.ACCOUNTID_INTENT, String.valueOf(accountid));
                params.put(Constants.POLICY_ID, String.valueOf(aPolicyID));
                params.put(Constants.PARAM_NAME, aName);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService.MusicBinder binder = (musicService.MusicBinder) service;
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
        try (Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null)) {

            if (musicCursor != null && musicCursor.moveToFirst()) {
                int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                do {
                    long thisID = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    songList.add(new song(thisID, thisTitle, thisArtist));
                }
                while (musicCursor.moveToNext());
            }
        }
    }

//    public void MakeNotifications(String str, String chanelID){
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelID);
//        builder.setContentText(str);
//        builder.setSmallIcon( R.mipmap.ic_launcher );
//        builder.setContentTitle( getString( R.string.app_name ) );
//        NotificationManagerCompat.from(this).notify(0, builder.build());
//    }

    @Override
    public void onStart(){
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, musicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

//    public void songPicked(View view){
//        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
//        musicSrv.playSong();
//        if(playbackPaused){
//            setController();
//            playbackPaused=false;
//        }
//        controller.show(0);
//    }

    @Override
    protected void onDestroy(){
        stopService(playIntent);
        unbindService(musicConnection);
        musicSrv=null;
        super.onDestroy();
    }

    public void StartWalkingService() {
        //create a new intent that will start walkingPolicy service
        //MakeNotifications("on Foot", "on Foot");
        if (recordRoute){
            Intent i = new Intent(getApplicationContext(), MapService.class);
            i.putExtra("temp", false);
            i.putExtra(Constants.POLICY_ID, 1);
            startService(i);
        }
        isPlaying = true;
        Intent intent = new Intent(this, WalkingPolicy.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        intent.putExtra(Constants.MUSIC_INTENT, musicPlayer);
        intent.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        intent.putExtra(Constants.TIME_INTENT, timeRecord);
        intent.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        startService(intent);
        startService(new Intent(this, musicService.class));
        if(musicPlayer) {
            if (controller.getVisibility() == View.GONE) {
                controller.setVisibility(View.VISIBLE);
            }
            musicSrv.setSong(0);
            musicSrv.playSong();
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
            controller.show(0);
        }
    }

    public void StartWalkingOptions(){
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, WalkingOptions.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        try {
            startActivity(intent);
        } catch (Exception e){
            System.out.print(e.toString());
        }
    }

    public void StartRunningService(){
        //create a new intent that will start walkingPolicy service
        //MakeNotifications("on Foot", "on Foot");
        if (recordRoute){
            Intent i = new Intent(getApplicationContext(), MapService.class);
            i.putExtra("temp", false);
            i.putExtra(Constants.POLICY_ID, 2);
            startService(i);
        }
        isPlaying = true;
        Intent intent = new Intent(this, RunningPolicy.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        intent.putExtra(Constants.MUSIC_INTENT, musicPlayer);
        intent.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        intent.putExtra(Constants.TIME_INTENT, timeRecord);
        intent.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        startService(intent);
        startService(new Intent(this, musicService.class));
        if(musicPlayer) {
            if (controller.getVisibility() == View.GONE) {
                controller.setVisibility(View.VISIBLE);
            }
            musicSrv.setSong(0);
            musicSrv.playSong();
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
            controller.show(0);
        }
    }

    public void StartRunningOptions(){
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, RunningOptions.class);
        intent.putExtra("accountid", accountid);
        try {
            startActivity(intent);
        } catch (Exception e){
            System.out.printf(e.toString());
        }
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
