package com.example.ciprian.myapplication.Classes;

/**
 * Used in conjunction with activity recognition that will return view
 * Source : https://github.com/tassioauad/CoachTracker
 * */

import com.example.ciprian.myapplication.Interfaces.HomeView;
import com.example.ciprian.myapplication.Interfaces.Tracker;
import com.example.ciprian.myapplication.Views.ActivitesListeners;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = {AppModule.class, ApiModule.class}, injects = ActivitesListeners.class)
public class HomeViewModule {

    private final HomeView view;

    public HomeViewModule(HomeView view) {
        this.view = view;
    }

    //Returns a home presenter object with the tracker inside of it to be used
    @Provides
    HomePresenter provideHomePresenter(Tracker tracker) {
        return new HomePresenter(view, tracker);
    }
}