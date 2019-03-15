package com.example.nicholasanton.myapplication.Classes;

import com.example.nicholasanton.myapplication.Enums.ActivityType;
import com.example.nicholasanton.myapplication.Interfaces.HomeView;
import com.example.nicholasanton.myapplication.Interfaces.Tracker;
import com.example.nicholasanton.myapplication.Interfaces.TrackerListener;

public class HomePresenter {
    private HomeView view;
    private Tracker tracker;

    public HomePresenter(HomeView view, Tracker tracker) {
        this.view = view;
        this.tracker = tracker;
    }

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
