package com.example.nicholasanton.myapplication;

public interface ActivityRecognizerListener {
    void connectionFailed(String errorMessage);

    void onActivityRecognized(ActivityType activityType);
}
