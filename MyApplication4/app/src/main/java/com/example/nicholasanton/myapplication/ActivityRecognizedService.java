//package com.example.nicholasanton.myapplication;
//
//import android.app.ActivityManager;
//import android.app.IntentService;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.database.Cursor;
//import android.media.AudioManager;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.location.ActivityRecognitionResult;
//import com.google.android.gms.location.DetectedActivity;
//
//import java.util.List;
//
///*
//Need to start working on Transition either with Transition or with Fence API ( Fence and Snapshot are more useful since we can detect beacons)
// */
//
//public class ActivityRecognizedService extends IntentService {
//
//    private DataHandler db;
//    private String ServiceName = "";
//
//    public ActivityRecognizedService() {
//        super("ActivityRecognizedService");
//    }
//
//    public ActivityRecognizedService(String name) {
//        super(name);
//    }
//
//
//    //Start the service and check which activity takes place
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        if(ActivityRecognitionResult.hasResult(intent)) {
//            //get data from passed in intent
//            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
//            //pass most likely activity to the handleDetected Activities
//            handleDetectedActivities( result.getProbableActivities() );
//        }
//    }
//
//    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
//        /*
//        This method will loop around all probable activities and it will use the type to access each case in the switch statement
//        confidence will be used in order to decide which activity is taking place
//         */
//        for( DetectedActivity activity : probableActivities ) {
//            switch( activity.getType() ) {
//                case DetectedActivity.IN_VEHICLE: {
//                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
//                    MakeNotifications("In Vehicle", "In Vehicle");
//                    break;
//                }
//                case DetectedActivity.ON_BICYCLE: {
//                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
//                    MakeNotifications("on Foot", "on Foot");
//                    break;
//                }
//                case DetectedActivity.ON_FOOT: {
//                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
//                    MakeNotifications("on Bicycle", "on Bicycle");
//                    break;
//                }
//                case DetectedActivity.RUNNING: {
//                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
//                    MakeNotifications("Running", "Running");
//                    break;
//                }
//                case DetectedActivity.STILL: {
//                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
//                    //for testing purposes still is the only activity that has the confidence until transition will be implemented
//                    if( activity.getConfidence() >= 75 ) {
//                        //method to create a notification will let the user know the activity for testing purposes
//                        MakeNotifications("Still?", "still");
//                        //begin the walking service
//                        StartWalkingService();
//                    }
//                    break;
//                }
//                case DetectedActivity.TILTING: {
//                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
//                    MakeNotifications("TILTING?", "TILTING");
//                    break;
//                }
//                case DetectedActivity.WALKING: {
//                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
//                    if( activity.getConfidence() >= 75 ) {
//                        MakeNotifications("walking?", "walking");
//                    }
//                    break;
//                }
//                case DetectedActivity.UNKNOWN: {
//                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
//                    MakeNotifications("Unknown", "Unknown");
//                    break;
//                }
//            }
//        }
//    }
//
//    public void StartWalkingService(){
//        //create a new intent that will start walkingPolicy service
//        Intent intent = new Intent(this, WalkingPolicy.class);
//        startService(intent);
//    }
//
//
//    //Create a notification and add it to the android device
//    public void MakeNotifications(String str, String chanelID){
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelID);
//        builder.setContentText(str);
//        builder.setSmallIcon( R.mipmap.ic_launcher );
//        builder.setContentTitle( getString( R.string.app_name ) );
//        NotificationManagerCompat.from(this).notify(0, builder.build());
//    }
//}