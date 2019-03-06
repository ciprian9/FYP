package com.example.nicholasanton.myapplication;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = {AppModule.class, ApiModule.class}, injects = ActivitesListeners.class)
class HomeViewModule {

    private HomeView view;

    HomeViewModule(HomeView view) {
        this.view = view;
    }

    @Provides
    HomePresenter provideHomePresenter(Tracker tracker) {
        return new HomePresenter(view, tracker);
    }
}
