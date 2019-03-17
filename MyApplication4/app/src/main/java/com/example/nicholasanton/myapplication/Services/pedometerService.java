package com.example.nicholasanton.myapplication.Services;

//COPIED FROM http://www.gadgetsaint.com/android/create-pedometer-step-counter-android/#.XG65DOj7S70
//USED ALGORITHM FROM http://www.lewisgavin.co.uk/Step-Tracker-Android/

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.Classes.StepDetector;
import com.example.nicholasanton.myapplication.Interfaces.StepListener;

public class pedometerService extends Service implements SensorEventListener, StepListener {

    private StepDetector simpleStepDetector;
    private int numSteps;
    private float distanceMeters;
    private float speed;
    private boolean pedometer, time, dist_speed;
    private DataHandler db;

    Handler customHandler = new Handler();

    long startTime=0L, timeInMilliseconds=0L, updateTime=0L, timeSwapBuff=0L;
    int secs, mins, hour;

    Runnable updateTimerThread = new Runnable(){

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
            updateTime = timeSwapBuff+timeInMilliseconds;
            secs=(int)(updateTime/1000);
            mins=secs/60;
            secs%=60;
            hour = mins / 60;
            Intent data = new Intent();
            data.setAction("GET_PEDOMETER_DATA");
            data.putExtra("sec", secs);
            data.putExtra("min", mins);
            data.putExtra("hour", hour);
            data.putExtra("steps", numSteps);
            data.putExtra("dist", distanceMeters);
            data.putExtra("speed", speed);
            sendBroadcast(data);
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new DataHandler(this);
        Bundle extras = intent.getExtras();

        //just checking
        if( extras != null ) {
            pedometer = extras.getBoolean(Constants.PEDOMETER_INTENT);
            time = extras.getBoolean(Constants.TIME_INTENT);
            dist_speed = extras.getBoolean(Constants.DISTANCE_INTENT);
        }

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);


        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        customHandler.removeCallbacks(updateTimerThread);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        //db.insertLog("Step");
        numSteps++;
        long handm;
        handm = (hour * 3600) + (mins * 60) + secs;
        distanceMeters = getDistanceRun(numSteps);
        speed = distanceMeters / handm;
    }

    public float getDistanceRun(long steps){
        return (float) ((float) steps * 0.762);
    }

}
