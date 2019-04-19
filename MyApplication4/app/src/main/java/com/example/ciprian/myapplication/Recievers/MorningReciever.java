package com.example.ciprian.myapplication.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.ciprian.myapplication.Services.getTheWeather;

public class MorningReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, getTheWeather.class);
        context.startService(intent1);
    }
}
