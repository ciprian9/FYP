package com.example.nicholasanton.myapplication;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
class AppModule {

    private static Application app;

    AppModule(Application app) {
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
