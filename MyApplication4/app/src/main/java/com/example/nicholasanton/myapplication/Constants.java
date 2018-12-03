package com.example.nicholasanton.myapplication;

/*
If a value is hard to remember or is over used stick it in here
 */

public interface Constants {
    String HEADPHONE_SETTING = "playHeadphones";
    String USAGE_SETTING = "trackUsage";
    String SAVE_RESOURCE_SETTING = "saveResources";
    String TEXT_TO_SPEECH_SETTING = "notificationTTS";
    String AUTO_REPLY_SETTING = "autoReply";
    String BATTERY_LEVEL = "battery";


    int COLUMN_SETTINGS_ID = 0;
    int COLUMN_SETTINGS_NAME = 1;
    int COLUMN_SETTINGS_BATTERY  = 2;
    int COLUMN_SETTINGS_STATUS = 3;
    int COLUMN_SETTINGS_DONE = 4;

    int COLUMN_PLAYLIST_ID = 0;
    int COLUMN_PLAYLIST_NAME = 1;
    int COLUMN_PLAYLIST_POLICYID = 2;
    int COLUMN_PLAYLIST_LOCATION = 3;
}
