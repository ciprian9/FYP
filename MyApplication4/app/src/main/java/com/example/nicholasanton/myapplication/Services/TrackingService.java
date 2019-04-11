package com.example.nicholasanton.myapplication.Services;

/**
 *  Will return the users activity to wherever it is being called
 *  */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.nicholasanton.myapplication.Interfaces.ActivityRecognizer;
import com.example.nicholasanton.myapplication.ActivityRecognizerImpl;
import com.example.nicholasanton.myapplication.Interfaces.ActivityRecognizerListener;
import com.example.nicholasanton.myapplication.Enums.ActivityType;

public class TrackingService extends Service {

    private static ActivityRecognizer activityRecognizer;
    private static ActivityType currentActivityType;
    private static final TrackingServiceBinder binder = new TrackingServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //create an instance of activity recognition
        activityRecognizer = new ActivityRecognizerImpl(this);
        //set the listener for activity recognition
        activityRecognizer.setActivityRecognizerListener(new ActivityRecognizerListener() {

            @Override
            public void connectionFailed() {

            }

            @Override
            public void onActivityRecognized(ActivityType activityType) {
                //if connection was successful retrieve the current activity
                currentActivityType = activityType;
            }
        });

        //start the activity recognizer to determine current activity
        activityRecognizer.startToRecognizeActivities();

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        //stop activity tracking
        activityRecognizer.stopToRecognizeActivities();
        super.onDestroy();
    }

    public static class TrackingServiceBinder extends Binder {
        public ActivityType getActivityType() {
            return currentActivityType;
        }
    }
}
