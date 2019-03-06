package com.example.nicholasanton.myapplication;

public interface ActivityRecognizer {
    void startToRecognizeActivities();

    void stopToRecognizeActivities();

    void setActivityRecognizerListener(ActivityRecognizerListener activityRecognizerListener);
}
