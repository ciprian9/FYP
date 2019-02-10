package com.example.nicholasanton.myapplication;

import android.Manifest;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.os.Build;
import android.database.Cursor;
import android.provider.ContactsContract;
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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
    private TextView Username;
    private TextView Password;
    private CheckBox chkRemember;
    private String uName;
    private String Pass;
    private boolean LoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        Username = findViewById(R.id.Username);
        Password = findViewById(R.id.Password);
        chkRemember = findViewById(R.id.saveLoginCheckBox);

        DataHandler db = new DataHandler(this);
        Cursor cursor = db.SelectUser();
        int temp = cursor.getCount();
        cursor.moveToFirst();
        if(temp > 0){
            try {
                Username.setText(cursor.getString(Constants.COLUMN_USERNAME));
                Password.setText(cursor.getString(Constants.COLUMN_PASSWORD));
                chkRemember.setChecked(true);
            }catch (Exception e){
                System.out.print(e);
            }
        }

        //Listener for the Register Button
        final Button RegisterBtn = findViewById(R.id.RegisterButton);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OpenRegisterScreen();
            }
        });

        final Button LoginBtn = findViewById(R.id.NextButton);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogUserIn();
            }
        });
    }

        public void OpenRegisterScreen(){
            Intent i = new Intent(this, Register.class);
            startActivity(i);
        }

    public void GoToMenu(int accountid){
        DataHandler db = new DataHandler(this);
        if (chkRemember.isChecked()){
            if (db.SelectUser().getCount() == 0) {
                db.insertUser(uName, Pass);
                Cursor c = db.SelectUser();
                int temp = c.getCount();
            }else{
                Cursor cursor = db.SelectUser();
                cursor.moveToFirst();
                if(cursor.getString(Constants.COLUMN_USERNAME) != uName){
                    db.updateUser(uName, Pass);
                }
            }
        }else{
            db.DeleteUser();
        }
        Intent i = new Intent(this, ActivitesListeners.class);
        i.putExtra("accountid", accountid);
        try {
            startActivity(i);
        }catch (Exception e){
            System.out.print(e);
        }
    }

        public void LogUserIn(){
            uName    = Username.getText().toString();
            Pass     = Password.getText().toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(!jsonObject.getBoolean("error")){
                                    LoggedIn = true;
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    GoToMenu(jsonObject.getInt("id"));
                                }else{
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                }

                            }catch(JSONException e){
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", uName);
                    params.put("password", Pass);
                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
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
}