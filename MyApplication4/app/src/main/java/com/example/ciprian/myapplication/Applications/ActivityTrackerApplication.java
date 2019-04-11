package com.example.ciprian.myapplication.Applications;

/**
 * Class that creates an ObjectGraph because it is essential to obtain an instance of a class with
 * injected attributes and then returns it to activities listeners to inject data into the variable
 * */

import android.app.Application;

import com.example.ciprian.myapplication.Classes.ApiModule;
import com.example.ciprian.myapplication.Classes.AppModule;

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