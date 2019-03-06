package com.example.nicholasanton.myapplication;

public class HomePresenter {
    private HomeView view;
    private Tracker tracker;

    public HomePresenter(HomeView view, Tracker tracker) {
        this.view = view;
        this.tracker = tracker;
    }

    void init() {
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

    void callTrackingService() {
        if (tracker.isTracking()) {
            stopTrackingService();
        } else {
            startTrackingService();
        }
    }
}
