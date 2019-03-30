package com.example.nicholasanton.myapplication.Views;

/**
 * Used code from : https://gist.github.com/enginebai/adcae1f17d3b2114590c
 * Activity that shows everything that has been happening while the app has been running
 * */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.R;
import com.example.nicholasanton.myapplication.Services.MapService;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Objects;

import permissions.dispatcher.RuntimePermissions;
import permissions.dispatcher.NeedsPermission;

@RuntimePermissions
public class MapActivity extends AppCompatActivity {
    private boolean pedometer, time, dist;
    private TextView TvSteps, TvDistance, TvTimer, TvSpeed;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private static final String TEXT_MET_STEPS = "Number of Meters: ";
    private static final String TEXT_STO_STEPS = "Stopwatch: ";
    private static final String TEXT_SPE_STEPS = "Speed: ";

    private GoogleMap map;
    private final String walkingFileName = "latandlongsWalk.txt";
    private final String runningFileName = "latandlongsRun.txt";
    private final String cyclingFileName = "latandlongsCycle.txt";
    private final String drivingFileName = "latandlongsDrive.txt";
    private ArrayList<LatLng> points;
    private Location mCurrentLocation;
    private NewLocationReciever receiver;
    private DataReceiver dataReceiver;
    private SpeedReciever speedReciever;
    private int policyID;
    private DataHandler db;

    private final static String KEY_LOCATION = "location";

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        db = new DataHandler(this);

        db.insertLog("Enter Map Activity\n");

        points = new ArrayList<>();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                pedometer = extras.getBoolean(Constants.PEDOMETER_INTENT);
                time = extras.getBoolean(Constants.TIME_INTENT);
                dist = extras.getBoolean(Constants.DISTANCE_INTENT);
                policyID = extras.getInt(Constants.POLICY_ID);
            }
        }

        receiver = new NewLocationReciever();
        registerReceiver(receiver, new IntentFilter("location_update"));


        if (isMyServiceRunning()) {
            Intent mapServiceIntent = new Intent(this, MapService.class);
            mapServiceIntent.putExtra("temp", true);
            mapServiceIntent.putExtra(Constants.POLICY_ID, policyID);
            startService(mapServiceIntent);
        }

        //*********************************************************************************PEDOMETER**************************************************************

        dataReceiver = new DataReceiver();
        registerReceiver(dataReceiver, new IntentFilter("GET_PEDOMETER_DATA"));

        speedReciever = new SpeedReciever();
        registerReceiver(speedReciever, new IntentFilter("GET_SPEED_DATA"));


        TvSteps = findViewById(R.id.tv_steps);
        TvDistance = findViewById(R.id.tvDistance);
        TvTimer = findViewById(R.id.tvTimer);
        TvSpeed = findViewById(R.id.tvSpeed);


        //***************************************************************************MAP*************************************************************
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MapService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Draw lines on the map according to what the file has
    private void drawTheLines() {
        String textFromFile = "";
        if (policyID == 1) {
            textFromFile = readFile(walkingFileName);
        } else if (policyID == 2) {
            textFromFile = readFile(runningFileName);
        } else if (policyID == 3) {
            textFromFile = readFile(cyclingFileName);
        } else if (policyID == 4) {
            textFromFile = readFile(drivingFileName);
        }
        String[] textFile = textFromFile.split("\n");
        for (String aTextFile : textFile) {
            points.add(removeText(aTextFile));
        }
        redrawLine();
    }

    //removes text and extracts the lat and lng
    private LatLng removeText(String text) {
        text = text.substring(10);
        text = text.substring(0, text.length() - 1);
        String[] split = text.split(",");
        String Lattitude = split[0];
        String Longitude = split[1];
        double lat = Double.parseDouble(Lattitude);
        double lng = Double.parseDouble(Longitude);
        return new LatLng(lat, lng);
    }

    //read file containing the users movements
    private String readFile(String file) {
        String text = "";
        try {
            FileInputStream fis = openFileInput(file);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            text = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error Reading File!", Toast.LENGTH_SHORT).show();
        }

        return text;
    }

    //Loading the map with users current location and draws the lines
    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                11);
                    }
                }

                return;
            }
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).bearing(location.getBearing()).tilt(0).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);
            }
            MapActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            MapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
            if (policyID == 1) {
                if (fileExists(this, walkingFileName)) {
                    db.insertLog("Failed Drawing Lines for Walking");
                    drawTheLines();
                }
            } else if (policyID == 2){
                if (fileExists(this, runningFileName)) {
                    db.insertLog("Failed Drawing Lines for Running");
                    drawTheLines();
                }
            } else if (policyID == 3){
                if (fileExists(this, cyclingFileName)) {
                    db.insertLog("Failed Drawing Lines for Cycling");
                    drawTheLines();
                }
            } else if (policyID == 4){
                if (fileExists(this, drivingFileName)) {
                    db.insertLog("Failed Drawing Lines for Driving");
                    drawTheLines();
                }
            }
        } else {
            db.insertLog("Map was null");
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    //checks if txt files exist
    private boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return file != null && file.exists();
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //Gets users location
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        map.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onBackPressed() {
        db.insertLog("Exit Map Activity\n");
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        map.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
            if (dataReceiver != null){
                unregisterReceiver(dataReceiver);
            }
            if (speedReciever != null){
                unregisterReceiver(speedReciever);
            }
            if (isMyServiceRunning()){
                Intent mapServiceIntent = new Intent(this, MapService.class);
                mapServiceIntent.putExtra("temp", false);
                startService(mapServiceIntent);
            }
        } catch (IllegalArgumentException e) {
            db.insertLog("Reciever is already unregistered");
            Log.i("Receiver", "Reciver is already unregistered");
            receiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).bearing(mCurrentLocation.getBearing()).tilt(70).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate);
        }
        MapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    //Listens for the users updates
    @SuppressLint("RestrictedApi")
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        /* 1 sec */
        long UPDATE_INTERVAL = 1000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 0.5 secs */
        long FASTEST_INTERVAL = 500;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
    }

    //adds points to the line
    private void redrawLine(){
        map.clear();
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(points);
        Polyline line = map.addPolyline(options);
        points.clear();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    //listens for location updates
    class NewLocationReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals("location_update")) {
                drawTheLines();
            }
        }
    }

    //will get the speed, distance and steps of user
    class SpeedReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals("GET_SPEED_DATA")){
                String speed = intent.getStringExtra("Speed");
                float distance = intent.getFloatExtra(Constants.DISTANCE_INTENT, 0);
                String result = TEXT_SPE_STEPS + speed;
                TvSpeed.setText(result);
                TvSteps.setText("");
                result = TEXT_MET_STEPS + String.format("%.02f", distance);
                TvDistance.setText(result);
                TvTimer.setText("");

            }
        }
    }


    //will set the steps, distance, speed and time
    class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals("GET_PEDOMETER_DATA")) {
                float speed = intent.getFloatExtra("speed", 0);
                float distance = intent.getFloatExtra("dist", 0);
                int min = intent.getIntExtra("min", 0);
                int sec = intent.getIntExtra("sec", 0);
                int hour = intent.getIntExtra("hour", 0);
                int steps = intent.getIntExtra("steps", 0);
                String result;
                if(time) {
                    result = TEXT_STO_STEPS + String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
                    TvTimer.setText(result);
                }
                if(dist) {
                    result = TEXT_MET_STEPS + String.format("%.02f", distance);
                    TvDistance.setText(result);
                    result = TEXT_SPE_STEPS + String.format("%.02f", speed);
                    TvSpeed.setText(result);
                }
                if(pedometer) {
                    result = TEXT_NUM_STEPS + steps;
                    TvSteps.setText(result);
                }

            }
        }
    }
}
