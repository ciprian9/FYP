package com.example.ciprian.myapplication.Classes;

/**
 * Used Code From : http://www.gadgetsaint.com/android/create-pedometer-step-counter-android/#.XG69Cej7S71
 * Class with an algorithm to filter out values that has close approximation to steps
 * */

class SensorFilter {

    private SensorFilter() {
    }

    //Get the sum of all the numbers in the array
    static float sum(float[] array) {
        float retval = 0;
        for (float anArray : array) {
            retval += anArray;
        }
        return retval;
    }

    //Returns the normalization factor by getting the square root of a squared number
    static float norm(float[] array) {
        float retval = 0;
        for (float anArray : array) {
            retval += anArray * anArray;
        }
        return (float) Math.sqrt(retval);
    }

    //Returns the Z Axis by multiplying the World Z and the current acceleration
    static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }
}
