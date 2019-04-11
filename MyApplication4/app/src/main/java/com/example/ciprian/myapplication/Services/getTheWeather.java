package com.example.ciprian.myapplication.Services;

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

import com.example.ciprian.myapplication.Classes.GetURLWeather;
import com.example.ciprian.myapplication.DataHandler;
import com.example.ciprian.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class getTheWeather extends Service {

    private DataHandler db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Will turn off do not disturb and then start getting the weather
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new DataHandler(this);
        db.insertLog("Start Morning Routine");
        turnOffDoNotDisturb();
        String city = "Dublin, IE";
        taskLoadUp(city);
        return super.onStartCommand(intent, flags, startId);
    }

    //Will access the audio manager of the device and then turn the ringer off
    private void turnOffDoNotDisturb() {
        db.insertLog("SILENT OFF");
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    //Will start to get the weather by executing the function to retrieve json
    private void taskLoadUp(String query) {
        getTheWeather.DownloadWeather task = new getTheWeather.DownloadWeather();
        task.execute(query);
    }

    //Send notification with the weather
    private void sendNotification(String Temp) {
        db.insertLog("Build Notification");

        //Will set all the settings including settings and vibration settings for the notification
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

        //Adds the data to the notification such as the icon, title and the actual weather
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "111")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Good Morning!")
                        .setContentText("Todays Temperature is : " + Temp)
                        .setChannelId(id);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    //Creates the class that will generate the url using the api key and the city that the user is in
    class DownloadWeather extends AsyncTask< String, Void, String > {
        protected String doInBackground(String...args) {
            String OPEN_WEATHER_MAP_API = "cbfdb21fa1793c10b14b6b6d00fbef03";
            return GetURLWeather.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
        }
        @Override
        protected void onPostExecute(String xml) {
            //Sets do not disturb on
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            //Add the JSON to a JSONObjext and then extracts the weather and sends it to the function to create a notification
            try {
                JSONObject json = new JSONObject(xml);
                JSONObject main = json.getJSONObject("main");
                String temp = (String.format("%.2f", main.getDouble("temp")) + "°");
                sendNotification(temp);
            } catch (JSONException e) {
                //If any errors occur then it will log the error
                Log.d("TEST : ", e.toString());
            }
        }
    }
}
