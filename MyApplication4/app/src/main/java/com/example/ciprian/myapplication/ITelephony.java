package com.android.internal.telephony;

/**
 * Interface that has procedures for calls
 * */

public interface ITelephony {
    //function for ending calls
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}