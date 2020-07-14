package com.example.cst2335_finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;
import java.util.ArrayList;

public class DeezerActivity extends AppCompatActivity {

    ArrayList<Song> tracklist = new ArrayList<>();
    TrackListAdapter adapter;
    //SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button searchButton = (Button)findViewById(R.id.searchButton);

        //do button things

        ListView tracklistList = findViewById(R.id.chatWindow);
        tracklistList.setAdapter(adapter = new TrackListAdapter());


        tracklistList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Song song = tracklist.get(pos);
            alertDialogBuilder.setTitle("Do you want to delete this?")
/*
                    //What is the message:
                    .setMessage("The selected row is: " + pos + "\n\nThe database id is: " + id)

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        elements.remove(pos);
                        deleteContact(message);
                        adapter.notifyDataSetChanged();
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> { })
*/
                    //Show the dialog
                    .create().show();
            return true;
        });

/*
        public void displayToastMsg(View view) {
            toastMsg(getResources().getString(R.string.toast_message));
        }

        private void toastMsg(String msg) {
            Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            toast.show();
        }
*/
    }

    private class TrackListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tracklist.size();
        }

        @Override
        public Object getItem(int position) {
            return tracklist.get(position);
        }

        @Override
        //last week we returned (long) position. Now we return the object's database id that we get from line 71
        public long getItemId(int position)
        {
            return  tracklist.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Song song = (Song) getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.searchedsong, parent, false);

            return newView;
        }
    }

    private class Song {
        String title;
        Time duration; //check the format of this
        String albumName;
        long id;


        private Song(long id, String title, Time duration, String albumName) {
            setId(id);
            setSongTitle(title);
            setDuration(duration);
            setAlbumName(albumName);
        }

        private void setSongTitle(String title) {
            this.title = title;
        }

        private String getSongTitle() {
            return title;
        }

        private void setDuration(Time duration) {
            this.duration = duration;
        }

        private Time getDuration() {
            return duration;
        }

        private void setAlbumName(String albumName) {
            this.albumName = albumName;
        }

        private String getAlbumName() {
            return albumName;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

    }
}
