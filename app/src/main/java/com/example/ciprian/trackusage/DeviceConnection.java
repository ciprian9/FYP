package com.example.ciprian.trackusage;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class DeviceConnection {

    private String ConType;

    public DeviceConnection(){
        this.ConType = "";
    }

    public void WhichConnection(ConnectivityManager connManager) {
        //Create Connectivity Manager and Network types
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if ((activeNetwork != null) && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
            ConType = "Connected to WIFI";
        } else if ((activeNetwork != null) && (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)) {
            ConType = "Connected to data";
        } else {
            ConType = "Not Connected";
        }
    }


    public String getConType() {
        return ConType;
    }
}
