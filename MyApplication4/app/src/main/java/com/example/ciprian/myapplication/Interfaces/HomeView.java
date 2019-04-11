package com.example.ciprian.myapplication.Interfaces;

/**
 * Interface used to initialize procedures for the activity recognition
 * */

import com.example.ciprian.myapplication.Enums.ActivityType;

public interface HomeView {

    void show(ActivityType activityType);

    void warnTracking();

    void warnTrackingHasBeenStopped();
}
