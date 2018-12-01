package com.example.nicholasanton.myapplication;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/*
Need to start working on Transition either with Transition or with Fence API ( Fence and Snapshot are more useful since we can detect beacons)
 */

public class ActivityRecognizedService extends IntentService {

    private DataHandler db;

    private String ServiceName = "";

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    MakeNotifications("Are you In Vehicle?", "In Vehicle");
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    MakeNotifications("Are you on Foot?", "on Foot");
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    MakeNotifications("Are you on Bicycle?", "on Bicycle");
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    MakeNotifications("Are you Running?", "Running");
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );

                    if( activity.getConfidence() >= 75 ) {
                        MakeNotifications("Are you Still?", "still");
                        StartWalkingService();
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    MakeNotifications("Are you TILTING?", "TILTING");
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        MakeNotifications("Are you walking?", "walking");
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    MakeNotifications("Unknown", "Unknown");
                    break;
                }
            }
        }
    }

    public void StartWalkingService(){
        Intent intent = new Intent(this, WalkingPolicy.class);
        startService(intent);
    }

    public void MakeNotifications(String str, String chanelID){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelID);
        builder.setContentText(str);
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setContentTitle( getString( R.string.app_name ) );
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }
}