package com.example.nicholasanton.myapplication.Services;

//https://stackoverflow.com/questions/15570542/determining-the-speed-of-a-vehicle-using-gps-in-android

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
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);
        prevLocation = new Location("prevLocation");
        updateSpeed(null);

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;
        db.insertLog("Getting Speed");

        if(location != null) {
            location.setUseMetricunits(true);
            nCurrentSpeed = location.getSpeed();

        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "meters/second";

        String actualSpeed = (strCurrentSpeed + " " + strUnits);

        if(prevLocation != null && prevLocation.getLongitude() != 0 && prevLocation.getLatitude() != 0){
            distance =  distance + prevLocation.distanceTo(location);
        }

        prevLocation = location;

        Intent tempIntent = new Intent("GET_SPEED_DATA");
        tempIntent.putExtra("Speed", actualSpeed);
        tempIntent.putExtra(Constants.DISTANCE_INTENT, distance);
        sendBroadcast(tempIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null)
        {
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