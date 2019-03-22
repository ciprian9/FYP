package com.example.nicholasanton.myapplication.Views;

import android.content.Intent;
import android.net.Uri;
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
import com.example.nicholasanton.myapplication.Classes.RequestHandler;
import com.example.nicholasanton.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.nicholasanton.myapplication.Interfaces.Constants.GOOGLEROOTURL;
import static com.example.nicholasanton.myapplication.Interfaces.Constants.ROOT_URL;

public class forgotPassword extends AppCompatActivity {

    private EditText username;
    private EditText secretAnswer;
    private Spinner spinner;
    private String name;
    private String secAns;
    private String secQue;

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

    private void getPassword() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ROOT_URL+"forgotPassword.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String password = jsonObject.getString("password");
                            if (password.length() > 1){
                                Intent i = new Intent(forgotPassword.this, ChangePassword.class);
                                i.putExtra("username", name);
                                i.putExtra("disable", true);
                                startActivity(i);
                                finish();
                            }
                        }catch(JSONException e){
                            Log.e("TEST : ", e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TEST : ", error.getMessage());
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
