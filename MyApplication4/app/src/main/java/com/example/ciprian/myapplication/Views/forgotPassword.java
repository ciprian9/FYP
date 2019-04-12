package com.example.ciprian.myapplication.Views;

/**
 * Activity will let user change password if they forgot it based on a secret q&a
 * */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ciprian.myapplication.Classes.RequestHandler;
import com.example.ciprian.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.ciprian.myapplication.Interfaces.Constants.ROOT_URL;

public class forgotPassword extends AppCompatActivity {

    private EditText username;
    private EditText secretAnswer;
    private Spinner spinner;
    private String name;
    private String secAns;
    private String secQue;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username = findViewById(R.id.userName);
        secretAnswer = findViewById(R.id.secretAnswer);
        spinner = findViewById(R.id.spSecretQuestion);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.question_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Button CancelBtn = findViewById(R.id.CancelBtn);
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        final Button RegisterBtn = findViewById(R.id.RegisterButton);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = username.getText().toString();
                secAns = secretAnswer.getText().toString();
                secQue = String.valueOf(spinner.getSelectedItemId());
                getPassword();
            }
        });
    }

    //gets the password from the database if the answer is right
    private void getPassword() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ROOT_URL+"forgotPassword.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                                try {
                                    password = jsonObject.getString("password");
                                }catch (Exception e){
                                    password = "";
                                }
                            if (password.length() > 1){
                                Intent i = new Intent(forgotPassword.this, ChangePassword.class);
                                i.putExtra("username", name);
                                i.putExtra("disable", true);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Wrong Credentials. Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }catch(JSONException e){
                            Log.e("forgotpw : ", e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("forgotpw : ", error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", name);
                params.put("secretQues", secQue);
                params.put("secretAns", secAns);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
