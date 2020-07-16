package com.example.cst2335_finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Uses the Deezer api to search and list the top 50 song list of a searched artist
 */
public class DeezerActivity extends AppCompatActivity {

    ArrayList<Song> tracklist = new ArrayList<>();
    TrackListAdapter adapter;
    SongQuery songQuery = new SongQuery();
    //SongQuery songQuery = null;
    EditText searchField;
    ProgressBar progressBar;
    String songListUrl;
    TextView trackListTitle;
    //SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer);

        trackListTitle = findViewById(R.id.artistName);
        progressBar = findViewById(R.id.deezerProgressBar);
        searchField = findViewById(R.id.searchField);
        Button searchButton = findViewById(R.id.searchButton);
        SongQuery songQuery = new SongQuery();
        songQuery.execute("https://api.deezer.com/search/artist/?q=paramore&output=xml");

        ListView songListView = findViewById(R.id.songListWindow);
        songListView.setAdapter(adapter = new TrackListAdapter());

        /**
         * long click listener for when user presses on a song from ListView, displays Alert
         */
        songListView.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Song song = tracklist.get(pos);
            alertDialogBuilder.setTitle("Here are some extra details: ")

                    //What is the message:
                    .setMessage("The selected row is: " + pos + "\n\n" +
                        "Song title: " + song.getSongTitle() + "\n\n" +
                            "Song Ranking: " + song.getRanking()
                    )


                    //Show the dialog
                    .create().show();
            return true;
        });

        /**
         * temporary snack bar showing "coming soon" when user clicks search button
         */
        Snackbar comingSoon = Snackbar.make(findViewById(R.id.searchButton),
                "search functionality coming soon",
                Snackbar.LENGTH_LONG);
        searchButton.setOnClickListener(btn -> {
            comingSoon.show();
            //songQuery.execute("https://api.deezer.com/search/artist/?q=paramore&output=xml");
        });
        //do button things

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

            TextView songInfo = newView.findViewById(R.id.songDetails);
            songInfo.setText(song.getSongTitle());

            return newView;
        }
    }


    /**
     * parser that extracts songlist given a specified artist
     */
    private class SongQuery extends AsyncTask<String, Integer, String> {

        private String artist;

        @Override
        protected String doInBackground(String ... args) {

            try {
                //create a URL object with server address from args
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for the data
                runOnUiThread(new Runnable() {
                      @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Please wait while we get your song list",
                            Toast.LENGTH_LONG).show();
                }
            });
                publishProgress(25);
                InputStream response = urlConnection.getInputStream();
                publishProgress(100);

                //pulling xml
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                //the parse is currently at START_DOCUMENT
                int eventType = xpp.getEventType();
                Boolean artistFound = false;

                while(artistFound.equals(false)) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("tracklist")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                songListUrl = xpp.getText();
                                artistFound = true;
                            }
                        }
                    }
                    eventType = xpp.next();
                }

                URL iconUrl = new URL(songListUrl);
                HttpURLConnection songListConnection = (HttpURLConnection) iconUrl.openConnection();
                songListConnection.connect();

                InputStream songListResponse = songListConnection.getInputStream();

                //JSON reading:
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(songListResponse, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string


                // convert string to JSON: Look at slide 27:
                JSONObject songListReport = new JSONObject(result);

                JSONArray songList = songListReport.getJSONArray("data");

                for(int i = 0; i < songList.length(); i++) {
                    JSONObject foundSong = songList.getJSONObject(i);
                    int songNum = i;
                    String songTitle = foundSong.getString("title");
                    int rank = foundSong.getInt("rank");
                    int tester = foundSong.getInt("explicit_content_lyrics");

                    Song song = new Song(songTitle, rank, tester);
                    tracklist.add(song);
                    String searchedTitleTest = tracklist.get(i).getSongTitle();
                    String test = "test";
                }

            } catch (Exception e) {
                Log.e("parsing error", e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

            return null;
        }

        public void onProgressUpdate(Integer ...value)
        {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);

        }
        public void onPostExecute(String fromDoInBackground)
        {
            searchField.setText(songListUrl);
            progressBar.setVisibility(View.INVISIBLE);
            trackListTitle.setVisibility(View.VISIBLE);

        }
    }

    /**
     * song object which stores all the relevant information that makes up a song
     */
    private class Song {
        String title;
        int duration; //check the format of this
        String albumName;
        long id;
        int ranking;
        int explicit;

        private Song(String title, int ranking, int explicit) {
            this.title = title;
            this.ranking = ranking;
            this.explicit = explicit;
        }

//        private Song(long id, String title, Time duration, String albumName) {
//            setId(id);
//            setSongTitle(title);
//            setDuration(duration);
//            setAlbumName(albumName);
//        }

        private void setSongTitle(String title) {
            this.title = title;
        }

        private String getSongTitle() {
            return title;
        }

        private void setDuration(int duration) {
            this.duration = duration;
        }

        private int getDuration() {
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

        public int getRanking(){
            return ranking;
        }
    }

}
