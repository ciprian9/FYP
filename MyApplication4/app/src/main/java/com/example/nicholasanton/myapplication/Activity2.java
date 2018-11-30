package com.example.nicholasanton.myapplication;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Activity2 extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    private ArrayList<String> arrayList;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private Uri songUri;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        if(ContextCompat.checkSelfPermission(Activity2.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Activity2.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(Activity2.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(Activity2.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            doStuff();
        }
    }

    public void doStuff(){
        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if (mediaPlayer == null){
                    mediaPlayer = new MediaPlayer();
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                String media_path = arrayList.get(position);
                String[] parts = media_path.split(":");
                media_path = parts[parts.length-1];
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Uri uri = Uri.parse(media_path);
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);
                arrayList.add("Title: " + currentTitle + "\nArtist: " + currentArtist + "\nLocation:" + currentLocation);
            } while (songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ActivityCompat.requestPermissions(Activity2.this, permissions, requestCode);
        switch (requestCode){
            case MY_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(Activity2.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();

                        doStuff();
                    }
                } else {
                    //ActivityCompat.requestPermissions(Activity2.this, permissions, requestCode);
                    Toast.makeText(this, "No Permission Granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}
