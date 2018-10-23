package com.example.ciprian.trackusage;

import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    //public String OldConType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isInternetConnected();
    }


    public void isInternetConnected() {
        //Create Connectivity Manager and Network types
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        DeviceConnection devCon = new DeviceConnection();
        devCon.WhichConnection(connManager);
        final TextView mTextView = findViewById(R.id.textView);
        mTextView.setText(devCon.getConType());
        dbConnection dbcon = new dbConnection();
        dbcon.execute(devCon.getConType());
    }
}
