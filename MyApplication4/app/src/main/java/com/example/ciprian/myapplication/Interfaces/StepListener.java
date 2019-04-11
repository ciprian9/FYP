package com.example.ciprian.myapplication.Interfaces;

/**
 * Used code from : http://www.gadgetsaint.com/android/create-pedometer-step-counter-android/#.XG65DOj7S70
 * Will listen to step alerts
 * */

public interface StepListener {

    void step(long timeNs);

}