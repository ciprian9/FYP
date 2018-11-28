package com.example.nicholasanton.myapplication;

/*
    This class needs to be used to set the options for the walking policy. If the application is running and the current activity is walking the policy will come into action, this page will decide what the policy will do
    the following boolean values will be used to store the value of the options. This together with the playlist button will be used to config this policy
    This class needs to implemenat a datahandler class that will write each of these settings to the database, the values will then be read and set again when the user will reopen the page
    functions needed are VarsToForm and FormToVars
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WalkingOptions extends AppCompatActivity {

    private Boolean PlayMusicOpion;
    private Boolean TrackUsageOption;
    private Boolean SaveResourcesOption;
    private Boolean TextToSpeechOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_options);
    }

    public void VarsToForm(){
        //Read the database values and update the activity to reflect those values
    }

    public void FormToVars(){
        //Read the variable values and write them to the database
    }
}
