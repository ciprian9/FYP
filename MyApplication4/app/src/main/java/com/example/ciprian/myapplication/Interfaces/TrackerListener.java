package com.example.ciprian.myapplication.Interfaces;

/**
 * Listens for user movement
 * */

import com.example.ciprian.myapplication.Enums.ActivityType;

public interface TrackerListener {
    void onTracked(ActivityType activityType);
}
