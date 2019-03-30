package com.example.nicholasanton.myapplication.Services;

/**
 * Will start at the morning time and give the user a notification with the weather of the day
 * */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nicholasanton.myapplication.Classes.Function;
import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class getTheWeather extends Service {

    private DataHandler db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new DataHandler(this);
        db.insertLog("Start Morning Routine");
        turnOffDoNotDisturb();
        String city = "Dublin, IE";
        taskLoadUp(city);
        return super.onStartCommand(intent, flags, startId);
    }

    private void turnOffDoNotDisturb() {
        db.insertLog("SILENT OFF");
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    private void taskLoadUp(String query) {
        getTheWeather.DownloadWeather task = new getTheWeather.DownloadWeather();
        task.execute(query);
    }

    //Send notification with the weather
    private void sendNotification(String Temp) {
        db.insertLog("Build Notification");

        String id = "1";
        CharSequence name = "my Channel";
        String description = "Description";
        int importance = NotificationManager.IMPORTANCE_LOW;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name,importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "111")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Good Morning!")
                        .setContentText("Todays Temperature is : " + Temp)
                        .setChannelId(id);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    //Gets the weather using a url
    class DownloadWeather extends AsyncTask< String, Void, String > {
        protected String doInBackground(String...args) {
            String OPEN_WEATHER_MAP_API = "cbfdb21fa1793c10b14b6b6d00fbef03";
            return Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
        }
        @Override
        protected void onPostExecute(String xml) {
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            try {
                JSONObject json = new JSONObject(xml);
                JSONObject main = json.getJSONObject("main");
                String temp = (String.format("%.2f", main.getDouble("temp")) + "°");
                sendNotification(temp);
            } catch (JSONException e) {
                Log.d("TEST : ", e.toString());
            }
        }
    }
}
