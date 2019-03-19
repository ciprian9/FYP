package com.example.nicholasanton.myapplication.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Enums.ActivityType;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.Classes.HomePresenter;
import com.example.nicholasanton.myapplication.Interfaces.HomeView;
import com.example.nicholasanton.myapplication.Classes.HomeViewModule;
import com.example.nicholasanton.myapplication.R;
import com.example.nicholasanton.myapplication.Classes.RequestHandler;
import com.example.nicholasanton.myapplication.Services.Cycling_Policy_Service;
import com.example.nicholasanton.myapplication.Services.Driving_Policy_Service;
import com.example.nicholasanton.myapplication.Services.LocationTrack;
import com.example.nicholasanton.myapplication.Services.MapService;
import com.example.nicholasanton.myapplication.Services.MyJobService;
import com.example.nicholasanton.myapplication.Services.Running_Policy_Service;
import com.example.nicholasanton.myapplication.Services.SpeedAndDistance;
import com.example.nicholasanton.myapplication.Services.Timer_Service;
import com.example.nicholasanton.myapplication.Services.Walking_Policy_Service;
import com.example.nicholasanton.myapplication.Services.bedtimeRoutineService;
import com.example.nicholasanton.myapplication.Services.getTheWeather;
import com.example.nicholasanton.myapplication.Services.googleCalendarService;
import com.example.nicholasanton.myapplication.Services.pedometerService;
import com.example.nicholasanton.myapplication.Classes.SpotifyPlayer;
import com.example.nicholasanton.myapplication.Applications.ActivityTrackerApplication;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.android.gms.maps.model.LatLng;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import javax.inject.Inject;

import butterknife.ButterKnife;

import static com.example.nicholasanton.myapplication.Interfaces.Constants.defaultMorning;
import static com.example.nicholasanton.myapplication.Interfaces.Constants.defaultNight;

public class ActivitesListeners extends AppCompatActivity implements HomeView {
    private int accountid;
    private String username;
    private String gmail;
    private List<String> calendarList;
    private boolean musicPlayer = false, pedometer = false, timeRecord = false,
            dist_speed = false, recordRoute = false;
    public static boolean notificationTTS;
    public static boolean autoReply;
    public static boolean callReply;
    public static boolean runningService = false;
    public static boolean cyclingService = false;
    public static boolean drivingService = false;
    public static boolean inMeeting = false;
    private String previousActivity = "UNKNOWN";
    private String work;
    private String home;
    private Location workLL;
    private Location homeLL;
    private Location loc1;
    private EventReciever reciever;
    private FirebaseJobDispatcher mDispatcher;
    private static final String CLIENT_ID = "9059d78622a94813a3b8920877c0198a";
    private static final String REDIRECT_URI = "http://example.com/callback";
    private static final int REQUEST_CODE = 1337;
    private SpotifyAppRemote mSpotifyAppRemote;
    private String mAccessToken;
    private BroadcastReceiver updateUIReciver;
    private String summary;
    private int hour, mins, secs, ehour, emins, esecs;
    private SpotifyPlayer spotifyPlayer;
    private DataHandler db;

    @Inject
    HomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        db = new DataHandler(this);

        requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();
        turnOffDoNotDisturb();


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                accountid = extras.getInt(Constants.ACCOUNTID_INTENT);
                username = extras.getString(Constants.USERNAME_INTENT);
                gmail = extras.getString(Constants.GMAIL_INTENT);
            }
        }
        if(gmail != null) {
            if (gmail.equalsIgnoreCase("NULL")) {
                GmailDialog();
            }
        }else{
            db.insertLog("Gmail is NULL");
            Log.d("TEST : ", "GMAIL IS NULL");
        }

        if (username.equals("tester1234")){
            workLL = new Location("");
            workLL.setLatitude(53.374698300000006);
            workLL.setLongitude(-6.3938991);
        }else{
            loc1 = new Location("School/Work");
        }

        final LocationListener mLocationListener = new LocationListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onLocationChanged(final Location location) {
                Log.d("TEST : ", "Latitude HOME: "+String.format("%.3f",loc1.getLatitude()));
                db.insertLog("Checking location");

                if (workLL != null) {
                    if (location.distanceTo(workLL) < 40){
                        Log.d("TEST : ", "Arrived to work");
                        db.insertLog("Work found request do not disturb");
                        requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();
                    }
                }else{
                    if(location.distanceTo(loc1) < 40){
                        Log.d("TEST : ", "Arrived to Work");
                        db.insertLog("Work found request do not disturb");
                        requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();
                    }
                }

                if (homeLL != null) {
                    if (location.distanceTo(homeLL) < 40){
                        Log.d("TEST : ", "Arrived Home");
                        db.insertLog("Arrived Home\n Turning ON WIFI");
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if (!wifiManager.isWifiEnabled()) {
                            wifiManager.setWifiEnabled(true);
                        }
                    }
                }
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
        };

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                10, mLocationListener);

        calendarList = new ArrayList<>();


        if (!username.equals("tester1234")) {
            ReadLocation();
        }

        if (accountid == -1) {
            db.insertLog("Set Up Testing Enviroment");

            Button btnTestDrive = findViewById(R.id.btnTestDriving);
            btnTestDrive.setVisibility(View.VISIBLE);
            btnTestDrive.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    getWalkingSettings(4);
                    db.insertLog("Test Driving Started\n");
                }
            });

            Button btnTestBike = findViewById(R.id.btnTestCycling);
            btnTestBike.setVisibility(View.VISIBLE);
            btnTestBike.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    getWalkingSettings(3);
                    db.insertLog("Test Cycling Started\n");
                }
            });

            Button btnTestWalk = findViewById(R.id.btnTestWalking);
            btnTestWalk.setVisibility(View.VISIBLE);
            btnTestWalk.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    getWalkingSettings(1);
                    db.insertLog("Test Walking Started\n");
                }
            });

            Button btnTestRun = findViewById(R.id.btnTestRunning);
            btnTestRun.setVisibility(View.VISIBLE);
            btnTestRun.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    getWalkingSettings(2);
                    db.insertLog("Test Running Started\n");
                }
            });

            Button btnStop = findViewById(R.id.btnStop);
            btnStop.setVisibility(View.VISIBLE);
            btnStop.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    StopWalkingPolicy();
                    StopDrivingPolicy();
                    StopCyclingPolicy();
                    StopRunningPolicy();
                    spotifyPlayer.pause();
                    db.insertLog("ALL Policies Stopped\n");
                }
            });
        }

        if (username.equals("tester1234")) {
            musicPlayer = true;
            pedometer = true;
            timeRecord = true;
            dist_speed = true;
            recordRoute = true;
            notificationTTS = true;
            autoReply = true;
            callReply = true;
        }

        if (accountid != -1) {
            ButterKnife.bind(this);
            ((ActivityTrackerApplication) getApplication()).getObjectGraph().plus(new HomeViewModule(this)).inject(this);
            presenter.init();
            presenter.callTrackingService();
        }

        reciever = new EventReciever();
        registerReceiver(reciever, new IntentFilter("GET_EVENTS"));
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Start();

        setMorningNightRoutine(defaultMorning, defaultNight);

        final Button walkingOptions = findViewById(R.id.walkingOptions);
        walkingOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartWalkingOptions();
                db.insertLog("Starting Walking Options\n");
            }
        });

        final Button runningOptions = findViewById(R.id.runningOptions);
        runningOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartRunningOptions();
                db.insertLog("Starting Running Options\n");
            }
        });

        final Button cyclingOptions = findViewById(R.id.cyclingOptions);
        cyclingOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartCyclingOptions();
                db.insertLog("Starting Cycling Options\n");
            }
        });

        final Button drivingOptions = findViewById(R.id.drivingOptions);
        drivingOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartDrivingOptions();
                db.insertLog("Starting Driving Options\n");
            }
        });

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("TEST : ", "Connected!");
                        db.insertLog("Spotify Connected\n");

                        AuthenticationRequest.Builder builder =
                                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

                        builder.setScopes(new String[]{"streaming"});
                        AuthenticationRequest request = builder.build();

                        AuthenticationClient.openLoginActivity(ActivitesListeners.this, REQUEST_CODE, request);

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("TEST : ", throwable.getMessage(), throwable);
                        db.insertLog("SPOTIFY ERROR CONNECTING");
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void setMorningNightRoutine(String morningTime, String nightTime){
        db.insertLog("Start Day/Night routine");
        String[] strings = morningTime.split(":");
        String morningHour = strings[0];
        String morningMin = strings[1];

        strings = nightTime.split(":");
        String nightHour = strings[0];
        String nightMin = strings[1];

        AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), getTheWeather.class);
        PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(morningHour));
        calendar.set(Calendar.MINUTE, Integer.valueOf(morningMin));

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
//-------------------------------------------------------------------------------------------------------------------------------------------------------
        AlarmManager alarmMgr2 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(getApplicationContext(), bedtimeRoutineService.class);
        PendingIntent alarmIntent2 = PendingIntent.getService(getApplicationContext(), 0, intent2, 0);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());
        calendar2.set(Calendar.HOUR_OF_DAY, Integer.valueOf(nightHour));
        calendar2.set(Calendar.MINUTE, Integer.valueOf(nightMin));

        alarmMgr2.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent2);

        db.insertLog("End day / night routine");
    }

    private void openTheMap(){
        db.insertLog("Map/Pedometer Activity Starting\n");
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        i.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        i.putExtra(Constants.TIME_INTENT, timeRecord);
        i.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        i.putExtra(Constants.POLICY_ID, 1);
        startActivity(i);
    }

    private LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
            Log.d("LATLONG123", p1.latitude + ", " + p1.longitude);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    mAccessToken = response.getAccessToken();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
    @SuppressWarnings("deprecation")
    private void connected(String ActivityTypeString) {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (spotifyPlayer == null){
            db.insertLog("Starting Spotify Playlist\n");
            spotifyPlayer = new SpotifyPlayer(mSpotifyAppRemote, mAccessToken, ActivityTypeString);
            spotifyPlayer.init();
        } else if((!(spotifyPlayer.ActivityTypeString.equals(ActivityTypeString)) && !ActivityTypeString.isEmpty()) || (ActivityTypeString.isEmpty() && (!isBluetoothHeadsetConnected() || !audioManager.isWiredHeadsetOn()))){
            db.insertLog("Starting Spotify Playlist\n");
            spotifyPlayer = new SpotifyPlayer(mSpotifyAppRemote, mAccessToken, ActivityTypeString);
            spotifyPlayer.init();
        }
    }

    private void GmailDialog() {
        db.insertLog("Request G-mail account");
        if (gmail.equalsIgnoreCase("NULL")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to input a Gmail Address?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            final EditText taskEditText = new EditText(ActivitesListeners.this);
                            final AlertDialog SecondDialog = new AlertDialog.Builder(ActivitesListeners.this)
                                    .setTitle("Input Gmail Address :")
                                    .setMessage("")
                                    .setView(taskEditText)
                                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            gmail = taskEditText.getText().toString();
                                            setNewGmail(gmail);
                                            getCalendarEvents();
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .create();
                            SecondDialog.show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void setNewGmail(final String newGmailAccount) {
        db.insertLog("Set new g-mail account");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_UPDATE_ACCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                db.insertLog("Gmail added\n");
                                Log.e("TEST : ", jsonObject.getString("message"));
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                Log.e("TEST : ", jsonObject.getString("message"));
                                db.insertLog("Failed Adding Gmail\n");
                            }

                        } catch (JSONException e) {
                            Log.e("TEST : ",e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.insertLog("Failed Using Update GMail Script\n");
                        Log.e("TEST : ",error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.GMAIL_INTENT, newGmailAccount);
                params.put(Constants.ACCOUNTID_INTENT, String.valueOf(accountid));
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ChangeGmail) {
            final EditText taskEditText = new EditText(ActivitesListeners.this);
            final AlertDialog SecondDialog = new AlertDialog.Builder(ActivitesListeners.this)
                    .setTitle("Input Gmail Address :")
                    .setMessage("")
                    .setView(taskEditText)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gmail = taskEditText.getText().toString();
                            setNewGmail(gmail);
                            getCalendarEvents();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            SecondDialog.show();
        } else if (id == R.id.Logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to Log Out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (id == R.id.School_WorkLocation){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Use current location or type in address?")
                    .setCancelable(false)
                    .setPositiveButton("Address", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            final EditText taskEditText = new EditText(ActivitesListeners.this);
                            final AlertDialog SecondDialog = new AlertDialog.Builder(ActivitesListeners.this)
                                    .setTitle("Input Address :")
                                    .setMessage("")
                                    .setView(taskEditText)
                                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String address = taskEditText.getText().toString();
                                            LatLng latlng1 = getLocationFromAddress(getApplicationContext(), address);
                                            if (latlng1 != null) {
                                                loc1.setLongitude(latlng1.longitude);
                                                loc1.setLatitude(latlng1.latitude);
                                                SetUpLocationwork(latlng1.latitude + "," + latlng1.longitude);
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .create();
                            SecondDialog.show();
                        }
                    })
                    .setNegativeButton("Here", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LocationTrack locationTrack = new LocationTrack(ActivitesListeners.this);

                            if (locationTrack.canGetLocation()) {
                                double longitude = locationTrack.getLongitude();
                                double latitude = locationTrack.getLatitude();

                                loc1.setLatitude(latitude);
                                loc1.setLongitude(longitude);

                                SetUpLocation(loc1.getLatitude() + "," + loc1.getLongitude());
                            } else {
                                locationTrack.showSettingsAlert();
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (id == R.id.MapPedometer){
            openTheMap();
        } else if (id == R.id.Console){
            Intent i = new Intent(this, DebugConsole.class);
            startActivity(i);
        } else if (id == R.id.ClearDB){
            db.DeleteLogs();
        } else if (id == R.id.SetMorningNight){
            final EditText taskEditText = new EditText(ActivitesListeners.this);
            final AlertDialog SecondDialog = new AlertDialog.Builder(ActivitesListeners.this)
                    .setTitle("Input Times ( separated by , ) :")
                    .setMessage("")
                    .setView(taskEditText)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newTimes = taskEditText.getText().toString();
                            String[] strings = newTimes.split(",");
                            String MorningTime = strings[0];
                            String NightTime = strings[1];
                            db.insertLog("Set Morning/night routine");
                            setMorningNightRoutine(MorningTime, NightTime);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            SecondDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void ReadLocation() {
        //db.insertLog("Reading Location...\n");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_READ_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                db.insertLog("Location Read\n");
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                work = jsonObject.getString("work");
                                home = jsonObject.getString("home");

                                String[] parts = work.split(",");
                                if(parts.length >= 2) {
                                    String first = parts[0];
                                    String second = parts[1];
                                    db.insertLog("Set Work Coordinates");
                                    workLL = new Location("");
                                    workLL.setLatitude(Double.valueOf(first));
                                    workLL.setLongitude(Double.valueOf(second));
                                }

                                parts = home.split(",");
                                if(parts.length >= 2) {
                                    String first = parts[0];
                                    String second = parts[1];
                                    db.insertLog("Set Home Coordinates");
                                    homeLL = new Location("");
                                    homeLL.setLatitude(Double.valueOf(first));
                                    homeLL.setLongitude(Double.valueOf(second));
                                }
                            } else {
                                db.insertLog("Failed Getting Work/Home LatLng\n");
                                Log.d("TEST : ", jsonObject.getString("message"));
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            db.insertLog("Error Inside Getting Work/Home\n");
                            Log.d("TEST : ", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.insertLog("Failed Using Work/Home LatLng Script\n");
                        Log.d("TEST : ", error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.USERID_INTENT, String.valueOf(accountid));
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void SetUpLocationwork(final String workAdd) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_ADDWORK_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                db.insertLog("Added Work Location\n");
                                Log.d("TEST : ", jsonObject.getString("message"));
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                db.insertLog("Failed Adding Work Location\n");
                                Log.d("TEST : ", jsonObject.getString("message"));
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            db.insertLog("Failed Getting Work Response\n");
                            Log.d("TEST : ", e.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.insertLog("Failed Using Work Location Script\n");
                        Log.d("TEST : ", error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.USERID_INTENT, String.valueOf(accountid));
                params.put(Constants.WORK_INTENT, workAdd);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void SetUpLocation(final String homeAdd) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_ADD_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                db.insertLog("Added Home Location\n");
                                Log.d("TEST : ", jsonObject.getString("message"));
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                db.insertLog("Failed Adding Home Location\n");
                                Log.d("TEST : ", jsonObject.getString("message"));
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            db.insertLog("Failed Getting Home Response\n");
                            Log.d("TEST : ", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.insertLog("Failed Using Home Location Script\n");
                        Log.d("TEST : ", error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.USERID_INTENT, String.valueOf(accountid));
                params.put(Constants.HOME_INTENT, homeAdd);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void StartDrivingOptions() {
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, DrivingOptions.class);
        intent.putExtra("accountid", accountid);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("TEST : ", e.getMessage());
        }
    }

    private void StartCyclingOptions() {
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, CyclingOptions.class);
        intent.putExtra("accountid", accountid);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("TEST : ", e.getMessage());
        }
    }

    private void requestDoNotDisturbPermission() {
        //TO SUPPRESS API ERROR MESSAGES IN THIS FUNCTION, since Ive no time to figrure our Android SDK suppress stuff
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        db.insertLog("Getting permission for DND\n");
        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        startActivityForResult(intent, 2);
    }

    private void requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp() {
        //TO SUPPRESS API ERROR MESSAGES IN THIS FUNCTION, since Ive no time to figrure our Android SDK suppress stuff
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        try {
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            db.insertLog("Do Not Disturb On");
        }catch(Exception e){
            requestDoNotDisturbPermission();

        }
    }

    private void turnOffDoNotDisturb() {
        //TO SUPPRESS API ERROR MESSAGES IN THIS FUNCTION, since Ive no time to figrure our Android SDK suppress stuff
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        db.insertLog("Do Not Disturb OFF\n");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void StartDrivingService() {
        if (recordRoute) {
            Intent i = new Intent(getApplicationContext(), MapService.class);
            i.putExtra("temp", false);
            i.putExtra(Constants.POLICY_ID, 4);
            startService(i);
        }
        requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();
        Intent myIntent = new Intent(ActivitesListeners.this, LockedScreen.class);
        ActivitesListeners.this.startActivity(myIntent);
        Intent intent = new Intent(this, Driving_Policy_Service.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        intent.putExtra(Constants.MUSIC_INTENT, musicPlayer);
        intent.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        intent.putExtra(Constants.TIME_INTENT, timeRecord);
        intent.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        startService(intent);
        if (musicPlayer) {
            connected("CAR");
        }
    }

    private void StartCyclingService() {
        if (recordRoute) {
            Intent i = new Intent(getApplicationContext(), MapService.class);
            i.putExtra("temp", false);
            i.putExtra(Constants.POLICY_ID, 3);
            startService(i);
        }
        Intent speed = new Intent(this, SpeedAndDistance.class);
        startService(speed);
        Intent intent = new Intent(this, Cycling_Policy_Service.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        intent.putExtra(Constants.MUSIC_INTENT, musicPlayer);
        intent.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        intent.putExtra(Constants.TIME_INTENT, timeRecord);
        intent.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        startService(intent);
        if (musicPlayer) {
            connected("BIKE");
        }
    }

    private void StopWalkingPolicy() {
        stopService(new Intent(this, pedometerService.class));
        stopService(new Intent(this, MapService.class));
    }

    private void StopRunningPolicy() {
        stopService(new Intent(this, pedometerService.class));
        stopService(new Intent(this, MapService.class));
    }

    private void StopCyclingPolicy() {
        stopService(new Intent(this, pedometerService.class));
        stopService(new Intent(this, MapService.class));
    }

    private void StopDrivingPolicy() {
        stopService(new Intent(this, pedometerService.class));
        stopService(new Intent(this, MapService.class));
        turnOffDoNotDisturb();
    }

    private void getWalkingSettings(int aPolicyID) {
        readSettings(Constants.MUSIC_SETTING, aPolicyID);
        readSettings(Constants.PEDOMETER_SETTING, aPolicyID);
        readSettings(Constants.TIME_SETTING, aPolicyID);
        readSettings(Constants.DISTANCE_SETTING, aPolicyID);
        readSettings(Constants.RECORD_ROUTE, aPolicyID);
        readSettings(Constants.TEXT_TO_SPEECH_SETTING, aPolicyID);
        readSettings(Constants.AUTO_REPLY_SETTING, aPolicyID);
        readSettings(Constants.CALL_REPLY_SETTING, aPolicyID);
    }

    private void readSettings(final String aName, final int aPolicyID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_READ_SETTING,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (aName) {
                                case Constants.MUSIC_SETTING:
                                    musicPlayer = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.PEDOMETER_SETTING:
                                    pedometer = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.TIME_SETTING:
                                    timeRecord = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.DISTANCE_SETTING:
                                    dist_speed = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.RECORD_ROUTE:
                                    recordRoute = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.TEXT_TO_SPEECH_SETTING:
                                    notificationTTS = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.AUTO_REPLY_SETTING:
                                    autoReply = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    break;
                                case Constants.CALL_REPLY_SETTING:
                                    callReply = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                                    switch (aPolicyID) {
                                        case 1:
                                            db.insertLog("Starting Walking Service\n");
                                            StartWalkingService();
                                            break;
                                        case 2:
                                            db.insertLog("Starting Running Service\n");
                                            StartRunningService();
                                            runningService = true;
                                            break;
                                        case 3:
                                            db.insertLog("Starting Cycling Service\n");
                                            StartCyclingService();
                                            cyclingService = true;
                                            break;
                                        case 4:
                                            db.insertLog("Starting Driving Service\n");
                                            StartDrivingService();
                                            drivingService = true;
                                            break;
                                    }
                            }

                        } catch (JSONException e) {
                            db.insertLog("Failed Reading Setting\n");
                            Log.e("TEST : ", e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.insertLog("Failed Reading Setting Script\n");
                        Log.e("TEST : ", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.ACCOUNTID_INTENT, String.valueOf(accountid));
                params.put(Constants.POLICY_ID, String.valueOf(aPolicyID));
                params.put(Constants.PARAM_NAME, aName);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onDestroy() {
        db.insertLog("Destroying ActivitiesListeners\n");
        unregisterReceiver(updateUIReciver);
        unregisterReceiver(reciever);
        super.onDestroy();
    }

    private void StartWalkingService() {
        if (recordRoute) {
            Intent i = new Intent(getApplicationContext(), MapService.class);
            i.putExtra("temp", false);
            i.putExtra(Constants.POLICY_ID, 1);
            startService(i);
        }
        Intent intent = new Intent(this, Walking_Policy_Service.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        intent.putExtra(Constants.MUSIC_INTENT, musicPlayer);
        intent.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        intent.putExtra(Constants.TIME_INTENT, timeRecord);
        intent.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        startService(intent);
        if (musicPlayer) {
            connected("WALK");
        }
    }

    private void StartWalkingOptions() {
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, WalkingOptions.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        intent.putExtra(Constants.USERNAME_INTENT, username);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("TEST : ", e.getMessage());
        }
    }

    private void StartRunningService() {
        //create a new intent that will start walkingPolicy service
        //MakeNotifications("on Foot", "on Foot");
        if (recordRoute) {
            Intent i = new Intent(getApplicationContext(), MapService.class);
            i.putExtra("temp", false);
            i.putExtra(Constants.POLICY_ID, 2);
            startService(i);
        }
        Intent intent = new Intent(this, Running_Policy_Service.class);
        intent.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        intent.putExtra(Constants.MUSIC_INTENT, musicPlayer);
        intent.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        intent.putExtra(Constants.TIME_INTENT, timeRecord);
        intent.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        startService(intent);
        if (musicPlayer) {
            connected("RUN");
        }
    }

    private void StartRunningOptions() {
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, RunningOptions.class);
        intent.putExtra("accountid", accountid);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("TEST : ", e.getMessage());
        }
    }

    private void Start() {
        db.insertLog("Starting Timer Service\n");
        startService(new Intent(ActivitesListeners.this, Timer_Service.class));
    }

//    private void getEvents() {
//        //db.insertLog("Getting phone events\n");
//        String[] projection = new String[]{CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION};
//        Calendar startTime = Calendar.getInstance();
//        startTime.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1, 23, 59);
//        Calendar endTime = Calendar.getInstance();
//        endTime.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), 23, 59);
//        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ))";
//        Cursor cursor = this.getBaseContext().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);
//        assert cursor != null;
//        if (cursor.moveToFirst()) {
//            do {
//                try {
//                    long start = new Date(cursor.getLong(3)).getTime();
//                    long end = new Date(cursor.getLong(4)).getTime();
//                    scheduleJobs(start, cursor.getString(1), 0);
//                    scheduleJobs(end, (cursor.getString(1) + "1"), 1);
//                } catch (Exception e) {
//                    Log.d("TEST : ", e.toString());
//                }
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//    }

    private long D2MS(int month, int day, int year, int hour, int minute, int seconds) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute, seconds);

        return c.getTimeInMillis();
    }

    private void getCalendarEvents() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("NOW");
        updateUIReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Calendar calendar = Calendar.getInstance();

                int thisYear = calendar.get(Calendar.YEAR);
                int thisMonth = calendar.get(Calendar.MONTH);
                int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
                //UI update here
                summary = intent.getStringExtra("summary");
                hour = intent.getIntExtra("sHour", 0);
                mins = intent.getIntExtra("sMins", 0);
                secs = intent.getIntExtra("sSecs", 0);
                ehour = intent.getIntExtra("eHour", 0);
                emins = intent.getIntExtra("eMins", 0);
                esecs = intent.getIntExtra("eSecs", 0);

                long FirstD = D2MS(thisMonth, thisDay, thisYear, hour, mins, secs);
                long SecondD = D2MS(thisMonth, thisDay, thisYear, ehour, emins, esecs);
                boolean isThere = true;

                if (calendarList.size() == 0) {
                    calendarList.add(summary);
                    scheduleJobs(FirstD, summary, 0);
                    scheduleJobs(SecondD, summary + "1", 1);
                }

                for (int i = 0; i < calendarList.size(); i++) {
                    if (calendarList.get(i).equalsIgnoreCase(summary)) {
                        isThere = true;
                        break;
                    }else{
                        isThere = false;
                    }
                }

                if(!isThere){
                    calendarList.add(summary);
                    db.insertLog("Schedule Event\n");
                    scheduleJobs(FirstD, summary, 0);
                    scheduleJobs(SecondD, summary + "1", 1);
                }

//                Toast.makeText(ActivitesListeners.this, summary + "\n" + "start hour : "+hour+" - min : "+mins+" - sec : "+secs
//                        + "\n" + "end hour : "+ehour+" - min : "+emins+" - sec : "+esecs, Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(updateUIReciver, filter);

        if (!username.equals("tester1234")) {
            Intent i = new Intent(this, googleCalendarService.class);
            //db.insertLog("Starting Google Calendar Service\n");
            i.putExtra("calendarid", gmail);
            i.putExtra("username", username);
            startService(i);
        }
    }



    private void scheduleJobs(long setTime, String title, int startend) {
        Bundle bun = new Bundle();
        db.insertLog("Scheduling Jobs\n");
        //requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();

        bun.putInt("StartEnd", startend);

        long time = System.currentTimeMillis();
//        long setTime = (new Date(cursor.getLong(3))).getTime();
        long newtime = (setTime - time);
        newtime = newtime / 1000;
        if (newtime > 0) {
            Job myJob = mDispatcher.newJobBuilder()
                    .setService(MyJobService.class)
                    //.setTag(cursor.getString(1))
                    .setTag(title + startend)
                    .setRecurring(false)
                    .setTrigger(Trigger.executionWindow((int) newtime, (int) (newtime + 5)))
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    .setReplaceCurrent(false)
                    .setExtras(bun)
                    .build();
            mDispatcher.mustSchedule(myJob);
            Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT).show();
            Log.e("TEST : ", "Job Scheduled");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void show(ActivityType activityType) {
        switch (activityType) {
            case IN_VEHICLE:
                if (!previousActivity.equalsIgnoreCase("CAR")) {
                    Log.d("TEST : ", "CAR");
                    previousActivity = "CAR";
                    if (username.equals("tester1234")){
                        StartDrivingService();
                    } else {
                        getWalkingSettings(Constants.DRIVING_POLICY);
                    }
                    db.insertLog("Starting: DRIVING Activity\n");
                }
                break;
            case ON_BICYCLE:
                if (!previousActivity.equalsIgnoreCase("BIKE")) {
                    Log.d("TEST : ", "BIKE");
                    previousActivity = "BIKE";
                    if (username.equals("tester1234")){
                        StartCyclingService();
                    } else {
                        getWalkingSettings(Constants.CYCLING_POLICY);
                    }
                    db.insertLog("Starting: CYCLING Activity\n");
                }
                break;
            case ON_FOOT:
            case WALKING:
                if (!previousActivity.equalsIgnoreCase("WALKING")) {
                    Log.d("TEST : ", "WALKING");
                    previousActivity = "WALKING";
                    if (username.equals("tester1234")){
                        StartWalkingService();
                    } else {
                        getWalkingSettings(Constants.WALKING_POLICY);
                    }
                    db.insertLog("Starting: WALKING Activity\n");
                }
                break;
            case STILL:
                if (!previousActivity.equalsIgnoreCase("STILL")) {
                    Log.d("TEST : ", "STILL");
                    previousActivity = "STILL";
                    db.insertLog("Starting: STILL Activity\n");
                    if (isBluetoothHeadsetConnected()) {
                        StopWalkingPolicy();
                        StopRunningPolicy();
                        StopCyclingPolicy();
                        StopDrivingPolicy();
                        turnOffDoNotDisturb();
                    }
                }
                break;
            case TILTING:
                if (!previousActivity.equalsIgnoreCase("TILTING")) {
                    Log.d("TEST : ", "TILTING");
                    previousActivity = "TILTING";
                    db.insertLog("Starting: TILTING Activity\n");
                }
                break;
            case RUNNING:
                if (!previousActivity.equalsIgnoreCase("RUNNING")) {
                    Log.d("TEST : ", "RUNNING");
                    previousActivity = "RUNNING";
                    if (username.equals("tester1234")){
                        StartRunningService();
                    } else {
                        getWalkingSettings(Constants.RUNNING_POLICY);
                    }
                    db.insertLog("Starting: RUNNING Activity\n");
                }
                break;
            case UNKNOWN:
            case DEFAULT:
            default:
                if (!previousActivity.equalsIgnoreCase("DEFAULT")) {
                    Log.d("TEST : ", "DEFAULT");
                    previousActivity = "DEFAULT";
                    db.insertLog("Starting: DEFAULT Activity\n");
                }
                    break;
        }
    }

    private static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    @Override
    public void warnTracking() {
        db.insertLog("TRACKING STARTED\n");
        Log.e("TEST : ","Tracking Began");
        Toast.makeText(this, "Tracking Began", Toast.LENGTH_LONG).show();
    }

    @Override
    public void warnTrackingHasBeenStopped() {
        db.insertLog("TRACKING STOPPED\n");
        Log.e("TEST : ","Tracking Stopped");
        Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_LONG).show();
    }

    class EventReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals("GET_EVENTS")) {
                //getEvents();
                getCalendarEvents();
            }
        }
    }
}
