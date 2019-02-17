package com.example.nicholasanton.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.nearby.messages.Distance;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RunningOptions extends AppCompatActivity {
    private boolean   musicPlayer = false, pedometer = false, timeRecord = false,
            dist_speed = false, recordRoute = false, notificationTTS = false, autoReply = false, callReply = false;
    private Switch playHeadphones, startPedometer, Time, Rest, RecordRoute, NotficationTTS, AutoReply, CallReply;
    private int accountid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_options);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                accountid= 0;
            } else {
                accountid= extras.getInt("accountid");
            }
        }

        playHeadphones =  findViewById(R.id.swPlayHeadphones);
        startPedometer = findViewById(R.id.swStartPedometer);
        Time =  findViewById(R.id.swTime);
        Rest = findViewById(R.id.swRest);
        RecordRoute = findViewById(R.id.swRecordRoute);
        NotficationTTS = findViewById(R.id.swNotificationTTS);
        AutoReply = findViewById(R.id.swAutoReply);
        CallReply = findViewById(R.id.swCallReply);

        VarsToForm();
        if (accountid != 0) {
            SaveSettings db = new SaveSettings(accountid, 2, "MusicPlayer", false, this);
            db.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db1 = new SaveSettings(accountid, 2, "Pedometer", false, this);
            db1.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db2 = new SaveSettings(accountid, 2, "Time", false, this);
            db2.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db3 = new SaveSettings(accountid, 2, "Distance_Speed", false, this);
            db3.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db4 = new SaveSettings(accountid, 2, "RecordRoute", false, this);
            db4.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db5 = new SaveSettings(accountid, 2, "NotificationTTS", false, this);
            db5.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db6 = new SaveSettings(accountid, 2, "AutoReply", false, this);
            db6.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db7 = new SaveSettings(accountid, 2, "CallReply", false, this);
            db7.registerSetting(Constants.URL_SAVE_SETTING);
        };



        final Button WalkingPlaylist = findViewById(R.id.walkingPlaylist);
        WalkingPlaylist.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mApiClient.disconnect();
                openPlaylists();
            }
        });

        final Button MapBtn = findViewById(R.id.btnOpenMap);
        MapBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openTheMap();
            }
        });


        playHeadphones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 2, "MusicPlayer", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
            }
        });

        startPedometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 2, "Pedometer", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
            }
        });

        Time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 2, "Time", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
            }
        });

        Rest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 2, "Distance_Speed", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
            }
        });

        RecordRoute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 2, "RecordRoute", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                recordRoute = isChecked;
            }
        });

        NotficationTTS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 2, "NotificationTTS", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                notificationTTS = isChecked;
            }
        });

        AutoReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 2, "AutoReply", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                autoReply = isChecked;
            }
        });

        CallReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 2, "CallReply", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                callReply = isChecked;
            }
        });
    }

    public void readSettings(final String aName, final Switch aSwitch) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_READ_SETTING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            if (aSwitch != null) {
                                aSwitch.setChecked(Boolean.valueOf(jsonObject.getString("status")));
                                if(aName.equals("MusicPlayer")) {
                                    musicPlayer = Boolean.valueOf(jsonObject.getString("status"));
                                } else if(aName.equals("Pedometer")){
                                    pedometer = Boolean.valueOf(jsonObject.getString("status"));
                                }else if(aName.equals("Time")){
                                    timeRecord = Boolean.valueOf(jsonObject.getString("status"));
                                }else if(aName.equals("Distance_Speed")){
                                    dist_speed= Boolean.valueOf(jsonObject.getString("status"));
                                }else if(aName.equals("RecordRoute")){
                                    recordRoute = Boolean.valueOf(jsonObject.getString("status"));
                                } else if(aName.equals("NotificationTTS")){
                                    notificationTTS = Boolean.valueOf(jsonObject.getString("status"));
                                } else if(aName.equals("AutoReply")){
                                    autoReply = Boolean.valueOf(jsonObject.getString("status"));
                                } else if(aName.equals("CallReply")){
                                    callReply = Boolean.valueOf(jsonObject.getString("status"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("accountid", String.valueOf(accountid));
                params.put("policyid", String.valueOf(2));
                params.put("name", aName);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    public void VarsToForm(){
        //Read the database values and update the activity to reflect those values

        readSettings("MusicPlayer", playHeadphones);
        readSettings("Pedometer", startPedometer);
        readSettings("Time", Time);
        readSettings("Distance_Speed", Rest);
        readSettings("RecordRoute", RecordRoute);
        readSettings("NotificationTTS", NotficationTTS);
        readSettings("AutoReply", AutoReply);
        readSettings("CallReply", CallReply);
    }

    public void openPlaylists(){
        Intent intent = new Intent(this, PlayLists.class);
        startActivity(intent);
    }

    public void openTheMap(){
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        i.putExtra(Constants.PEDOMETER_INTENT, pedometer);
        i.putExtra(Constants.TIME_INTENT, timeRecord);
        i.putExtra(Constants.DISTANCE_INTENT, dist_speed);
        i.putExtra(Constants.POLICY_ID, 2);
        startActivity(i);
    }

}

