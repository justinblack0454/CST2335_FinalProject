package com.example.cst2335_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Uses the Deezer api to search and list the top 50 song list of a searched artist
 */
public class DeezerActivity extends AppCompatActivity {

    ArrayList<Song> tracklist = new ArrayList<>();
    TrackListAdapter adapter;
    Boolean isTablet;
    ImageView albumCoverView;
    SongQuery songQuery = new SongQuery();
    //SongQuery songQuery = null;
    EditText searchField;
    Boolean asyncCancelled;
    ProgressBar progressBar;
    String songListUrl;
    TextView trackListTitle;
    Bitmap albumCover = null;
    private SharedPreferences prefs;
    private String savedSearchString;
    SQLiteDatabase db;
    String artistName;
    String coverLink;
    public static final long serialVersionUID = 1L;
    public static final String ARTIST = "ARTIST";
    public static final String SONG = "SONG";
    public static final String ITEM_ID = "ID";
    public static final String DURATION = "DURATION";
    public static final String ALBUM = "ALBUM";
    public static final String COVER = "COVER";

    DetailsFragment aFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer);

        albumCoverView = null;
        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        trackListTitle = findViewById(R.id.artistName);
        progressBar = findViewById(R.id.deezerProgressBar);
        searchField = findViewById(R.id.searchField);
        Button searchButton = findViewById(R.id.searchButton);
        SongQuery songQuery = new SongQuery();
        //songQuery.execute("https://api.deezer.com/search/artist/?q=paramore&output=xml");
        searchField.setText(savedSearchString);
        //saveSharedPrefs(searchField.getText().toString());
        //loadDataFromDatabase();

        ListView songListView = findViewById(R.id.songListWindow);
        songListView.setAdapter(adapter = new TrackListAdapter());

        songListView.setOnItemClickListener((list, item, position, id) -> {
                Intent nextActivity = new Intent(DeezerActivity.this, EmptyActivity.class);
                nextActivity.putExtra("tracklist", tracklist);
                startActivity (nextActivity);

        });
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
                            "Song duration: " + song.getDuration() + "\n\n" +
                            "Album title: " + song.getAlbumTitle() + "\n\n" +
                            "Album cover: " + song.getAlbumCover() //generate albumView xml to display this cover actually
                    )

                    //positive button to favourite the song which then put song in DB and opens fragment with the favourties db view
                    .setPositiveButton("add to favourites", (click, arg) -> {
                        ContentValues newRowValues = new ContentValues();
                        newRowValues.put(MyOpener.ARTIST, artistName);
                        newRowValues.put(MyOpener.SONG, song.getSongTitle());
                        newRowValues.put(MyOpener.DURATION, song.getDuration());
                        newRowValues.put(MyOpener.ALBUM, song.getAlbumTitle());
                        //newRowValues.put(MyOpener.COVER, song.getAlbumCover());

                        db.insert(MyOpener.TABLE_NAME, null, newRowValues);
                        loadDataFromDatabase();
                        adapter.notifyDataSetChanged();

                    })

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
            //comingSoon.show();
            artistName = searchField.getText().toString();
            new SongQuery().execute("https://api.deezer.com/search/artist/?q=" + searchField.getText().toString().replace(" ", "") + "&output=xml");
            saveSharedPrefs(searchField.getText().toString());
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        savedSearchString = searchField.getText().toString();
        saveSharedPrefs(savedSearchString);
    }

    @Override
    protected void onStop(){
        super.onStop();
        savedSearchString = searchField.getText().toString();
        saveSharedPrefs(savedSearchString);
    }

    @Override
    protected void onResume(){
        super.onResume();
        searchField.setText(savedSearchString);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        searchField.setText(savedSearchString);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    private void loadDataFromDatabase() {
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {MyOpener.COL_ID, MyOpener.ARTIST, MyOpener.SONG, MyOpener.DURATION, MyOpener.ALBUM};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int artistColumnIndex = results.getColumnIndex(MyOpener.ARTIST);
        int songColumnIndex = results.getColumnIndex(MyOpener.SONG);
        int durationColumnIndex = results.getColumnIndex(MyOpener.DURATION);
        int albumColIndex = results.getColumnIndex(MyOpener.ALBUM);
        int idColIndex = results.getColumnIndex(MyOpener.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String artist = results.getString(artistColumnIndex);
            String song = results.getString(songColumnIndex);
            String duration = results.getString(durationColumnIndex);
            String album = results.getString(albumColIndex);
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            tracklist.add(new Song(artist, song, duration, album, coverLink));
        }

        //At this point, the contactsList array has loaded every row from the cursor.
        printCursor(results, db.getVersion());

    }

    protected void printCursor (Cursor c, int version) {
        Log.v("Cursor Object", String.valueOf(db.getVersion()));
        Log.v("Cursor number of cols", String.valueOf(c.getColumnCount()));
        Log.v("Cursor col names", Arrays.toString(c.getColumnNames()));
        Log.v("Cursor number of rows", String.valueOf(c.getCount()));
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(c));
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
                publishProgress(20);
                InputStream response = urlConnection.getInputStream();

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
                publishProgress(40);

                URL iconUrl = new URL(songListUrl);
                HttpURLConnection songListConnection = (HttpURLConnection) iconUrl.openConnection();
                songListConnection.connect();

                InputStream songListResponse = songListConnection.getInputStream();

                publishProgress(60);

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

                    //Getting the nested album details
                    JSONObject foundSongAlbumDetails = foundSong.getJSONObject("album");
                    JSONObject foundSongArtistDetails = foundSong.getJSONObject("artist");

                    //Artist Details
                    //int artistID = foundSongArtistDetails.getInt("id");
                    String artistName = foundSongArtistDetails.getString("name");

                    //Album Details
                    //int albumID = foundSongAlbumDetails.getInt("id");
                    String albumTitle = foundSongAlbumDetails.getString("title");

                    //Album Cover
                    String coverLink = foundSongAlbumDetails.getString("cover_small");
                    URL albumCoverURL = new URL(coverLink);
                    HttpURLConnection albumCoverConnection = (HttpURLConnection) albumCoverURL.openConnection();
                    albumCoverConnection.connect();
                    int responseCode = albumCoverConnection.getResponseCode();

                    if(responseCode == 200){
                        albumCover = BitmapFactory.decodeStream(albumCoverConnection.getInputStream());
                        FileOutputStream outputStream = openFileOutput( albumCover + ".png", Context.MODE_PRIVATE);
                        albumCover.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }

                    String imageFilename = albumCover + ".png";
                    FileInputStream fis = null;
                    try {    fis = openFileInput(albumCover + ".png");   }
                    catch (FileNotFoundException e) {    e.printStackTrace();  }
                    Bitmap bm = BitmapFactory.decodeStream(fis);

                    //Song Details
                    String songTitle = foundSong.getString("title");
                    //int rank = foundSong.getInt("rank");
                    int durationInSeconds = foundSong.getInt("duration");
                    int minute = durationInSeconds / 60;
                    int second = durationInSeconds % 60;
                    String duration = minute + "min " + second + "sec";

                    //int tester = foundSong.getInt("explicit_content_lyrics");
                    publishProgress(80);


                    Song song = new Song(artistName, songTitle, duration, albumTitle, coverLink);
                    tracklist.add(song);
                    String searchedTitleTest = tracklist.get(i).getSongTitle();
                    String test = "test";

                    trackListTitle.setText("Showing results for " + artistName);
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
            //songQuery.cancel(true);
            return null;
        }

        public void onProgressUpdate(Integer ...value)
        {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);

        }
        public void onPostExecute(String fromDoInBackground)
        {
            searchField.setText(artistName);
            //albumCoverView.setImageBitmap(albumCover);
            progressBar.setVisibility(View.INVISIBLE);
            trackListTitle.setVisibility(View.VISIBLE);

        }
    }

    /**
     * song object which stores all the relevant information that makes up a song
     */
    protected static class Song implements Serializable {
        String artist;
        String title;
        long id;
        String duration;
        String albumTitle;
        String coverLink;

        protected Song(String artist, String title, String duration, String albumTitle){
            this.artist = artist;
            this.title = title;
            this.duration = duration;
            this.albumTitle = albumTitle;
        }

        private Song(String artist, String title, String duration, String albumTitle, String coverLink) {
            this.artist = artist;
            this.title = title;
            this.duration = duration;
            this.albumTitle = albumTitle;
            this.coverLink = coverLink;
        }

        private String getArtist() { return artist; }

        protected String getSongTitle() {
            return title;
        }

        private String getDuration() {
            return duration;
        }

        private String getAlbumTitle() {
            return albumTitle;
        }

        private String getAlbumCover() { return coverLink; }

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

    }

    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ReserveName", stringToSave);
        editor.commit();

    }

}
