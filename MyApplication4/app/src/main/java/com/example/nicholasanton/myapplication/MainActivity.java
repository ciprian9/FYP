package com.example.nicholasanton.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private TextView Username;
    private TextView Password;
    private CheckBox chkRemember;
    private String uName;
    private String Pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // No explanation needed; request the permission
                        int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // No explanation needed; request the permission
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {
                ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // No explanation needed; request the permission
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        11);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // No explanation needed; request the permission
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        12);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // No explanation needed; request the permission
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        13);
            }
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
                System.out.print(e.getMessage());
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
            }else{
                Cursor cursor = db.SelectUser();
                cursor.moveToFirst();
                if(!cursor.getString(Constants.COLUMN_USERNAME).equals(uName)){
                    db.updateUser(uName, Pass);
                }
            }
        }else{
            db.DeleteUser();
        }
        Intent i = new Intent(this, ActivitesListeners.class);
        i.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        try {
            startActivity(i);
        }catch (Exception e){
            System.out.print(e.getMessage());
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
                protected Map<String, String> getParams() {
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