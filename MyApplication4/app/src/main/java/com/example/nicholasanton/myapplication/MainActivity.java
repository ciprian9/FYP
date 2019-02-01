package com.example.nicholasanton.myapplication;

import android.Manifest;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
//    public GoogleApiClient mApiClient;
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.KILL_BACKGROUND_PROCESSES},
                1);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
//
//        NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        if(!n.isNotificationPolicyAccessGranted()) {
//            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//            startActivity(intent);
//        }
//
        //request runtime permisson to access Received SMS messages
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
//
//        //create the GoogleApiClient and bulid it so that it will detect activities
////        mApiClient = new GoogleApiClient.Builder(this)
////                .addApi(ActivityRecognition.API)
////                .addConnectionCallbacks(this)
////                .addOnConnectionFailedListener(this)
////                .build();
//
//
//        //Listener for the Start Button
////////        final Button StartBtn = findViewById(R.id.StartBtn);
////////        StartBtn.setOnClickListener(new View.OnClickListener(){
////////            public void onClick(View v){
////////                mApiClient.connect();
////////            }
////////        });

        //Listener for the Register Button
        final Button RegisterBtn = findViewById(R.id.RegisterButton);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OpenRegisterScreen();
            }
        });
//
////        //Listener for the stop button
////        final Button StopBtn = findViewById(R.id.StopBtn);
////        StopBtn.setOnClickListener(new View.OnClickListener(){
////            public void onClick(View v){
////                mApiClient.disconnect();
////                StopApp();
////            }
////        });
//
//
//        //Listener for the walkingPolicy button
////        final Button walkingPolicy =  findViewById(R.id.walkingPolicy);
////        walkingPolicy.setOnClickListener(new View.OnClickListener(){
////            public void onClick(View v){
////                openWalkingOptions();
////            }
////        });
////    }
    }
        public void OpenRegisterScreen(){
            Intent i = new Intent(this, Register.class);
            startActivity(i);
        }
//    //Open Walking options activity
////    public void openWalkingOptions(){
////        Intent intent = new Intent(this, WalkingOptions.class);
////        startActivity(intent);
////    }

//
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Use a pending intent and a task to start the Intent service ActivityRecognitionService
        //Intent intent = new Intent( this, ActivityRecognizedService.class );
        //PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        //ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 3000, pendingIntent );
        //ActivityRecognitionClient activityRecognitionClient = ActivityRecognition.getClient(this);
        //Task task = activityRecognitionClient.requestActivityUpdates(3000, pendingIntent);
    }

//
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
//
//    public void StopApp(){
//        stopService(new Intent(this, WalkingPolicy.class));
//        MusicPlayerService.mediaPlayer.stop();
//    }
}