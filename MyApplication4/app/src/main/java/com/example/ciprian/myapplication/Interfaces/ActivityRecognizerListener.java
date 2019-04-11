package com.example.ciprian.myapplication.Interfaces;

/**
 * Interface used to initialize procedures for the activity recognition listeners
 * */

import com.example.ciprian.myapplication.Enums.ActivityType;

public interface ActivityRecognizerListener {
    void connectionFailed();

    void onActivityRecognized(ActivityType activityType);
}
