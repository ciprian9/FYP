package com.example.nicholasanton.myapplication.Interfaces;

import com.example.nicholasanton.myapplication.Enums.ActivityType;

public interface ActivityRecognizerListener {
    void connectionFailed();

    void onActivityRecognized(ActivityType activityType);
}
