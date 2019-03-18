package com.example.nicholasanton.myapplication.Classes;

import com.example.nicholasanton.myapplication.Interfaces.HomeView;
import com.example.nicholasanton.myapplication.Interfaces.Tracker;
import com.example.nicholasanton.myapplication.Views.ActivitesListeners;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = {AppModule.class, ApiModule.class}, injects = ActivitesListeners.class)
public class HomeViewModule {

    private final HomeView view;

    public HomeViewModule(HomeView view) {
        this.view = view;
    }

    @Provides
    HomePresenter provideHomePresenter(Tracker tracker) {
        return new HomePresenter(view, tracker);
    }
}
