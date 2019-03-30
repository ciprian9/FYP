package com.example.nicholasanton.myapplication.Services;

/**
 *  Used code from : http://www.gadgetsaint.com/android/create-pedometer-step-counter-android/#.XG65DOj7S70
 *  Used Algorithm from : http://www.lewisgavin.co.uk/Step-Tracker-Android
 *  Class to get the speed and the amount of steps the user produces and then forwards the information to a view
 *  */

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.nicholasanton.myapplication.Classes.StepDetector;
import com.example.nicholasanton.myapplication.Interfaces.StepListener;

public class pedometerService extends Service implements SensorEventListener, StepListener {

    private StepDetector simpleStepDetector;
    private int numSteps;
    private float distanceMeters;
    private float speed;

    private final Handler customHandler = new Handler();

    private long startTime=0L;
    private int secs, mins, hour;

    private final Runnable updateTimerThread = new Runnable(){
        //Main function that will send data as a broadcast to the reciever to display the information
        @Override
        public void run() {
            long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            long timeSwapBuff = 0L;
            long updateTime = timeSwapBuff + timeInMilliseconds;
            secs=(int)(updateTime /1000);
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

    //Main function that will register the listeners so the program will be enabled to listen for
    //steps of the user
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Everytime a step occurs this function will be called
    @Override
    public void step(long timeNs) {
        numSteps++;
        long handm;
        handm = (hour * 3600) + (mins * 60) + secs;
        distanceMeters = getDistanceRun(numSteps);
        speed = distanceMeters / handm;
    }

    //Returns the distance ran using simple maths formula
    private float getDistanceRun(long steps){
        return (float) ((float) steps * 0.762);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        customHandler.removeCallbacks(updateTimerThread);
    }

}
