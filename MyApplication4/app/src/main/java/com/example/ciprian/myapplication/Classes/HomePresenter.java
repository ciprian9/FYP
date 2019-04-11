package com.example.ciprian.myapplication.Classes;

/**
 * File to start Activity recognition
 * */

import com.example.ciprian.myapplication.Enums.ActivityType;
import com.example.ciprian.myapplication.Interfaces.HomeView;
import com.example.ciprian.myapplication.Interfaces.Tracker;
import com.example.ciprian.myapplication.Interfaces.TrackerListener;

public class HomePresenter {
    private final HomeView view;
    private final Tracker tracker;

    public HomePresenter(HomeView view, Tracker tracker) {
        this.view = view;
        this.tracker = tracker;
    }

    //Starts activity recognition
    public void init() {
        tracker.setTrackerListener(new TrackerListener() {
            //Will only run if tracking has been recognized
            @Override
            public void onTracked(ActivityType activityType) {
                //Checks if the activity type is valid and if it is then use it
                if (activityType != null) {
                    view.show(activityType);
                } else {
                    view.warnTracking();
                }
            }
        });
    }

    //Start the tracking service
    private void startTrackingService() {
        view.warnTracking();
        tracker.startTrackingService();
    }

    //Stop the tracking service
    private void stopTrackingService() {
        tracker.stopTrackingService();
        view.warnTrackingHasBeenStopped();
    }

    //Procedure to check if the tracker is currently tracking then stop it and it not then start it
    public void callTrackingService() {
        if (tracker.isTracking()) {
            stopTrackingService();
        } else {
            startTrackingService();
        }
    }
}
