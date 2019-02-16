package com.example.nicholasanton.myapplication;

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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import permissions.dispatcher.RuntimePermissions;
import permissions.dispatcher.NeedsPermission;


@RuntimePermissions
public class MapActivity extends AppCompatActivity {

    //Pedometer processing
    private boolean pedometer, time, dist;
    private TextView TvSteps, TvDistance, TvTimer, TvSpeed;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private static final String TEXT_MET_STEPS = "Number of Meters: ";
    private static final String TEXT_STO_STEPS = "Stopwatch: ";
    private static final String TEXT_SPE_STEPS = "Speed: ";


    //Map processing
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    private String walkingFileName = "latandlongsWalk.txt";
    private String runningFileName = "latandlongsRun.txt";
    private ArrayList<LatLng> points; //added
    Polyline line; //added
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 1000;  /* 1 sec */
    private long FASTEST_INTERVAL = 500; /* 0.5 secs */
    private NewLocationReciever receiver;
    private DataReceiver dataReceiver;
    private int policyID;

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
        points = new ArrayList<LatLng>();

        receiver = new NewLocationReciever();
        registerReceiver(receiver, new IntentFilter("location_update"));

        if (isMyServiceRunning(MapService.class)){
            Intent mapServiceIntent = new Intent(this, MapService.class);
            mapServiceIntent.putExtra("temp", true);
            startService(mapServiceIntent);
        }

        //*********************************************************************************PEDOMETER**************************************************************

        dataReceiver = new DataReceiver();
        registerReceiver(dataReceiver, new IntentFilter("GET_PEDOMETER_DATA"));

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                pedometer = extras.getBoolean(Constants.PEDOMETER_INTENT);
                time = extras.getBoolean(Constants.TIME_INTENT);
                dist = extras.getBoolean(Constants.DISTANCE_INTENT);
                policyID = extras.getInt(Constants.POLICY_ID);
            }
        }

        TvSteps =  findViewById(R.id.tv_steps);
        TvDistance =  findViewById(R.id.tvDistance);
        TvTimer =  findViewById(R.id.tvTimer);
        TvSpeed =  findViewById(R.id.tvSpeed);


        //***************************************************************************MAP*************************************************************
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }


        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void drawTheLines() {
        String textFromFile = "";
        if (policyID == 1) {
            textFromFile = readFile(walkingFileName);
        } else if (policyID == 2){
            textFromFile = readFile(runningFileName);
        }
        String[] textFile = textFromFile.split("\n");
        for (int i = 0; i < textFile.length; i++) {
            points.add(removeText(textFile[i]));
        }
        redrawLine();
    }

    public LatLng removeText(String text) {
        text = text.substring(10);
        text = text.substring(0, text.length() - 1);
        String[] split = text.split(",");
        String Lattitude = split[0];
        String Longitude = split[1];
        double lat = Double.parseDouble(Lattitude);
        double lng = Double.parseDouble(Longitude);
        final LatLng latLng = new LatLng(lat, lng);
        return latLng;
    }

    public String readFile(String file) {
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


    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
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
                    drawTheLines();
                }
            } else if (policyID == 2){
                if (fileExists(this, runningFileName)) {
                    drawTheLines();
                }
            }
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        map.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        map.setMyLocationEnabled(true);
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * Called when the Activity is no longer visible.
     */
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
            if (isMyServiceRunning(MapService.class)){
                Intent mapServiceIntent = new Intent(this, MapService.class);
                mapServiceIntent.putExtra("temp", false);
                startService(mapServiceIntent);
            }
        } catch (IllegalArgumentException e) {
            Log.i("Receiver", "Reciver is already unregistered");
            receiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status

        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).bearing(mCurrentLocation.getBearing()).tilt(70).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate);
        } else {

        }
        MapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    @SuppressLint("RestrictedApi")
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
    }

    private void redrawLine(){
        map.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(points);
        line = map.addPolyline(options); //add Polyline
        points.clear();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends android.support.v4.app.DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    class NewLocationReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("location_update")) {
                drawTheLines();
            }
        }
    }

    class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("GET_PEDOMETER_DATA")) {
                float speed = intent.getFloatExtra("speed", 0);
                float distance = intent.getFloatExtra("dist", 0);
                int min = intent.getIntExtra("min", 0);
                int sec = intent.getIntExtra("sec", 0);
                int hour = intent.getIntExtra("hour", 0);
                int steps = intent.getIntExtra("steps", 0);
                if(time) {
                    TvTimer.setText(TEXT_STO_STEPS + String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec));
                }
                if(dist) {
                    TvDistance.setText(TEXT_MET_STEPS + String.format("%.02f", distance));
                    TvSpeed.setText(TEXT_SPE_STEPS + String.format("%.02f", speed));
                }
                if(pedometer) {
                    TvSteps.setText(TEXT_NUM_STEPS + steps);
                }

            }
        }
    }
}
