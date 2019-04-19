package com.example.ciprian.myapplication.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.ciprian.myapplication.Services.bedtimeRoutineService;

public class NightReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, bedtimeRoutineService.class);
        context.startService(intent1);
    }
}
