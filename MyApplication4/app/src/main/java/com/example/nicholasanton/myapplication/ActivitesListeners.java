package com.example.nicholasanton.myapplication;

import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivitesListeners extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final Button walkingPolicy =  findViewById(R.id.walkingPolicy);
        walkingPolicy.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                StartWalkingService();
            }
        });

        final Button walkingOptions =  findViewById(R.id.walkingOptions);
        walkingOptions.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                StartWalkingOptions();
            }
        });
    }

    public void MakeNotifications(String str, String chanelID){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelID);
        builder.setContentText(str);
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setContentTitle( getString( R.string.app_name ) );
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }

    public void StartWalkingService(){
        //create a new intent that will start walkingPolicy service
        MakeNotifications("on Foot", "on Foot");
        Intent intent = new Intent(this, WalkingPolicy.class);
        startService(intent);
    }

    public void StartWalkingOptions(){
        //create a new intent that will start walkingOptions class
        Intent intent = new Intent(this, WalkingOptions.class);
        startActivity(intent);
    }
}
