package com.example.nicholasanton.myapplication;

//COPIED FROM https://stackoverflow.com/questions/15570542/determining-the-speed-of-a-vehicle-using-gps-in-androidhttps://stackoverflow.com/questions/15570542/determining-the-speed-of-a-vehicle-using-gps-in-android

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

    @Override
    public float distanceTo(Location dest) {
        // TODO Auto-generated method stub
        float nDistance = super.distanceTo(dest);
        if(this.getUseMetricUnits())
        {
            nDistance = nDistance * 3.28083989501312f;
        }
        return nDistance;
    }

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
