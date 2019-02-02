package com.example.nicholasanton.myapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PedometerActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    private TextView textView;
    private StepDectector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private TextView TvSteps;
    private String where;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        Intent intent = getIntent();
        where = intent.getStringExtra("where");

        if (where.equals("Policy")) {
            moveTaskToBack(true);
        }
        StartPedometer();
    }

    public void StartPedometer(){
        // Get an instance of the SensorManager
        try {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            simpleStepDetector = new StepDectector();
            simpleStepDetector.registerListener(this);

            TvSteps = (TextView) findViewById(R.id.textView);
        } catch (Exception e){
            System.out.printf(e.toString());
        }

        if (where.equals("Policy")) {
            try {
                numSteps = 0;
                sensorManager.registerListener((SensorEventListener) PedometerActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            } catch (Exception e) {
                System.out.printf(e.toString());
            }
        }
    }



    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    public void step(long timeNs) {
        numSteps++;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
    }

}

