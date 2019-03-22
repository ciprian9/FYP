package com.example.nicholasanton.myapplication.Services;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Views.ActivitesListeners;
import com.example.nicholasanton.myapplication.Classes.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.nicholasanton.myapplication.Interfaces.Constants.GOOGLEROOTURL;

public class googleCalendarService extends Service {

    private ClipboardManager clipboard;
    private String token;
    private String calendarid;
    private Boolean inOrOut = false;
    private DataHandler db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new DataHandler(this);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            calendarid = bundle.getString("calendarid");
            String username1 = bundle.getString("username");
        }
        clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(mPrimaryChangeListener);
        googleCalendarConnection(GOOGLEROOTURL+"tokenExists.php", 0);
        return super.onStartCommand(intent, flags, startId);
    }

    private void start(){
        Intent i = new Intent(this, ActivitesListeners.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @SuppressWarnings("deprecation")
    private final ClipboardManager.OnPrimaryClipChangedListener mPrimaryChangeListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            db.insertLog("Got Clipboard");
            start();
            token = (String) clipboard.getText();
            googleCalendarConnection(GOOGLEROOTURL+"createToken.php", 2);
        }
    };

    private void googleCalendarConnection(final String apath, final int anAction){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                apath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (anAction){
                                case 0:
                                    boolean bool = jsonObject.getBoolean("exists");
                                    if (bool){
                                        googleCalendarConnection(GOOGLEROOTURL+"getEvents.php", 3);
                                    } else {
                                        googleCalendarConnection(GOOGLEROOTURL+"getcalendar.php", 1);
                                    }
                                    break;
                                case 1:
                                    if(apath.equals(GOOGLEROOTURL+"getcalendar.php")){
                                        if (!inOrOut) {
                                            String url = jsonObject.getString("url");
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                            inOrOut = true;
                                        }
                                    }
                                    break;
                                case 2:
                                    db.insertLog("Creating Token");
                                    if (jsonObject.getString("message").equalsIgnoreCase("ok") && !jsonObject.getBoolean("error")){
                                        googleCalendarConnection(GOOGLEROOTURL+"getEvents.php", 3);
                                    }
                                    break;
                                case 3:
                                    JSONArray the_json_array = jsonObject.getJSONArray("events");
                                    int len = the_json_array.length();
                                    for (int i = 0; i < len; i++) {
                                        JSONObject another_json_object = the_json_array.getJSONObject(i);

                                        String toSplit = another_json_object.getString("startTime");
                                        String result[] = toSplit.split("T");
                                        String returnValue = result[result.length -1];
                                        returnValue = returnValue.replaceFirst(".$","");
                                        String results[] = returnValue.split(":");
                                        int hour = Integer.parseInt(results[0]);
                                        int minute = Integer.parseInt(results[1]);
                                        int second = Integer.parseInt(results[2]);

                                        toSplit = another_json_object.getString("endTime");
                                        result = toSplit.split("T");
                                        returnValue = result[result.length -1];
                                        returnValue = returnValue.replaceFirst(".$","");
                                        results = returnValue.split(":");
                                        int ehour = Integer.parseInt(results[0]);
                                        int eminute = Integer.parseInt(results[1]);
                                        int esecond = Integer.parseInt(results[2]);

                                        Intent local = new Intent();
                                        local.setAction("NOW");
                                        local.putExtra("summary", another_json_object.getString("summary"));
                                        local.putExtra("sHour", hour);
                                        local.putExtra("sMins", minute);
                                        local.putExtra("sSecs", second);
                                        local.putExtra("eHour", ehour);
                                        local.putExtra("eMins", eminute);
                                        local.putExtra("eSecs", esecond);
                                        getApplicationContext().sendBroadcast(local);
                                    }
                            }
                        }catch(JSONException e){
                            Log.e("TEST : ", e.getMessage());
                            db.insertLog("Error in google calendar code");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.insertLog("Failed inside of calendar script");
                        Log.e("TEST : ", error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                switch (anAction) {
                    case 0:
                    case 1:
                    case 3:
                        params.put("username", calendarid);
                        break;
                    case 2:
                        params.put("username", calendarid);
                        params.put("tokenCode", token);
                        break;
                }
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
