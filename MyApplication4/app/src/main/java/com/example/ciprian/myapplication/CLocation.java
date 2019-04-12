package com.example.ciprian.myapplication;

/**
 * Uses code from : https://stackoverflow.com/questions/15570542/determining-the-speed-of-a-vehicle-using-gps-in-android
 * Class that will get values for the pedometer service
 * */

import android.location.Location;

public class CLocation extends Location {

    private boolean bUseMetricUnits;

    public CLocation(Location location, boolean bUseMetricUnits) {
        super(location);
        this.bUseMetricUnits = bUseMetricUnits;
    }


    private boolean getUseMetricUnits()
    {
        return !this.bUseMetricUnits;
    }

    public void setUseMetricunits(boolean bUseMetricUntis)
    {
        this.bUseMetricUnits = bUseMetricUntis;
    }

    //gets distance from one location to another
    @Override
    public float distanceTo(Location dest) {
        // TODO Auto-generated method stub
        if(dest != null) {
            float nDistance = super.distanceTo(dest);
            if (this.getUseMetricUnits()) {
                nDistance = nDistance * 3.28083989501312f;
            }
            return nDistance;
        }else{
            return 0;
        }
    }

    //gets accuracy of a user
    @Override
    public float getAccuracy() {
        // TODO Auto-generated method stub
        float nAccuracy = super.getAccuracy();
        if(this.getUseMetricUnits())
        {
            nAccuracy = nAccuracy * 3.28083989501312f;
        }
        return nAccuracy;
    }

    //gets altitude of a user
    @Override
    public double getAltitude() {
        // TODO Auto-generated method stub
        double nAltitude = super.getAltitude();
        if(this.getUseMetricUnits())
        {
            nAltitude = nAltitude * 3.28083989501312d;
        }
        return nAltitude;
    }

    //gets speed of user
    @Override
    public float getSpeed() {
        // TODO Auto-generated method stub
        float nSpeed = super.getSpeed() * 3.6f;
        if(this.getUseMetricUnits())
        {
            nSpeed = nSpeed * 2.2369362920544f/3.6f;
        }
        return nSpeed;
    }



}
