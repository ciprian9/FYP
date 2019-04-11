package com.example.ciprian.myapplication.Views;

/**
 * Class that will appear when the user is driving and will only allow the user to exit when
 * he is going under 20 km/hr.
 * */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.ciprian.myapplication.R;

public class LockedScreen extends AppCompatActivity {

    private double speedTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked_screen);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            //Simple function that will get the speed of the user
            public void onLocationChanged(Location location) {
                if (location == null) {
                    speedTotal = 0.0;
                } else {
                    speedTotal = (int) ((location.getSpeed() * 3600) / 1000);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    //Listener for the back button that will check if the user is going under 20km/hr it will let the user exit but otherwise it wont
    @Override
    public void onBackPressed() {
        if (speedTotal < 20.0) {
            super.onBackPressed();
        }
    }
}