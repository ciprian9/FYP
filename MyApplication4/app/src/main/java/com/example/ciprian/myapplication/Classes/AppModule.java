package com.example.ciprian.myapplication.Classes;

/**
 * Used in conjunction with activity recognition classes that will return the appropriate objects
 * */

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class AppModule {

    private static Application app;

    public AppModule(Application app) {
        AppModule.app = app;
    }

    AppModule() {}

    //Returns the application
    @Provides
    Application provideApplication() {
        return app;
    }

    //Returns the context of the application
    @Provides
    Context provideContext() {
        return app;
    }

}
