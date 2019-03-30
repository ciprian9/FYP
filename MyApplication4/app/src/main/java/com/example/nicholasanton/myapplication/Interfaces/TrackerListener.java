package com.example.nicholasanton.myapplication.Interfaces;

/**
 * Listens for user movement
 * */

import com.example.nicholasanton.myapplication.Enums.ActivityType;

public interface TrackerListener {
    void onTracked(ActivityType activityType);
}
