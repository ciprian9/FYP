package com.example.nicholasanton.myapplication;

public interface HomeView {

    void show(ActivityType activityType);

    void warnTracking();

    void warnTrackingHasBeenStopped();
}
