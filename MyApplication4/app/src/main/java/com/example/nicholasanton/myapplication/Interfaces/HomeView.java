package com.example.nicholasanton.myapplication.Interfaces;

import com.example.nicholasanton.myapplication.Enums.ActivityType;

public interface HomeView {

    void show(ActivityType activityType);

    void warnTracking();

    void warnTrackingHasBeenStopped();
}
