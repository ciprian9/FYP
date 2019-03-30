package com.example.nicholasanton.myapplication.Classes;

/**
 * File to start Activity recognition
 * */

import com.example.nicholasanton.myapplication.Enums.ActivityType;
import com.example.nicholasanton.myapplication.Interfaces.HomeView;
import com.example.nicholasanton.myapplication.Interfaces.Tracker;
import com.example.nicholasanton.myapplication.Interfaces.TrackerListener;

public class HomePresenter {
    private final HomeView view;
    private final Tracker tracker;

    //Constructor for the class that retrieves HomeView and Tracker object that copies to local variables
    public HomePresenter(HomeView view, Tracker tracker) {
        this.view = view;
        this.tracker = tracker;
    }

    //Starts the listener for activity
    public void init() {
        tracker.setTrackerListener(new TrackerListener() {
            @Override
            public void onTracked(ActivityType activityType) {
                if (activityType != null) {
                    view.show(activityType);
                } else {
                    view.warnTracking();
                }
            }
        });
    }

    private void startTrackingService() {
        view.warnTracking();
        tracker.startTrackingService();
    }

    private void stopTrackingService() {
        tracker.stopTrackingService();
        view.warnTrackingHasBeenStopped();
    }

    public void callTrackingService() {
        if (tracker.isTracking()) {
            stopTrackingService();
        } else {
            startTrackingService();
        }
    }
}
