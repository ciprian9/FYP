package com.example.ciprian.myapplication;

/**
 * Interface that has procedures for calls
 * */

public interface ITelephony {
    //function for ending calls
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}