package com.example.nicholasanton.myapplication.Interfaces;

/**
 * Interface used to initialize procedures for the activity recognition
 * */

public interface ActivityRecognizer {
    void startToRecognizeActivities();

    void stopToRecognizeActivities();

    void setActivityRecognizerListener(ActivityRecognizerListener activityRecognizerListener);
}
