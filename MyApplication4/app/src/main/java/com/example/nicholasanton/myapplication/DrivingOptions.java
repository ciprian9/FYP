package com.example.nicholasanton.myapplication;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DrivingOptions extends AppCompatActivity {
    private boolean   musicPlayer = false, showSpeed = false, recomendDestinations = false,
            caloriesBurnt = false, recordRoute = false, notificationTTS = false, autoReply = false, callReply = false, gpsSpeech = false, doNotDisturb = false;
    private Switch playHeadphones, DoNotDisturb, GPSSpeech, CaloriesBurnt, RecordRoute, NotficationTTS, AutoReply, CallReply;
    private int accountid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_options); //This is cycle options not running so there is no calories burned switch meaning CaloriesBurnet is null causing it to crash
        //fix the layout  then you can uncomment the listener for calories burned

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                accountid= 0;
            } else {
                accountid= extras.getInt("accountid");
            }
        }

        playHeadphones =  findViewById(R.id.swPlayHeadphones);
        DoNotDisturb = findViewById(R.id.swDoNotDisturb);
        GPSSpeech =  findViewById(R.id.swGPSSpeech);
        RecordRoute = findViewById(R.id.swRecordRoute);
        NotficationTTS = findViewById(R.id.swNotificationTTS);
        AutoReply = findViewById(R.id.swAutoReply);
        CallReply = findViewById(R.id.swCallReply);

        VarsToForm();
        if (accountid != 0) {
            SaveSettings db = new SaveSettings(accountid, 4, "MusicPlayer", false, this);
            db.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db1 = new SaveSettings(accountid, 4, "DoNotDisturb", false, this);
            db1.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db2 = new SaveSettings(accountid, 4, "GPSSpeech", false, this);
            db2.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db3 = new SaveSettings(accountid, 4, "RecordRoute", false, this);
            db3.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db4 = new SaveSettings(accountid, 4, "NotificationTTS", false, this);
            db4.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db5 = new SaveSettings(accountid, 4, "AutoReply", false, this);
            db5.registerSetting(Constants.URL_SAVE_SETTING);
            SaveSettings db6 = new SaveSettings(accountid, 4, "CallReply", false, this);
            db6.registerSetting(Constants.URL_SAVE_SETTING);
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
                SaveSettings db = new SaveSettings(accountid, 4, "MusicPlayer", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                musicPlayer = isChecked;
            }
        });

        DoNotDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 4, "DoNotDisturb", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                doNotDisturb = isChecked;
            }
        });

        GPSSpeech.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 4, "GPSSpeech", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                gpsSpeech = false;
            }
        });

//        CaloriesBurnt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SaveSettings db = new SaveSettings(accountid, 2, "CaloriesBurnt", isChecked, getApplicationContext());
//                db.registerSetting(Constants.URL_UPDATE_SETTING);
//            }
//        });

        RecordRoute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 4, "RecordRoute", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                recordRoute = isChecked;
            }
        });

        NotficationTTS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 4, "NotificationTTS", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                notificationTTS = isChecked;
            }
        });

        AutoReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 4, "AutoReply", isChecked, getApplicationContext());
                db.registerSetting(Constants.URL_UPDATE_SETTING);
                autoReply = isChecked;
            }
        });

        CallReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 4, "CallReply", isChecked, getApplicationContext());
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
                                } else if(aName.equals("DoNotDisturb")){
                                    doNotDisturb = Boolean.valueOf(jsonObject.getString("status"));
                                }else if(aName.equals("GPSSpeech")){
                                    gpsSpeech = Boolean.valueOf(jsonObject.getString("status"));
                                }else if(aName.equals("CaloriesBurnt")){
                                    caloriesBurnt= Boolean.valueOf(jsonObject.getString("status"));
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
                params.put("policyid", String.valueOf(4));
                params.put("name", aName);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    public void VarsToForm(){
        //Read the database values and update the activity to reflect those values

        readSettings("MusicPlayer", playHeadphones);
        readSettings("DoNotDisturb", DoNotDisturb);
        readSettings("GPSSpeech", GPSSpeech);
        //readSettings("CaloriesBurnt", CaloriesBurnt);
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
        //i.putExtra(Constants.CALORIES_INTENT, caloriesBurnt);
        i.putExtra(Constants.POLICY_ID, 4);
        startActivity(i);
    }

}