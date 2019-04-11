package com.example.ciprian.myapplication.Classes;

/**
 * Used to get the weather details using the provided url that will return the string in json
 * */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetURLWeather {

    public static String excuteGet(String targetURL)
    {
        URL url;
        //Create HTTPUrlConnection Object
        HttpURLConnection connection = null;
        try {
            //Adds the url to a url object
            url = new URL(targetURL);
            //sets the constraints of what the url can return
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("content-type", "application/json;  charset=utf-8");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(false);

            InputStream is;
            int status = connection.getResponseCode();
            //If the response is not okay then will add the stream to the InputStream
            if (status != HttpURLConnection.HTTP_OK)
                is = connection.getErrorStream();
            else
                is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            //add the lines received from the url to a StringBuilder object
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            //return the json
            return response.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}