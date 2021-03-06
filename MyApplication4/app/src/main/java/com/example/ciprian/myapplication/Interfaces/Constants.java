package com.example.ciprian.myapplication.Interfaces;

/**
 * If a value is hard to remember or is over used stick it in here
 * Values that are used in different parts of the code will be added here
 * */

public interface Constants {
    String TEXT_TO_SPEECH_SETTING = "notificationTTS";
    String AUTO_REPLY_SETTING = "autoReply";
    String CALL_REPLY_SETTING = "callReply";
    String CALL_REPLY_MESSAGE = "Sorry, I'm a bit busy.";
    String MEETING_MESSAGE = "Sorry, I'm at work right now, I'll call you later.";

    int COLUMN_USERNAME = 1;
    int COLUMN_PASSWORD = 2;

    int WALKING_POLICY = 1;
    int RUNNING_POLICY = 2;
    int CYCLING_POLICY = 3;
    int DRIVING_POLICY = 4;

    //Names of columns in db
    String PARAM_NAME = "name";
    String POLICY_ID = "policyid";
    String MUSIC_INTENT = "music";
    String ACCOUNTID_INTENT = "accountid";
    String USERNAME_INTENT = "username";
    String GMAIL_INTENT = "gmail";
    String PEDOMETER_INTENT = "pedometer";
    String TIME_INTENT ="time";
    String DISTANCE_INTENT= "distance";
    String SPEED_INTENT = "speed";
    String RECOMEND_INTENT ="recomend";
    String USERID_INTENT= "userid";
    String HOME_INTENT= "home";
    String WORK_INTENT= "work";

    String MUSIC_SETTING = "MusicPlayer";
    String PEDOMETER_SETTING = "pedometer";
    String TIME_SETTING = "Time";
    String DISTANCE_SETTING = "Distance_Speed";
    String RECORD_ROUTE = "RecordRoute";

    String DB_FLAG = "status";

    //URLS for the user,setting and location tables
    String ROOT_URL = "http://192.168.0.241/Android/v1/";
    String URL_REGISTER = ROOT_URL+"registerUser.php";
    String URL_LOGIN = ROOT_URL+"userLogin.php";
    String URL_SAVE_SETTING = ROOT_URL+"saveSetting.php";
    String URL_UPDATE_SETTING = ROOT_URL+"updateSetting.php";
    String URL_READ_SETTING = ROOT_URL+"readSetting.php";
    String URL_UPDATE_ACCOUNT = ROOT_URL+"updateAccount.php";
    String URL_ADD_LOCATION = ROOT_URL+"insertLocation.php";
    String URL_ADDWORK_LOCATION = ROOT_URL+"insertLocationWorking.php";
    String URL_READ_LOCATION = ROOT_URL+"readLocation.php";

    //URL for the Google Calendar php files
    String GOOGLEROOTURL = "http://192.168.0.241/Android/php/";

    //Default Night Morning Routines
    String defaultMorning = "8:0";
    String defaultNight = "10:0";
}
