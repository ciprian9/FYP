package com.example.nicholasanton.myapplication.Services;

/**
 *  Used Code from : https://stackoverflow.com/questions/15570542/determining-the-speed-of-a-vehicle-using-gps-in-android
 *  Will get the users speed and distance
 *  */

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.example.nicholasanton.myapplication.CLocation;
import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.Interfaces.IBaseGpsListener;

import java.util.Formatter;
import java.util.Locale;

public class SpeedAndDistance extends Service implements IBaseGpsListener {

    private Location prevLocation;
    private float distance;
    private DataHandler db;

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new DataHandler(this);
        //Get an instance of the Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //request the current location from the GPS proviced
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);
        //Create a previous location object to store the previous location
        prevLocation = new Location("prevLocation");
        updateSpeed(null);

        return super.onStartCommand(intent, flags, startId);
    }

    //Gets the speed
    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;
        db.insertLog("Getting Speed");

        if(location != null) {
            //use the location object to retrieve the appoximate speed in metric units
            location.setUseMetricunits(true);
            nCurrentSpeed = location.getSpeed();

        }

        //format the result using a StringBuilder
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "meters/second";
        //concatinate strings to provide the result string
        String actualSpeed = (strCurrentSpeed + " " + strUnits);

        //add the distnace from current location to the previous location to the distance variable
        if(prevLocation != null && prevLocation.getLongitude() != 0 && prevLocation.getLatitude() != 0){
            distance =  distance + prevLocation.distanceTo(location);
        }

        prevLocation = location;

        //set up a broadcast to let the map activity know the data has changed
        Intent tempIntent = new Intent("GET_SPEED_DATA");
        tempIntent.putExtra("Speed", actualSpeed);
        tempIntent.putExtra(Constants.DISTANCE_INTENT, distance);
        sendBroadcast(tempIntent);
    }

    //Calls function to get the speed
    @Override
    public void onLocationChanged(Location location) {
        if(location != null)
        {
            //if location has changed update the speed
            db.insertLog("Updating Last Known Speed");
            CLocation myLocation = new CLocation(location, true);
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onGpsStatusChanged(int event) {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}