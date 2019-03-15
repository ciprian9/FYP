package com.example.nicholasanton.myapplication.Views;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nicholasanton.myapplication.Interfaces.Constants;
import com.example.nicholasanton.myapplication.R;
import com.example.nicholasanton.myapplication.Classes.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    private String uName;
    private String Pass;
    private String Email;
    private TextView Username;
    private TextView E_mail;
    private TextView Password;
    private TextView ConfPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Username = findViewById(R.id.Username);
        E_mail = findViewById(R.id.email);
        Password = findViewById(R.id.Password);
        ConfPassword = findViewById(R.id.ConfirmPassword);

        final Button CancelBtn = findViewById(R.id.CancelBtn);
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        final Button RegisterBtn = findViewById(R.id.RegisterButton);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GatherData();
            }
        });
    }

    public void GatherData(){
        uName    = Username.getText().toString();
        Pass     = Password.getText().toString();
        String confPass = ConfPassword.getText().toString();
        Email    = E_mail.getText().toString();

        boolean CheckPassword = verifyPassword(Pass, confPass);
        if (!uName.equals("")) {
            if (CheckPassword) {
                if (verifyEmailFormat(Email)) {
                    registerUser();
                } else {
                    Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Password does not match", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(this, "Need to add a username", Toast.LENGTH_LONG).show();
        }
    }

    public void registerUser(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
            params.put("email", Email);
            return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private boolean verifyPassword(String Pass, String ConfPass){
        if (Pass.equals("")){
            return false;
        }

        if (ConfPass.equals("")){
            return false;
        }

        return Pass.equals(ConfPass);
    }

    private boolean verifyEmailFormat(String Email){
        return Email.contains("@") && Email.contains(".");
    }
}
