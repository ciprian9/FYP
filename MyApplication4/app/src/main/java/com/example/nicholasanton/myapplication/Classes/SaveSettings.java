package com.example.nicholasanton.myapplication.Classes;

//https://www.youtube.com/watch?v=5vy55k542NM&list=PLk7v1Z2rk4hjQaV062aE_CW68xgXdYFpV

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nicholasanton.myapplication.Interfaces.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SaveSettings {

    private  final int accountid, policyid;
    private  final String name;
    private  final boolean status;
    private  final Context c;
    private boolean isOn;

    public SaveSettings(int accountid, int policyid, String name, boolean status, Context c){
        this.accountid = accountid;
        this.policyid = policyid;
        this.name = name;
        this.status = status;
        this.c = c;
        this.isOn = false;
    }


    public void registerSetting(final String apath){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                apath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(apath.equals(Constants.URL_READ_SETTING)){
                                    isOn = Boolean.valueOf(jsonObject.getString(Constants.DB_FLAG));
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.ACCOUNTID_INTENT, String.valueOf(accountid));
                params.put(Constants.POLICY_ID, String.valueOf(policyid));
                params.put(Constants.PARAM_NAME, name);
                if(!apath.equals(Constants.URL_READ_SETTING)) {
                    params.put(Constants.DB_FLAG, String.valueOf(status));
                }
                return params;
            }
        };
        RequestHandler.getInstance(c).addToRequestQueue(stringRequest);
    }
}
