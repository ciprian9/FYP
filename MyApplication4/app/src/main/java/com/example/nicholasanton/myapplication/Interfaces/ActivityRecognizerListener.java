package com.example.nicholasanton.myapplication.Interfaces;

import com.example.nicholasanton.myapplication.Enums.ActivityType;

public interface ActivityRecognizerListener {
    void connectionFailed(String errorMessage);

    void onActivityRecognized(ActivityType activityType);
}
