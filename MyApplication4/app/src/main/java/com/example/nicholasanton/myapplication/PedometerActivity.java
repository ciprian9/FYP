package com.example.nicholasanton.myapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class PedometerActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    private TextView TvSteps, TvDistance, TvTimer, TvSpeed;
    private StepDetector simpleStepDetector;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private static final String TEXT_MET_STEPS = "Number of Meters: ";
    private static final String TEXT_STO_STEPS = "Stopwatch: ";
    private static final String TEXT_SPE_STEPS = "Speed: ";
    private int numSteps;

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
            TvTimer.setText(TEXT_STO_STEPS+String.format("%02d",hour)+":"+String.format("%02d",mins)+":"+String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pedometer);

        // Get an instance of the SensorManager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps =  findViewById(R.id.tv_steps);
        TvDistance =  findViewById(R.id.tvDistance);
        TvTimer =  findViewById(R.id.tvTimer);
        TvSpeed =  findViewById(R.id.tvSpeed);

        sensorManager.registerListener(PedometerActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);


//        BtnStop.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                sensorManager.unregisterListener(MainActivity.this);
//                customHandler.removeCallbacks(updateTimerThread);
//                timeInMilliseconds = 0L;
//
//            }
//        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PedometerActivity.this, ActivitesListeners.class));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        long handm;
        handm = (hour * 3600) + (mins * 60) + secs;
        numSteps++;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        float distanceMeters = getDistanceRun(numSteps);
        TvDistance.setText(TEXT_MET_STEPS + String.format("%.02f", distanceMeters));
        float speed = distanceMeters / handm;
        TvSpeed.setText(TEXT_SPE_STEPS+String.format("%.02f", speed));
    }

    public float getDistanceRun(long steps){
        return (float) ((float) steps * 0.762);
    }

}
