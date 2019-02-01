package com.example.nicholasanton.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private String uName;
    private String Pass;
    private String Email;
    private String ConfPass;
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
        ConfPass = ConfPassword.getText().toString();
        Email    = E_mail.getText().toString();

        boolean CheckPassword = verifyPassword(Pass, ConfPass);
        if (!uName.equals("")) {
            if (CheckPassword) {
                if (verifyEmailFormat(Email)) {
                    //allow register
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

    private boolean verifyPassword(String Pass, String ConfPass){
        if (Pass.equals("")){
            return false;
        }

        if (ConfPass.equals("")){
            return false;
        }

        if (Pass.equals(ConfPass)){
            return true;
        } else{
            return false;
        }
    }

    private boolean verifyEmailFormat(String Email){
        if(Email.contains("@") && Email.contains(".")){
            return true;
        } else{
            return false;
        }
    }
}
