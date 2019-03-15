package com.example.nicholasanton.myapplication.Classes;

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

    AppModule() {
    }

    @Provides
    Application provideApplication() {
        return app;
    }

    @Provides
    Context provideContext() {
        return app;
    }

}
