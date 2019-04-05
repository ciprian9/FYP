package com.example.nicholasanton.myapplication.Classes;

/**
 * In order to generate an instance of the class ObjectGraph a module is required
 * */

import android.content.Context;

import com.example.nicholasanton.myapplication.ActivityRecognizerImpl;
import com.example.nicholasanton.myapplication.Interfaces.ActivityRecognizer;
import com.example.nicholasanton.myapplication.Interfaces.Tracker;
import com.example.nicholasanton.myapplication.TrackerImpl;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = AppModule.class)
public class ApiModule {

    //Returns an activity tracker to be used
    @Provides
    ActivityRecognizer provideActivityRecognizer(Context context) {
        return new ActivityRecognizerImpl(context);
    }

    @Provides
    Tracker provideTracker(Context context) {
        return new TrackerImpl(context);
    }

}
