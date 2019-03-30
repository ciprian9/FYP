package com.example.nicholasanton.myapplication.Interfaces;

/**
 * Will track Activities
 * */

public interface Tracker {
    void stopTrackingService();

    void startTrackingService();

    void setTrackerListener(TrackerListener trackerListener);

    boolean isTracking();
}
