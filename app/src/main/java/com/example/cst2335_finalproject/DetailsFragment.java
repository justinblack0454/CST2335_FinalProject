package com.example.cst2335_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.cst2335_finalproject.DeezerActivity.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    SQLiteDatabase db;
    private Bundle dataFromActivity;
    ArrayList<Song> tracklist = null;
    private long id;
    int isSend;
    private AppCompatActivity parentActivity;
    TrackListAdapter adapter;



    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(DeezerActivity.ITEM_ID );
        isSend = dataFromActivity.getInt(DeezerActivity.SONG);

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_details, container, false);

        //show the message
        TextView message = (TextView)result.findViewById(R.id.fragmessage);
        message.setText("MESSAGE: " + dataFromActivity.getString(DeezerActivity.SONG));

        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.idmessage);
        idView.setText("ID=" + id);

        //show the checkbox
        CheckBox checkBox = (CheckBox)result.findViewById(R.id.fragcheckbox);
        if(isSend == 1) {
            checkBox.toggle();
        }

        //favsView
        ListView favsView = (ListView)result.findViewById(R.id.favView);
        favsView.setAdapter(adapter = new TrackListAdapter());


        // get the delete button, and add a click listener:
        Button finishButton = (Button)result.findViewById(R.id.hereButton);
        finishButton.setOnClickListener( clk -> {

            ContentValues newRowValues = new ContentValues();
            newRowValues.put(MyOpener.ARTIST, DeezerActivity.ARTIST);
            newRowValues.put(MyOpener.SONG, DeezerActivity.SONG);
            newRowValues.put(MyOpener.DURATION, DeezerActivity.DURATION);
            newRowValues.put(MyOpener.ALBUM, DeezerActivity.ALBUM);
        });

        return result;
    }

//    private void loadDataFromDatabase() {
//        MyOpener dbOpener = new MyOpener(getActivity());
//        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer
//
//
//        // We want to get all of the columns. Look at MyOpener.java for the definitions:
//        String [] columns = {MyOpener.COL_ID, MyOpener.ARTIST, MyOpener.SONG, MyOpener.DURATION, MyOpener.ALBUM};
//        //query all the results from the database:
//        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);
//
//        //Now the results object has rows of results that match the query.
//        //find the column indices:
//        int artistColumnIndex = results.getColumnIndex(MyOpener.ARTIST);
//        int songColumnIndex = results.getColumnIndex(MyOpener.SONG);
//        int durationColumnIndex = results.getColumnIndex(MyOpener.DURATION);
//        int albumColIndex = results.getColumnIndex(MyOpener.ALBUM);
//        int idColIndex = results.getColumnIndex(MyOpener.COL_ID);
//
//        //iterate over the results, return true if there is a next item:
//        while(results.moveToNext())
//        {
//            String artist = results.getString(artistColumnIndex);
//            String song = results.getString(songColumnIndex);
//            String duration = results.getString(durationColumnIndex);
//            String album = results.getString(albumColIndex);
//            long id = results.getLong(idColIndex);
//
//            //add the new Contact to the array list:
//            tracklist.add(new Song(artist, song, duration, album));
//        }
//    }
//
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
            DeezerActivity.Song song = (DeezerActivity.Song) getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.searchedsong, parent, false);

            TextView songInfo = newView.findViewById(R.id.songDetails);
            songInfo.setText(song.getSongTitle());

            return newView;
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }

}
