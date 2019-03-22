package com.example.nicholasanton.myapplication.Views;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ChangePassword extends AppCompatActivity {

    private String password;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private String username;
    private EditText oldPswd;
    private EditText newPswd;
    private EditText cofPswd;
    private boolean Disable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPswd = findViewById(R.id.OldPassword);
        newPswd = findViewById(R.id.Password);
        cofPswd = findViewById(R.id.ConfirmPassword);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        Disable = bundle.getBoolean("disable");

        if (Disable){
            oldPswd.setEnabled(false);
        } else {
            oldPswd.setEnabled(true);
        }

        Button btnChange = findViewById(R.id.ChangePwd);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldPassword = oldPswd.getText().toString();
                newPassword = newPswd.getText().toString();
                confirmPassword = cofPswd.getText().toString();
                ChangePasswordConnection(ROOT_URL+ "checkPassword.php", 0);
            }
        });

    }

    private void ChangePasswordConnection(final String apath, final int anAction){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                apath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (anAction){
                                case 0:
                                    String user = jsonObject.getString("username");
                                    if (user.equals(username)){
                                        if (newPassword.equals(confirmPassword)){
                                            ChangePasswordConnection(ROOT_URL+"changePassword.php", 1);
                                        }
                                    }
                                    break;
                                case 1:
                                    Toast.makeText(ChangePassword.this, "Finished Changing", Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;
                            }
                        }catch(JSONException e){
                            Log.e("TEST : ", e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e("TEST : ", error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                switch (anAction) {
                    case 0:
                        params.put("username", username);
                        break;
                    case 1:
                        params.put("username", username);
                        params.put("newPassword", confirmPassword);
                        break;
                }
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
