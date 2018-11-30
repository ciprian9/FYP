package com.example.nicholasanton.trackin;

import com.google.android.gms.location.DetectedActivity;

public class Policy {
    private String ActivityName;
    private int ActivityID;
    private int PolicyLevel;

    public Policy(String AActivityName, int AActivityID){
        ActivityName = AActivityName;
        ActivityID = AActivityID;
    }

    public String ChoosePolicy() {
        switch (ActivityID) {
            case 0:  return ActivityName;
            case DetectedActivity.ON_BICYCLE:  return ActivityName;
            case 2:  return ActivityName;
            case 3:  return ActivityName;
            case 4:  return ActivityName;
            case 5:  return ActivityName;
            case 6:  return ActivityName;
            default: return ActivityName;
        }
    }
}
