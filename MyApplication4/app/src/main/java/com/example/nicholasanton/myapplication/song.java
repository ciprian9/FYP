package com.example.nicholasanton.myapplication;

//USED FOR MUSIC PLAYER: https://www.youtube.com/watch?v=p2ffzsCqrs8

public class song {
    private long id;
    private String title;
    private String artist;

    public song(long songID, String songTitle, String songArtist){
        id = songID;
        title = songTitle;
        artist = songArtist;
    }

    public long getID(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }
}
