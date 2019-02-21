package com.android.internal.telephony;

//https://github.com/Hitman666/AndroidCallBlockingTestDemo

public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}