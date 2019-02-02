package com.example.nicholasanton.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

// Will listen to step alerts
public interface StepListener {
    public void step(long timeNs);

}