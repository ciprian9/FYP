package com.example.nicholasanton.myapplication.Interfaces;

/**
 * Interface used to initialize procedures for the activity recognition listeners
 * */

import com.example.nicholasanton.myapplication.Enums.ActivityType;

public interface ActivityRecognizerListener {
    void connectionFailed();

    void onActivityRecognized(ActivityType activityType);
}
