package com.example.nicholasanton.myapplication.Interfaces;

/**
 * Used code from : https://stackoverflow.com/questions/15570542/determining-the-speed-of-a-vehicle-using-gps-in-android
 * Used to initialize procedures for the determining the speed of vehicles
 * */

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public interface IBaseGpsListener extends LocationListener, GpsStatus.Listener {

    void onLocationChanged(Location location);

    void onProviderDisabled(String provider);

    void onProviderEnabled(String provider);

    void onStatusChanged(String provider, int status, Bundle extras);

    void onGpsStatusChanged(int event);

}