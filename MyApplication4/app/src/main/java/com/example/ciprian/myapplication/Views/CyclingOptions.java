package com.example.ciprian.myapplication.Views;

/**
 * Activity that lets user configure the options for cycling
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

public class CyclingOptions extends AppCompatActivity {
    private boolean   musicPlayer = false, showSpeed = false, recomendDestinations = false,
            caloriesBurnt = false, recordRoute = false, notificationTTS = false, autoReply = false, callReply = false;
    private Switch playHeadphones, ShowSpeed, RecomendDestinations, CaloriesBurnt, RecordRoute, NotficationTTS, AutoReply, CallReply;
    private int accountid;
    private DataHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycling_options);
        db = new DataHandler(this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                accountid= 0;
            } else {
                accountid= extras.getInt("accountid");
            }
        }

        playHeadphones =  findViewById(R.id.swPlayHeadphones);
        ShowSpeed = findViewById(R.id.swDoNotDisturb);
        RecomendDestinations =  findViewById(R.id.swGPSSpeech);
        RecordRoute = findViewById(R.id.swRecordRoute);
        NotficationTTS = findViewById(R.id.swNotificationTTS);
        AutoReply = findViewById(R.id.swAutoReply);
        CallReply = findViewById(R.id.swCallReply);

        //Prompts user to install spotify if not already
        if(appInstalledOrNot()){
            playHeadphones.setEnabled(true);
        } else {
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
            playHeadphones.setEnabled(false);
        }

        VarsToForm();
        if (accountid != 0) {
            SaveSettings db = new SaveSettings(accountid, 3, "MusicPlayer", false, this);
            db.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db1 = new SaveSettings(accountid, 3, "ShowSpeed", false, this);
            db1.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db2 = new SaveSettings(accountid, 3, "RecomendDestinations", false, this);
            db2.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db3 = new SaveSettings(accountid, 3, "RecordRoute", false, this);
            db3.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db4 = new SaveSettings(accountid, 3, "NotificationTTS", false, this);
            db4.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db5 = new SaveSettings(accountid, 3, "AutoReply", false, this);
            db5.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db6 = new SaveSettings(accountid, 3, "CallReply", false, this);
            db6.registerSetting(Constants.URL_SAVE_SETTING);
        }

        playHeadphones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 3, "MusicPlayer", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
            }
        });

        ShowSpeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 3, "ShowSpeed", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
            }
        });

        RecordRoute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 3, "RecordRoute", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                recordRoute = isChecked;
            }
        });

        NotficationTTS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 3, "NotificationTTS", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                notificationTTS = isChecked;
            }
        });

        AutoReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 3, "AutoReply", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                autoReply = isChecked;
            }
        });

        CallReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 3, "CallReply", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                callReply = isChecked;
            }
        });
    }

    //Code to check if application is installed
    private boolean appInstalledOrNot() {
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
                                    case "ShowSpeed":
                                        showSpeed = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "RecomendDestinations":
                                        recomendDestinations = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "CaloriesBurnt":
                                        caloriesBurnt = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "RecordRoute":
                                        recordRoute = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "NotificationTTS":
                                        notificationTTS = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "AutoReply":
                                        autoReply = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                    case "CallReply":
                                        callReply = Boolean.valueOf(jsonObject.getString("status"));
                                        break;
                                }
                            }

                        } catch (JSONException e) {
                            db.insertLog("Error Reading Settings Cycling");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.insertLog("Error Reading Settings Script Cycling");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accountid", String.valueOf(accountid));
                params.put("policyid", String.valueOf(3));
                params.put("name", aName);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void VarsToForm(){
        readSettings("MusicPlayer", playHeadphones);
        readSettings("ShowSpeed", ShowSpeed);
        readSettings("RecomendDestinations", RecomendDestinations);
        readSettings("RecordRoute", RecordRoute);
        readSettings("NotificationTTS", NotficationTTS);
        readSettings("AutoReply", AutoReply);
        readSettings("CallReply", CallReply);
    }
}