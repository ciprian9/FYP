package com.example.ciprian.myapplication.Views;

/**
 * Activity that lets user configure the options for walking
 * */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ciprian.myapplication.DataHandler;
import com.example.ciprian.myapplication.Interfaces.Constants;
import com.example.ciprian.myapplication.R;
import com.example.ciprian.myapplication.Classes.RequestHandler;
import com.example.ciprian.myapplication.Classes.SaveSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WalkingOptions extends AppCompatActivity {
    private boolean   musicPlayer = false, pedometer = false, timeRecord = false,
                      dist_speed = false, recordRoute = false;
    private Switch playHeadphones, startPedometer, Time, Rest, RecordRoute;
    private int accountid;
    private String username;
    private DataHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_options);
        //data handler for logging for debugging purposes
        db = new DataHandler(this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            //if present read the account id and username or set account id to 0
            if(extras == null) {
                accountid= 0;
            } else {
                accountid= extras.getInt(Constants.ACCOUNTID_INTENT);
                username= extras.getString(Constants.USERNAME_INTENT);
            }
        }

        //set switches for user preferences
        playHeadphones =  findViewById(R.id.swPlayHeadphones);
        startPedometer = findViewById(R.id.swDoNotDisturb);
        Time =  findViewById(R.id.swGPSSpeech);
        Rest = findViewById(R.id.swRest);
        RecordRoute = findViewById(R.id.swRecordRoute);

        //Prompts user to install spotify if not already
        if(appInstalledOrNot()){
            playHeadphones.setEnabled(true);
        } else {
            //display a dialog to as to install spotify
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to install Spotify?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.spotify.music")));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music")));
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            //disable music player
            playHeadphones.setEnabled(false);
        }

        //check for the debugging account
        if (username.equals("local")) {
            VarsToForm();
        }
        //if user is logged in create the settings for the user if not existent already
        if (accountid != 0) {
            SaveSettings db = new SaveSettings(accountid, 1, "MusicPlayer", false, this);
            db.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db1 = new SaveSettings(accountid, 1, "Pedometer", false, this);
            db1.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db2 = new SaveSettings(accountid, 1, "Time", false, this);
            db2.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db3 = new SaveSettings(accountid, 1, "Distance_Speed", false, this);
            db3.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db4 = new SaveSettings(accountid, 1, "RecordRoute", false, this);
            db4.registerSetting(Constants.URL_SAVE_SETTING);
        }

        //set up all the switches
        playHeadphones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 1, "MusicPlayer", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                musicPlayer = isChecked;
            }
        });

        startPedometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 1, "Pedometer", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                pedometer = isChecked;
            }
        });

        Time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 1, "Time", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                timeRecord = isChecked;
            }
        });

        Rest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 1, "Distance_Speed", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                dist_speed = isChecked;
            }
        });

        RecordRoute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 1, "RecordRoute", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                recordRoute = isChecked;
            }
        });
    }

    //Code to check if application is installed
    private boolean appInstalledOrNot() {
        //check if the spotify package is installed or not
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.spotify.music", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    //Will read settings from database using PHP script and update the view
    private void readSettings(final String aName, final Switch aSwitch) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_READ_SETTING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (aSwitch != null) {
                                aSwitch.setChecked(Boolean.valueOf(jsonObject.getString("status")));
                                switch (aName) {
                                    case "MusicPlayer":
                                        musicPlayer = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "Pedometer":
                                        pedometer = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "Time":
                                        timeRecord = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "Distance_Speed":
                                        dist_speed = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "RecordRoute":
                                        recordRoute = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                }
                            }

                        } catch (JSONException e) {
                            db.insertLog("Error Reading Settings Walking\n");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.insertLog("Error Reading Settings Script Walking\n");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //pass in html parameters
                Map<String, String> params = new HashMap<>();
                params.put("accountid", String.valueOf(accountid));
                params.put("policyid", String.valueOf(1));
                params.put("name", aName);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onResume(){
        accountid = Objects.requireNonNull(getIntent().getExtras()).getInt("accountid");
        VarsToForm();
        super.onResume();
    }


    private void VarsToForm(){
        //read all the settings for the user preferences
        readSettings("MusicPlayer", playHeadphones);
        readSettings("Pedometer", startPedometer);
        readSettings("Time", Time);
        readSettings("Distance_Speed", Rest);
        readSettings("RecordRoute", RecordRoute);
    }
}
