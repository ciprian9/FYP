package com.example.nicholasanton.myapplication.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nicholasanton.myapplication.Classes.Function;
import com.example.nicholasanton.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class getTheWeather extends Service {
    String city = "Dublin, IE";
    String OPEN_WEATHER_MAP_API = "cbfdb21fa1793c10b14b6b6d00fbef03";
    String Temp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskLoadUp(city);
        return super.onStartCommand(intent, flags, startId);
    }

    public void taskLoadUp(String query) {
        getTheWeather.DownloadWeather task = new getTheWeather.DownloadWeather();
        task.execute(query);
    }

    public void sendNotification(String Temp) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "111")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Good Morning!")
                        .setContentText("Todays Temperature is : " + Temp);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject main = json.getJSONObject("main");
                    Temp = (String.format("%.2f", main.getDouble("temp")) + "°");
                    sendNotification(Temp);
                }
            } catch (JSONException e) {
                Log.d("HELP123", e.toString());
            }
        }
    }
}
