package com.example.nicholasanton.myapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    public GoogleApiClient mApiClient;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //request runtime permisson to access Received SMS messages
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        //create the GoogleApiClient and bulid it so that it will detect activities
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        //Listener for the Start Button
        final Button StartBtn = findViewById(R.id.StartBtn);
        StartBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mApiClient.connect();
            }
        });

        //Listener for the stop button
        final Button StopBtn = findViewById(R.id.StopBtn);
        StopBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mApiClient.disconnect();
                StopApp();
            }
        });


        //Listener for the walkingPolicy button
        final Button walkingPolicy =  findViewById(R.id.walkingPolicy);
        walkingPolicy.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openWalkingOptions();
            }
        });
    }

    //Open Walking options activity
    public void openWalkingOptions(){
        Intent intent = new Intent(this, WalkingOptions.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Use a pending intent and a task to start the Intent service ActivityRecognitionService
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        //ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 3000, pendingIntent );
        ActivityRecognitionClient activityRecognitionClient = ActivityRecognition.getClient(this);
        Task task = activityRecognitionClient.requestActivityUpdates(3000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Check if permission was granted for SMS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }

    public void StopApp(){
        stopService(new Intent(this, WalkingPolicy.class));
    }
}