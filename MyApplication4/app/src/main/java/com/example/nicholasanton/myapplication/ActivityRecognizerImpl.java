package com.example.nicholasanton.myapplication;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.nicholasanton.myapplication.Enums.ActivityType;
import com.example.nicholasanton.myapplication.Interfaces.ActivityRecognizer;
import com.example.nicholasanton.myapplication.Interfaces.ActivityRecognizerListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;

import org.jetbrains.annotations.NotNull;

public class ActivityRecognizerImpl implements ActivityRecognizer, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final long ACTIVITY_RECOGNITION_INTERVAL = 10000;
    private final GoogleApiClient googleApiClient;
    private static ActivityRecognizerListener activityRecognizerListener;
    private final Context context;

    public ActivityRecognizerImpl(Context context) {
        this.context = context;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    public void startToRecognizeActivities() {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NotNull ConnectionResult connectionResult) {
        activityRecognizerListener.connectionFailed();
        stopToRecognizeActivities();
    }
    @SuppressWarnings("deprecation")
    @Override
    public void stopToRecognizeActivities() {
        if (googleApiClient.isConnected()) {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                    googleApiClient,
                    PendingIntent.getService(context, 0,
                            new Intent(context, ActivityRecognitionIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT)
            );
            googleApiClient.disconnect();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onConnected(Bundle bundle) {
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                googleApiClient,
                ACTIVITY_RECOGNITION_INTERVAL,
                PendingIntent.getService(context, 0,
                        new Intent(context, ActivityRecognitionIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT)
        );
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void setActivityRecognizerListener(ActivityRecognizerListener activityRecognizerListener) {
        ActivityRecognizerImpl.activityRecognizerListener = activityRecognizerListener;
    }

    public static class ActivityRecognitionIntentService extends IntentService {

        public ActivityRecognitionIntentService() {
            super("ACTIVITYRECOGNITION_INTENTSERVICE_NAME");
        }

        @Override
        protected void onHandleIntent(final Intent intent) {
            if (ActivityRecognitionResult.hasResult(intent)) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        activityRecognizerListener.onActivityRecognized(
                                ActivityType.values()[ActivityRecognitionResult.extractResult(intent).getMostProbableActivity().getType()]
                        );
                    }
                });
            }
        }
    }
}
