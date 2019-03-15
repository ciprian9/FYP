package com.example.nicholasanton.myapplication.Interfaces;

public interface ActivityRecognizer {
    void startToRecognizeActivities();

    void stopToRecognizeActivities();

    void setActivityRecognizerListener(ActivityRecognizerListener activityRecognizerListener);
}
