package com.example.nicholasanton.myapplication.Classes;

/**
 * Used to request data from the MySQL database
 * Video Used to Help : https://www.youtube.com/watch?v=5vy55k542NM&list=PLk7v1Z2rk4hjQaV062aE_CW68xgXdYFpV
 * */


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestHandler {
    private static RequestHandler mInstance;
    private RequestQueue mRequestQueue;
    private final Context mCtx;

    private RequestHandler(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    //Used to get the context and then add it to the queue to be able to get the data back
    public static synchronized RequestHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestHandler(context);
        }
        return mInstance;
    }

    //Returns a RequestQueue object to be able to start sending requests
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    //Adds the request to the queue
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}