package com.example.nicholasanton.myapplication.Views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.R;
import com.example.nicholasanton.myapplication.Classes.RequestHandler;

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
    private String gmail;
    private static final String testUname = "tester1234";
    private static final String testPass = "tester1234";
    private static final int testID = 10;
    public DataHandler db;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // No explanation needed; request the permission
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // No explanation needed; request the permission
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_CALENDAR)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
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

//        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//        startActivityForResult(intent);

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

        db = new DataHandler(this);
        Cursor cursor = db.SelectUser();
        int temp = cursor.getCount();
        cursor.moveToFirst();
        if(temp > 0){
            try {
                Username.setText(cursor.getString(Constants.COLUMN_USERNAME));
                Password.setText(cursor.getString(Constants.COLUMN_PASSWORD));
                chkRemember.setChecked(true);
            }catch (Exception e){
                db.insertLog("Failed Setting Credentials");
            }
        }

        //Listener for the Register Button
        final Button RegisterBtn = findViewById(R.id.RegisterButton);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db.insertLog("Opening Register Screen");
                OpenRegisterScreen();
            }
        });

        final Button LoginBtn = findViewById(R.id.NextButton);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db.insertLog("Logging In");
                LogUserIn();
            }
        });
    }

        public void OpenRegisterScreen(){
            Intent i = new Intent(this, Register.class);
            startActivity(i);
        }

    public void GoToMenu(int accountid){
        if(accountid != testID) {
            DataHandler db = new DataHandler(this);
            if (chkRemember.isChecked()) {
                if (db.SelectUser().getCount() == 0) {
                    db.insertUser(uName, Pass);
                } else {
                    Cursor cursor = db.SelectUser();
                    cursor.moveToFirst();
                    if (!cursor.getString(Constants.COLUMN_USERNAME).equals(uName)) {
                        db.updateUser(uName, Pass);
                    }
                }
            } else {
                db.DeleteUser();
            }
        }
        Intent i = new Intent(this, ActivitesListeners.class);
        i.putExtra(Constants.ACCOUNTID_INTENT, accountid);
        i.putExtra(Constants.USERNAME_INTENT, uName);
        i.putExtra(Constants.GMAIL_INTENT, gmail);

        try {
            startActivity(i);
        }catch (Exception e){
            db.insertLog("Failed opening ActivitesListeners");
        }
    }

        public void LogUserIn(){
            uName    = Username.getText().toString();
            Pass     = Password.getText().toString();

            if(!uName.equals(testUname) && !Pass.equals(testPass)) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Constants.URL_LOGIN,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (!jsonObject.getBoolean("error")) {
                                        if (!jsonObject.getString("gmail").isEmpty()) {
                                            gmail = jsonObject.getString("gmail");
                                        } else {
                                            gmail = "NULL";
                                        }
                                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                        GoToMenu(jsonObject.getInt("id"));
                                    } else {
                                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                db.insertLog("Error in Logging in script");
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", uName);
                        params.put("password", Pass);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
            }else{
                GoToMenu(testID);
            }
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