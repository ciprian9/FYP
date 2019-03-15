package com.example.nicholasanton.myapplication.Classes;

//COPIED FROM http://www.gadgetsaint.com/android/create-pedometer-step-counter-android/#.XG69Cej7S71

public class SensorFilter {

    private SensorFilter() {
    }

    static float sum(float[] array) {
        float retval = 0;
        for (float anArray : array) {
            retval += anArray;
        }
        return retval;
    }

    public static float[] cross(float[] arrayA, float[] arrayB) {
        float[] retArray = new float[3];
        retArray[0] = arrayA[1] * arrayB[2] - arrayA[2] * arrayB[1];
        retArray[1] = arrayA[2] * arrayB[0] - arrayA[0] * arrayB[2];
        retArray[2] = arrayA[0] * arrayB[1] - arrayA[1] * arrayB[0];
        return retArray;
    }

    static float norm(float[] array) {
        float retval = 0;
        for (float anArray : array) {
            retval += anArray * anArray;
        }
        return (float) Math.sqrt(retval);
    }


    static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static float[] normalize(float[] a) {
        float[] retval = new float[a.length];
        float norm = norm(a);
        for (int i = 0; i < a.length; i++) {
            retval[i] = a[i] / norm;
        }
        return retval;
    }

}