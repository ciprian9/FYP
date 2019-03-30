package com.example.nicholasanton.myapplication.Interfaces;

/**
 * Interface used to initialize procedures for the activity recognition
 * */

import com.example.nicholasanton.myapplication.Enums.ActivityType;

public interface HomeView {

    void show(ActivityType activityType);

    void warnTracking();

    void warnTrackingHasBeenStopped();
}
