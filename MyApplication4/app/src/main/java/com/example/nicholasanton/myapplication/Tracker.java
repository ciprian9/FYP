package com.example.nicholasanton.myapplication;

public interface Tracker {
    void stopTrackingService();

    void startTrackingService();

    void setTrackerListener(TrackerListener trackerListener);

    boolean isTracking();
}
