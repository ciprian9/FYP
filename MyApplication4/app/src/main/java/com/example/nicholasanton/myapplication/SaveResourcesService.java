package com.example.nicholasanton.myapplication;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.os.BatteryManager;
import android.os.IBinder;
import android.widget.Toast;

import java.util.List;

public class SaveResourcesService extends Service {

    final class SecondThread implements Runnable{
        int serviceId;

        SecondThread(int serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run() {
            synchronized (this) {
                saveResources();
            }
        }
    }

    private Cursor cursor;
    private DataHandler db;
    private Thread theThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void saveResources(){
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;

        //Toast.makeText(this, "BATTERY", Toast.LENGTH_SHORT).show();
        db = new DataHandler(getApplicationContext());
        cursor = db.SelectSettingsQuery(Constants.BATTERY_LEVEL);
        cursor.moveToFirst();
        if (level <= cursor.getInt(Constants.COLUMN_SETTINGS_BATTERY)) {
            List<ApplicationInfo> packages;
            PackageManager pm;
            pm = getPackageManager();
            //get a list of installed apps.
            packages = pm.getInstalledApplications(0);

            ActivityManager mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            String myPackage = getApplicationContext().getPackageName();
            for (ApplicationInfo packageInfo : packages) {
                if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
                if (packageInfo.packageName.equals(myPackage)) continue;
                mActivityManager.killBackgroundProcesses(packageInfo.packageName);
                //                pm.setApplicationEnabledSetting(packageInfo.packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 0);
                //                pm.setApplicationEnabledSetting(packageInfo.packageName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
            }

            //Toast.makeText(this, "DIE", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        theThread.interrupt();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        theThread = new Thread(new SecondThread(startId));
        theThread.start();
        return START_STICKY;

    }
}
