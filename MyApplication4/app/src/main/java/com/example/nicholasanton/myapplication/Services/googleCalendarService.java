package com.example.nicholasanton.myapplication.Services;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nicholasanton.myapplication.Views.ActivitesListeners;
import com.example.nicholasanton.myapplication.Classes.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class googleCalendarService extends Service {

    ClipboardManager clipboard;
    String username1;
    private static String ROOTURL = "http://192.168.0.241/Android/php/";
    String token;
    String calendarid;
    Boolean inOrOut = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            calendarid = bundle.getString("calendarid");
            username1 = bundle.getString("username");
        }
        clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(mPrimaryChangeListener);
        googleCalendarConnection(ROOTURL+"tokenExists.php", 0);
        return super.onStartCommand(intent, flags, startId);
    }

    public void start(){
        Intent i = new Intent(this, ActivitesListeners.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    ClipboardManager.OnPrimaryClipChangedListener mPrimaryChangeListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            start();
            token = (String) clipboard.getText();
            googleCalendarConnection(ROOTURL+"createToken.php", 2);
        }
    };

    public void googleCalendarConnection(final String apath, final int anAction){
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
                                        googleCalendarConnection(ROOTURL+"getEvents.php", 3);
                                    } else {
                                        googleCalendarConnection(ROOTURL+"getcalendar.php", 1);
                                    }
                                    break;
                                case 1:
                                    if(apath.equals(ROOTURL+"getcalendar.php")){
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
                                    if (jsonObject.getString("message").equalsIgnoreCase("ok") && !jsonObject.getBoolean("error")){
                                        googleCalendarConnection(ROOTURL+"getEvents.php", 3);
                                    }
                                    break;
                                case 3:
                                    JSONArray the_json_array = jsonObject.getJSONArray("events");
                                    //textView = findViewById(R.id.textView2);
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
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
