package com.example.nicholasanton.myapplication.Views;

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
import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.R;
import com.example.nicholasanton.myapplication.Classes.RequestHandler;
import com.example.nicholasanton.myapplication.Classes.SaveSettings;

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
        setContentView(R.layout.activity_cycling_options); //This is cycle options not running so there is no calories burned switch meaning CaloriesBurnet is null causing it to crash
        //fix the layout  then you can uncomment the listener for calories burned
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
        };

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

        RecomendDestinations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSettings db = new SaveSettings(accountid, 3, "RecomendDestinations", isChecked, getApplicationContext());
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
                                } else if(aName.equals("ShowSpeed")){
                                    showSpeed = Boolean.valueOf(jsonObject.getString("status"));
                                }else if(aName.equals("RecomendDestinations")){
                                    recomendDestinations = Boolean.valueOf(jsonObject.getString("status"));
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("accountid", String.valueOf(accountid));
                params.put("policyid", String.valueOf(3));
                params.put("name", aName);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    public void VarsToForm(){
        //Read the database values and update the activity to reflect those values

        readSettings("MusicPlayer", playHeadphones);
        readSettings("ShowSpeed", ShowSpeed);
        readSettings("RecomendDestinations", RecomendDestinations);
        readSettings("RecordRoute", RecordRoute);
        readSettings("NotificationTTS", NotficationTTS);
        readSettings("AutoReply", AutoReply);
        readSettings("CallReply", CallReply);
    }
}