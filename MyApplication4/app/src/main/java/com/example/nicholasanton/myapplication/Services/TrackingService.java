package com.example.nicholasanton.myapplication.Services;

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
    private static TrackingServiceBinder binder = new TrackingServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        activityRecognizer = new ActivityRecognizerImpl(this);
        activityRecognizer.setActivityRecognizerListener(new ActivityRecognizerListener() {
            @Override
            public void connectionFailed(String errorMessage) {}

            @Override
            public void onActivityRecognized(ActivityType activityType) {
                currentActivityType = activityType;
            }
        });

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
        activityRecognizer.stopToRecognizeActivities();
        super.onDestroy();
    }

    public static class TrackingServiceBinder extends Binder {
        public ActivityType getActivityType() {
            return currentActivityType;
        }
    }
}
