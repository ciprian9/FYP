package com.example.nicholasanton.myapplication;

import java.util.ArrayList;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.view.View;

public class PlayLists extends Activity {
    private ArrayList<String> arrayList;
    ArrayList<String> selectedItems;
    ArrayList<String> MusicLocation;
    ArrayList<String> locationToSave;
    ArrayList<String> selectedItemsShow;
    ArrayList<String> locationToSaveShow;
    private DataHandler db;

    protected void onCreate(Bundle savedInstanceState) {
        /* TODO Auto-generated method stub */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_lists);
        //create an ArrayList object to store selected items
        selectedItems= new ArrayList<>();
        arrayList=new ArrayList<>();
        MusicLocation = new ArrayList<>();
        locationToSave = new ArrayList<>();
        selectedItemsShow = new ArrayList<>();
        locationToSaveShow = new ArrayList<>();
        db = new DataHandler(this);


    }

    public void onStart(){
        super.onStart();
        //create an instance of ListView
        ListView chl = findViewById(R.id.checkable_list);
        //set multiple selection mode
        chl.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getMusic();
        //supply data itmes to ListView
        ArrayAdapter<String> aa=new ArrayAdapter<>(this,R.layout.checkable_list_layout,R.id.txt_title,arrayList);
        chl.setAdapter(aa);
        //set OnItemClickListener
        chl.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String selectedItem = ((TextView) view).getText().toString();
                if(selectedItems.contains(selectedItem)) {
                    selectedItems.remove(selectedItem); //remove deselected item from the list of selected items
                    locationToSave.remove(MusicLocation.get(position));
                }
                else{
                    selectedItems.add(selectedItem); //add selected item to the list of selected items
                    locationToSave.add(MusicLocation.get(position));
                }

            }

        });
    }

    public void saveSelectedItems(View view){
        String selItems="";
        int i = 0;
        db.DeletePlaylists();
        selectedItemsShow.clear();
        locationToSaveShow.clear();
        for(String item:locationToSave){
            db.insertPlaylistData(selectedItems.get(i), 1, item);
            selItems = selItems + selectedItems.get(i) + " : " + item;
            i++;
        }
    }


    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentLocation = songCursor.getString(songLocation);
                arrayList.add(currentTitle);
                MusicLocation.add(currentLocation);
            } while (songCursor.moveToNext());
        }
        assert songCursor != null;
            songCursor.close();
    }

    public void getSelected(){
        Cursor c = db.SelectPlaylistQuery(1);
        c.moveToFirst();
        for (int i=0; i< c.getCount(); i++ ) {
            String loc = c.getString(3);
            String name = c.getString(1);
            selectedItemsShow.add(name);
            locationToSaveShow.add(loc);
        }
    }

    public void showDbItems(View v){
        getSelected();
        String str="";
        for (int i = 0; i < selectedItems.size(); i++){
            str = str + selectedItems.get(i) + " : " + locationToSave.get(i) + "\n";
        }

        Toast.makeText(this, str, Toast.LENGTH_LONG).show();

    }
}