package com.example.nicholasanton.myapplication.Applications;

import android.app.Application;

import com.example.nicholasanton.myapplication.Classes.ApiModule;
import com.example.nicholasanton.myapplication.Classes.AppModule;

import dagger.ObjectGraph;

public class ActivityTrackerApplication extends Application {
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(
                new AppModule(ActivityTrackerApplication.this),
                new ApiModule()
        );
    }

    public ObjectGraph getObjectGraph(){
        return objectGraph;
    }
}
