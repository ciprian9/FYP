package com.example.nicholasanton.myapplication;/*
Need to implemeant a dataHandler that will create a SQLite database
This will be used to store settings and any other data we may require
functions below may require more or less parameters and different return types this is just a mock up
 */

public class DataHandler {

    public DataHandler(){
        //This is the constructor need to figure out what values are needed here
    }

    public void CreateTable(String TableName){
        //Allow the creation of a new table easily, the code should be easily adapted to create any table, most likeyly will need to pass an array of sorts to specify the columns
        //Need to research if this function is possible
    }

    public void SelectQuery(String Query){
        //This will most likely need to be thought out since we don't knwo what we will return, could be anything
        //Posibly pass in the query
    }

    public void Update(){
        //Need to be able to update existing data such as settings, this will probably take a query aswell but can't be sure until research
    }

    public void Delete(){
        //Need it just in case, we shouldnt have to delete anything but for example if using the music on the device the user might delete a song so we cannot play it in that case delete it from the playlist
    }

    public void Insert(){
        //May be used to add some new music or other fields to the DB
    }
}
