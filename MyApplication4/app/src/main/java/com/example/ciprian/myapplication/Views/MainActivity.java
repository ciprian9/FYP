package com.example.ciprian.myapplication.Views;

/**
 * Starting activity that lets the user login
 * */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import com.example.ciprian.myapplication.Interfaces.Constants;
import com.example.ciprian.myapplication.DataHandler;
import com.example.ciprian.myapplication.R;
import com.example.ciprian.myapplication.Classes.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView Username;
    private TextView Password;
    private CheckBox chkRemember;
    private String uName;
    private String Pass;
    private String gmail;
    private String email;
    private static final String testUname = "local";
    private static final String testPass = "local";
    private static final int testID = 10;
    private DataHandler db;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting all permissions at once
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.WRITE_CALENDAR,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_CONTACTS
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
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

        final Button PasswordBtn = findViewById(R.id.btnForgotPassword);
        PasswordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db.insertLog("Clicked Forgot Password");
                forgotPassword();
            }
        });
    }

    //Starts the forgotPassword Activity
    private void forgotPassword() {
        Intent i = new Intent(this, forgotPassword.class);
        startActivity(i);
    }

    //Lets the user register
    private void OpenRegisterScreen(){
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

    //checks if the user has permissions
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //Opens activitieslisteners that will then start to listen for activity changes
    private void GoToMenu(int accountid){
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

    //logs user in using database values that are accessed using PHP scripts
    private void LogUserIn(){
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            Log.i("MainActivity", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }
}