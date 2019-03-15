package com.example.nicholasanton.myapplication.Views;

/*
    This class needs to be used to set the options for the walking policy. If the application is running and the current activity is walking the policy will come into action, this page will decide what the policy will do
    the following boolean values will be used to store the value of the options. This together with the playlist button will be used to config this policy
    This class needs to implement a datahandler class that will write each of these settings to the database, the values will then be read and set again when the user will reopen the page
    functions needed are VarsToForm and FormToVars


    TODO
    Add a battery percent level for the save resources
    Add Pedometer count the ammount of steps made
    possibly add distance walked aswell
    add a map that will record the route walked so you can view it on a map

    Issues to be fixed
    Database connection lost need to fix it ✓
    Need to add the media player to the Policy.class
    Need to inherit walking policy from policy to use the media player
    Need a loop to play each song (need to figure out how to only play once the audio file is finished playing)
    Need to start tracking usage (for now the ammount of time spent on each app according to android and record how many times we have opened the app)
    need to find a way to block incoming notifications e.g. text messages and turn the message to text-to-speech
    possibly allow the user to respond using voice


    TODO
    battery percent field ✓
    if we get track usage done we need a button to view the usage
    we need to rearage the window
    need to move to new policy
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.PlayLists;
import com.example.nicholasanton.myapplication.R;
import com.example.nicholasanton.myapplication.Classes.RequestHandler;
import com.example.nicholasanton.myapplication.Classes.SaveSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WalkingOptions extends AppCompatActivity {
    private boolean   musicPlayer = false, pedometer = false, timeRecord = false,
                      dist_speed = false, recordRoute = false;
    private Switch playHeadphones, startPedometer, Time, Rest, RecordRoute;
    private int accountid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_options);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                accountid= 0;
            } else {
                accountid= extras.getInt(Constants.ACCOUNTID_INTENT);
            }
        }

        playHeadphones =  findViewById(R.id.swPlayHeadphones);
        startPedometer = findViewById(R.id.swDoNotDisturb);
        Time =  findViewById(R.id.swGPSSpeech);
        Rest = findViewById(R.id.swRest);
        RecordRoute = findViewById(R.id.swRecordRoute);

        VarsToForm();
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
        };

        final Button MapBtn = findViewById(R.id.btnOpenMap);
        MapBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openTheMap();
            }
        });


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
                                } else if(aName.equals("Time")){
                                    timeRecord = Boolean.valueOf(jsonObject.getString("status"));
                                } else if(aName.equals("Distance_Speed")){
                                    dist_speed = Boolean.valueOf(jsonObject.getString("status"));
                                } else if(aName.equals("RecordRoute")){
                                    recordRoute = Boolean.valueOf(jsonObject.getString("status"));
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
                params.put("policyid", String.valueOf(1));
                params.put("name", aName);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onResume(){
        accountid = getIntent().getExtras().getInt("accountid");
        VarsToForm();
        super.onResume();
    }


    public void VarsToForm(){
        //Read the database values and update the activity to reflect those values

        readSettings("MusicPlayer", playHeadphones);
        readSettings("Pedometer", startPedometer);
        readSettings("Time", Time);
        readSettings("Distance_Speed", Rest);
        readSettings("RecordRoute", RecordRoute);
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
        i.putExtra(Constants.POLICY_ID, 1);
        startActivity(i);
    }

}
