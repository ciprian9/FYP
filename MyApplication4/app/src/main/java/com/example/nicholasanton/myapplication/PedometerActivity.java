package com.example.nicholasanton.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class PedometerActivity extends AppCompatActivity{
    private boolean pedometer, time, dist;
    private TextView TvSteps, TvDistance, TvTimer, TvSpeed;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private static final String TEXT_MET_STEPS = "Number of Meters: ";
    private static final String TEXT_STO_STEPS = "Stopwatch: ";
    private static final String TEXT_SPE_STEPS = "Speed: ";
    private DataReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        receiver = new DataReceiver();
        registerReceiver(receiver, new IntentFilter("GET_PEDOMETER_DATA"));

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                pedometer = extras.getBoolean(Constants.PEDOMETER_INTENT);
                time = extras.getBoolean(Constants.TIME_INTENT);
                dist = extras.getBoolean(Constants.DISTANCE_INTENT);
            }
        }

        TvSteps =  findViewById(R.id.tv_steps);
        TvDistance =  findViewById(R.id.tvDistance);
        TvTimer =  findViewById(R.id.tvTimer);
        TvSpeed =  findViewById(R.id.tvSpeed);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("GET_PEDOMETER_DATA")) {
                float speed = intent.getFloatExtra("speed", 0);
                float distance = intent.getFloatExtra("dist", 0);
                int min = intent.getIntExtra("min", 0);
                int sec = intent.getIntExtra("sec", 0);
                int hour = intent.getIntExtra("hour", 0);
                int steps = intent.getIntExtra("steps", 0);
                if(time) {
                    TvTimer.setText(TEXT_STO_STEPS + String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec));
                }
                if(dist) {
                    TvDistance.setText(TEXT_MET_STEPS + String.format("%.02f", distance));
                    TvSpeed.setText(TEXT_SPE_STEPS + String.format("%.02f", speed));
                }
                if(pedometer) {
                    TvSteps.setText(TEXT_NUM_STEPS + steps);
                }
            }
        }
    }

}
