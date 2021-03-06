package com.example.ciprian.myapplication.Services;

/**
 * This class is used to get the current location of the user by using the GPS
 * */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import com.example.ciprian.myapplication.DataHandler;

public class LocationTrack extends Service implements LocationListener {
    private final Context mContext;
    private boolean canGetLocation = false;
    private Location loc;
    private double latitude;
    private double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    private DataHandler db;

    public LocationTrack(Context mContext) {
        this.mContext = mContext;
        db = new DataHandler(mContext);
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        db.insertLog("Getting Location");
        try {
            LocationManager locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // gets the GPS status
            boolean checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // gets the network provider status
            boolean checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                db.insertLog("No service provider available");
                Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;

                // if the GPS is Enabled get the lat & lng using the GPS Services
                if (checkGPS) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    loc = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc != null) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                        db.insertLog("Got location");
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get longitude from the location object loc
    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    //get latitude from the location object loc
    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    //Create a datahandler object so we can insert lines into the console
    @Override
    public void onCreate() {
        db = new DataHandler(this);
        super.onCreate();
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    //Creates an alert dialog that will allow the user to enable gps if wanted
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        //Listens if the user clicks yes and if so brings the settings for location on the screen
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        //Listens if the user clicks no and if so closes the dialog
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    //AUTO-GENERATED
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
