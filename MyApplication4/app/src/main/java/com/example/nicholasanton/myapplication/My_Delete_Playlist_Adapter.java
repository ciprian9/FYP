package com.example.nicholasanton.myapplication;

/*
Code coming from
https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android
Creates a Custom Adapter to allow buttons and textviews to be displayed on a listview component
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class My_Delete_Playlist_Adapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private DataHandler db;
    private String selectedItem;


    public My_Delete_Playlist_Adapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
        db = new DataHandler(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return list.indexOf(list.get(pos));
        //just return 0 if your list items do not have an Id variable.
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.delete_list_row, null);
        }



        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                selectedItem = (String) getItem(position);
                list.remove(position); //or some other task
                db.DeletePlaylists(selectedItem);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}

