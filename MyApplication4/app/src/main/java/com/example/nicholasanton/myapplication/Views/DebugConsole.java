package com.example.nicholasanton.myapplication.Views;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.nicholasanton.myapplication.DataHandler;
import com.example.nicholasanton.myapplication.R;


public class DebugConsole extends AppCompatActivity {

    private DataHandler db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_console);

        TextView textView = findViewById(R.id.text_view);
        textView.setMovementMethod(new ScrollingMovementMethod());

        db = new DataHandler(this);

        cursor = db.SelectLogs();

        if (cursor != null){
            cursor.moveToLast();
            if (cursor.getCount()>500) {
                for (int i=0;i<500;i++){
                    textView.setText(textView.getText() + cursor.getString(1));
                    cursor.moveToPrevious();
                }
            } else {
                for (int i = 0; i <= cursor.getCount() - 1; i++) {
                    textView.setText(textView.getText() + cursor.getString(1));
                    cursor.moveToPrevious();
                }
            }
        }

    }
}
