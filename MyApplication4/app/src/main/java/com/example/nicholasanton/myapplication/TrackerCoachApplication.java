package com.example.nicholasanton.myapplication;

import android.app.Application;

import dagger.ObjectGraph;

public class TrackerCoachApplication extends Application {
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(
                new AppModule(TrackerCoachApplication.this),
                new ApiModule()
        );
    }

    public ObjectGraph getObjectGraph(){
        return objectGraph;
    }
}
