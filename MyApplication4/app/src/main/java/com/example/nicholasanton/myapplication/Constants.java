package com.example.nicholasanton.myapplication;

/*
If a value is hard to remember or is over used stick it in here
Values that are used in different parts of the code will be added here
 */

public interface Constants {
    String HEADPHONE_SETTING = "playHeadphones";
    String USAGE_SETTING = "trackUsage";
    String SAVE_RESOURCE_SETTING = "saveResources";
    String TEXT_TO_SPEECH_SETTING = "notificationTTS";
    String AUTO_REPLY_SETTING = "autoReply";
    String CALL_REPLY_SETTING = "callReply";
    String BATTERY_LEVEL = "battery";
    //String PEDOMETER_SETTING = "startPedometer";

    int COLUMN_SETTINGS_ID = 0;
    int COLUMN_SETTINGS_NAME = 1;
    int COLUMN_SETTINGS_BATTERY  = 2;
    int COLUMN_SETTINGS_STATUS = 3;
    int COLUMN_SETTINGS_DONE = 4;

    int COLUMN_PLAYLIST_ID = 0;
    int COLUMN_PLAYLIST_NAME = 1;
    int COLUMN_PLAYLIST_POLICYID = 2;
    int COLUMN_PLAYLIST_LOCATION = 3;

    int MAINMENU = 1;
    int OPTIONS = 0;

    int COLUMN_USER_ID = 0;
    int COLUMN_USERNAME = 1;
    int COLUMN_PASSWORD = 2;

    int WALKING_POLICY = 1;
    int RUNNING_POLICY = 2;

    String PARAM_NAME = "name";
    String POLICY_ID = "policyid";
    String MUSIC_INTENT = "music";
    String ACCOUNTID_INTENT = "accountid";
    String WHERE_INTENT = "where";
    String PRESSED_INTENT = "pressed";
    String PEDOMETER_INTENT = "pedometer";
    String TIME_INTENT ="time";
    String DISTANCE_INTENT= "distance";

    String MUSIC_SETTING = "MusicPlayer";
    String PEDOMETER_SETTING = "pedometer";
    String TIME_SETTING = "Time";
    String DISTANCE_SETTING = "Distance_Speed";
    String RECORD_ROUTE = "RecordRoute";

    String DB_FLAG = "status";

    String ROOT_URL = "http://192.168.0.241/Android/v1/";
    String URL_REGISTER = ROOT_URL+"registerUser.php";
    String URL_LOGIN = ROOT_URL+"userLogin.php";
    String URL_SAVE_SETTING = ROOT_URL+"saveSetting.php";
    String URL_UPDATE_SETTING = ROOT_URL+"updateSetting.php";
    String URL_READ_SETTING = ROOT_URL+"readSetting.php";
}
