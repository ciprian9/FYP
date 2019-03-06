package com.example.nicholasanton.myapplication;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = AppModule.class)
class ApiModule {

    @Provides
    ActivityRecognizer provideActivityRecognizer(Context context) {
        return new ActivityRecognizerImpl(context);
    }

    @Provides
    Tracker provideTracker(Context context) {
        return new TrackerImpl(context);
    }

}
