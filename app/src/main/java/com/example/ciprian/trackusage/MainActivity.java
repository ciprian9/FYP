package com.example.ciprian.trackusage;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public String ConType;
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
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if ((activeNetwork != null) && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
            final TextView mTextView = findViewById(R.id.textView);
            mTextView.setText(getString(R.string.WIFI));
            ConType = "Connected to WIFI";
        } else if ((activeNetwork != null) && (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)) {
            final TextView mTextView = findViewById(R.id.textView);
            mTextView.setText(getString(R.string.DataMobile));
            ConType = "Connected to data";
        } else {
            final TextView mTextView = findViewById(R.id.textView);
            mTextView.setText(getString(R.string.NotConnected));
            ConType = "Not Connected";
        }

//        if (ConType != OldConType)
//        {
//            //UpdateDatabase;
//        }
    }

}
