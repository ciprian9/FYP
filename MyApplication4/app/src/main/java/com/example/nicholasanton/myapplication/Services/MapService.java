package com.example.nicholasanton.myapplication.Services;

//https://gist.github.com/enginebai/adcae1f17d3b2114590c

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;


public class MapService extends Service{
    private LocationListener listener;
    private LocationManager locationManager;
    private final String walkingFileName = "latandlongsWalk.txt";
    private final String runningFileName = "latandlongsRun.txt";
    private final String cyclingFileName = "latandlongsCycle.txt";
    private final String drivingFileName = "latandlongsDrive.txt";
    private double temp1 = 0;
    private double temp2 = 0;
    private double prevLat, prevLong;
    private boolean temp;
    private int policyID;
    private DataHandler db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return file != null && file.exists();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        db = new DataHandler(this);
        super.onCreate();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        if( extras != null ) {
            temp = extras.getBoolean("temp");
            policyID = extras.getInt(Constants.POLICY_ID);
        }

        File dir = getFilesDir();
        File file = null;
        if (policyID == 1 && !temp) {
            if (fileExists(this, walkingFileName)){
                db.insertLog("New Walking File Created");
                file = new File(dir, walkingFileName);
            }
        } else if (policyID == 2 && !temp) {
            if (fileExists(this, runningFileName)) {
                db.insertLog("New Running File Created");
                file = new File(dir, runningFileName);
            }
        } else if (policyID == 3 && !temp){
            if (fileExists(this, cyclingFileName)) {
                db.insertLog("New Cycling File Created");
                file = new File(dir, cyclingFileName);
            }
        } else if (policyID == 4 && !temp){
            if (fileExists(this, drivingFileName)) {
                db.insertLog("New Driving File Created");
                file = new File(dir, drivingFileName);
            }
        }
        if (file != null) {
            boolean deleted = file.delete();
        }

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                temp1 = location.getLatitude();
                temp2 = location.getLongitude();
                LatLng latLng = new LatLng(temp1, temp2);
                if ((temp1 >= (prevLat + 0.000005) || temp1 <= (prevLat - 0.000005)) ||
                        (temp2 >= (prevLong + 0.000005) || temp2 <= (prevLong - 0.000005))){
                    if (policyID == 1) {
                        db.insertLog("Saving Walking Locations");
                        saveFile(walkingFileName, (latLng.toString() + "\n"));
                    } else if (policyID == 2){
                        db.insertLog("Saving Running Locations");
                        saveFile(runningFileName, (latLng.toString() + "\n"));
                    } else if (policyID == 3){
                        db.insertLog("Saving Cycling Locations");
                        saveFile(cyclingFileName, (latLng.toString() + "\n"));
                    } else if (policyID == 4){
                        db.insertLog("Saving Driving Locations");
                        saveFile(drivingFileName, (latLng.toString() + "\n"));
                    }
                    if (temp) {
                        Intent i = new Intent("location_update");
                        sendBroadcast(i);
                    }
                }
                prevLat = temp1;
                prevLong = temp2;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);

        return super.onStartCommand(intent, flags, startId);
    }

    private void saveFile(String file, String text){
        try{
            FileOutputStream fos = openFileOutput(file, Context.MODE_APPEND);
            fos.write(text.getBytes());
            fos.close();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error Saving File", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }
}
