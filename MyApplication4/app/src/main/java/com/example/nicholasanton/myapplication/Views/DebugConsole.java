package com.example.nicholasanton.myapplication.Views;

/**
 * Activity that shows everything that has been happening while the app has been running
 * */

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.R;


public class DebugConsole extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_console);

        TextView textView = findViewById(R.id.text_view);
        textView.setMovementMethod(new ScrollingMovementMethod());

        DataHandler db = new DataHandler(this);

        Cursor cursor = db.SelectLogs();

        //Wont let there be more than 500 lines and it more will show the newest lines
        String result = "";
        if (cursor != null){
            cursor.moveToLast();
            if (cursor.getCount()>500) {
                for (int i=0;i<500;i++){
                    result = result + cursor.getString(1);
                    cursor.moveToPrevious();
                }
                textView.setText(result);
            } else {
                for (int i = 0; i <= cursor.getCount() - 1; i++) {
                    result = result + cursor.getString(1);
                    cursor.moveToPrevious();
                }
                textView.setText(result);
            }
        }

    }
}
