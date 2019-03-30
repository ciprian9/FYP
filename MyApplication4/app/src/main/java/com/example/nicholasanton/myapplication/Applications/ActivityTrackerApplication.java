package com.example.nicholasanton.myapplication.Applications;

/**
 * Class that creates an objectGraph object and returns it to where its needed
 * */

import android.app.Application;

import com.example.nicholasanton.myapplication.Classes.ApiModule;
import com.example.nicholasanton.myapplication.Classes.AppModule;

import dagger.ObjectGraph;

public class ActivityTrackerApplication extends Application {
    private ObjectGraph objectGraph;

    //Creates the object with appropriate parameters
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
