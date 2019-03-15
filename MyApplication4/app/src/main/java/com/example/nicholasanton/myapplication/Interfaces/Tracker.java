package com.example.nicholasanton.myapplication.Interfaces;

public interface Tracker {
    void stopTrackingService();

    void startTrackingService();

    void setTrackerListener(TrackerListener trackerListener);

    boolean isTracking();
}
