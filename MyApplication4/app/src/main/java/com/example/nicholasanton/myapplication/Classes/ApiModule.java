package com.example.nicholasanton.myapplication.Classes;

import android.content.Context;

import com.example.nicholasanton.myapplication.ActivityRecognizerImpl;
import com.example.nicholasanton.myapplication.Interfaces.ActivityRecognizer;
import com.example.nicholasanton.myapplication.Interfaces.Tracker;
import com.example.nicholasanton.myapplication.TrackerImpl;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = AppModule.class)
public class ApiModule {

    @Provides
    ActivityRecognizer provideActivityRecognizer(Context context) {
        return new ActivityRecognizerImpl(context);
    }

    @Provides
    Tracker provideTracker(Context context) {
        return new TrackerImpl(context);
    }

}
