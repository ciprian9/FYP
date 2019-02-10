package com.example.nicholasanton.myapplication;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class Playlist_View extends AppCompatActivity {

    private ArrayList<String> list;
    DataHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist__view);
        list = new ArrayList<>();
        //generate list
        SetItems();
        db = new DataHandler(this);

        //code from
        //https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android
        //instantiate custom adapter
        My_Delete_Playlist_Adapter adapter = new My_Delete_Playlist_Adapter(list, this);

        //handle listview and assign adapter
        ListView lView = findViewById(R.id.delete_list);
        lView.setAdapter(adapter);
    }

    private void SetItems(){
        //Retrieve all audio files in the database for policy with ID 1
        list.clear();
        DataHandler db = new DataHandler(this);
        Cursor c = db.SelectPlaylistQuery(1);
        if (c.moveToFirst()){
            for(int i=0; i<c.getCount(); i++){
                String name = c.getString(Constants.COLUMN_PLAYLIST_NAME);
                list.add(name);
                c.moveToNext();
            }
        }
    }
}
