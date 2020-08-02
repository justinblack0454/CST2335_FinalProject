package com.example.cst2335_finalproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Author: Jonathan Cordingley
 * Title: Soccer Match Highlights
 * Version: 1.0 August 7th, 2020
 * Retrieves a list of recent soccer matches that were played from https://www.scorebat.com/video-api/v1/
 */

public class SoccerActivity extends AppCompatActivity {

    ArrayList<Match> matches = new ArrayList<>();
    ArrayList<Match> favourites = new ArrayList<>();
    MatchListAdapter matchAdapter;
    ProgressBar progressBar;
    Button goToFavourites;
    TextView listHeader;

    SQLiteDatabase db;
    int positionClicked = 0;
    public static final String TITLE = "TITLE";
    public static final String DATE = "DATE";
    public static final String TEAM_1 = "TEAM1";
    public static final String TEAM_2 = "TEAM2";
    public static final String URL = "URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer);

        progressBar = findViewById(R.id.soccerProgressBar);

        MatchQuery query = new MatchQuery();
        query.execute("https://www.scorebat.com/video-api/v1/");

        loadDataFromDatabase();

        ListView listOfGameTitles = (ListView) findViewById(R.id.gameTitlesList);
        listOfGameTitles.setAdapter(matchAdapter = new MatchListAdapter());

        //add to favourites with long click
        listOfGameTitles.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Match longSelectedMatch = matches.get(position);
            alertDialogBuilder.setTitle(longSelectedMatch.getTitle() + "\n\nWould you like to save this match to your favourites?");

            //what is the message:
            alertDialogBuilder.setMessage("The date is: " + longSelectedMatch.getDate() + "\n\nTeam 1 is: " + longSelectedMatch.getTeam1() + "\n\nTeam 2 is: " + longSelectedMatch.getTeam2());

            //What the yes button does
            alertDialogBuilder.setPositiveButton("Yes", (click, arg) -> {
                favourites.add(longSelectedMatch);

                //Create a bundle to pass data to the new fragment
                Bundle dataToPass = new Bundle();
                dataToPass.putString(TITLE, longSelectedMatch.getTitle());
                dataToPass.putString(DATE, longSelectedMatch.getDate());
                dataToPass.putString(TEAM_1, longSelectedMatch.getTeam1());
                dataToPass.putString(TEAM_2, longSelectedMatch.getTeam2());
                dataToPass.putString(URL, longSelectedMatch.getUrl());

                //add toast or snack bar here perhaps

            });

            //What the no button does:
            alertDialogBuilder.setNegativeButton("No", (click, arg) -> {

            });

            //Show the dialog:
            alertDialogBuilder.create().show();

            return true;
        });

        //watch highlights with item click
        listOfGameTitles.setOnItemClickListener((list, item, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Match selectedMatch = matches.get(position);
            alertDialogBuilder.setTitle(selectedMatch.getTitle() + "\n\nWould you like to watch the match highlights?");

            //what is the message:
            alertDialogBuilder.setMessage("The date is: " + selectedMatch.getDate() + "\n\nTeam 1 is: " + selectedMatch.getTeam1() + "\n\nTeam 2 is: " + selectedMatch.getTeam2());

            //What the yes button does
            alertDialogBuilder.setPositiveButton("Yes", (click, arg) -> {
                //takes user to soccer highlights page

//                    Intent goToHighlightVideo = new Intent(SoccerActivity.this, SoccerHighlightActivity.class);
////                    //sends the title and url to the next activity, where it is used by the videoview
////                    goToHighlightVideo.putExtra("TITLE", selectedMatch.getTitle());
////                    goToHighlightVideo.putExtra("URL", selectedMatch.getUrl());
////                    startActivity(goToHighlightVideo);
                Bundle dataToPass = new Bundle();
                dataToPass.putString(URL, selectedMatch.getUrl());

                Intent nextActivity = new Intent(SoccerActivity.this, SoccerHighlightActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition

                //add toast or snackbar here perhaps
            });

            //What the no button does:
            alertDialogBuilder.setNegativeButton("No", (click, arg) -> {

            });

            //Show the dialog:
            alertDialogBuilder.create().show();
        });

        //Takes user to favourites page, acceses the database

        goToFavourites = findViewById(R.id.favouritesButton);
        goToFavourites.setOnClickListener( v -> {
            Intent goToFavourites = new Intent(SoccerActivity.this, SoccerFavourites.class);
            startActivity(goToFavourites);
        });
    }

    private class MatchListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return matches.size();
        }

        @Override
        public Match getItem(int position) {

            return matches.get(position);
        }

        @Override
        //last week we returned (long) position. Now we return the object's database id that we get from line 71
        public long getItemId(int position) {
            return matches.get(position).getId();
//            return (long) position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Match match = (Match) getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View matchDetailView = inflater.inflate(R.layout.match_details, parent, false);

            TextView matchInfo = (TextView) matchDetailView.findViewById(R.id.matchInfo);
            matchInfo.setText(getItem(position).getTitle());

            TextView dateInfo = (TextView) matchDetailView.findViewById(R.id.dateInfo);
            dateInfo.setText(getItem(position).getDate());

            TextView team1Info = (TextView) matchDetailView.findViewById(R.id.team1Info);
            team1Info.setText(getItem(position).getTeam1());

            TextView team2Info = (TextView) matchDetailView.findViewById(R.id.team2Info);
            team2Info.setText(getItem(position).getTeam2());

            return matchDetailView;
        }
    }

    //changed from private because I'm lazy
    public class Match {
        String title;
        String team1;
        String team2;
        String embed;
        String url;
        String thumbnail; //possible switch to image
        String date;
        long id;

        public Match(String title, String date, String team1, String team2, String url, long id){
            this.title = title;
            this.date = date;
            this.team1 = team1;
            this.team2 = team2;
            this.url = url;
            this.id = id;
        }

        public Match(String title, String date, String team1, String team2, String url) {
            this(title, date, team1, team2, url, 0);
        }


        public String getTitle() {
            return title;
        }

        public String getTeam1() {
            return team1;
        }

        public String getTeam2() {
            return team2;
        }

        public String getEmbed() {
            return embed;
        }

        public String getUrl() {
            return url;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public String getDate() {
            return date;
        }

        public long getId() {
            return id;
        }
    }

    private class MatchQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream response = urlConnection.getInputStream();

                //JSON reading:
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string

                publishProgress(25);


                // convert string to JSON: Look at slide 27:
                JSONArray matchReport = new JSONArray(result);

                for (int i = 0; i < matchReport.length(); i++) {
                    JSONObject match = matchReport.getJSONObject(i);
                    String title = match.getString("title");
                    String date = match.getString("date");
                    String team1 = match.getJSONObject("side1").getString("name");
                    String team2 = match.getJSONObject("side2").getString("name");


                    // for url:
                    //get string under embed object of videos array
                    //save to variable
                    //parse string with String.split() or something to get url after word src
                    //String url;

                    //revisit later. For now, just take first embedded video. Some matches have more than 1
                    JSONArray videosArray = match.getJSONArray("videos");
                    for (int j = 0; j < 1; j++) {
                        JSONObject video = videosArray.getJSONObject(j);
                        String urlToParse = video.getString("embed");
                        //find index of the start of the url
                        int indexOfUrl = urlToParse.indexOf("src='");
                        //split string at the index
                        String urlParsed = urlToParse.substring(indexOfUrl + 5);
                        //remove everything including after url by finding index of next ':
                        int indexToCutAt = urlParsed.indexOf("'");
                        String urlProcessed = urlParsed.substring(0, indexToCutAt);

                        matches.add(new Match(title, date, team1, team2, urlProcessed));
                    }
                    publishProgress(75);
                }
            } catch (Exception e) {
                e.printStackTrace(); //do something
            }

            publishProgress(100);
            //possibly change
            return null;
        }

        public void onProgressUpdate(Integer... value) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);

        }

        public void onPostExecute(String fromDoInBackground) {
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    private void loadDataFromDatabase() {
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String[] columns = {MyOpener.COL_ID, MyOpener.COL_TITLE, MyOpener.COL_DATE, MyOpener.COL_TEAM_1, MyOpener.COL_TEAM_2, MyOpener.COL_URL};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);


        //Now the results object has rows of results that match the query.
        //find the column indices:
        int titleColIndex = results.getColumnIndex(MyOpener.COL_TITLE);
        int dateColIndex = results.getColumnIndex(MyOpener.COL_DATE);
        int idColIndex = results.getColumnIndex(MyOpener.COL_ID);
        int team1ColIndex = results.getColumnIndex(MyOpener.COL_TEAM_1);
        int team2ColIndex = results.getColumnIndex(MyOpener.COL_TEAM_2);
        int urlColIndex = results.getColumnIndex(MyOpener.COL_URL);


        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            String title = results.getString(titleColIndex);
            String date = results.getString(dateColIndex);
            String team1 = results.getString(team1ColIndex);
            String team2 = results.getString(team2ColIndex);
            String url = results.getString(urlColIndex);
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            favourites.add(new Match(title, date, team1, team2, url, id));
        }

        //At this point, the contactsList array has loaded every row from the cursor.
//        printCursor(results, db.getVersion());
    }

    public class MyOpener extends SQLiteOpenHelper {

        protected final static String DATABASE_NAME = "FavouritesDB";
        protected final static int VERSION_NUM = 1;
        public final static String TABLE_NAME = "Favourites";
        public final static String COL_TITLE = "TITLE";
        public final static String COL_DATE = "DATE";
        public final static String COL_ID = "_id";
        public final static String COL_TEAM_1 = "TEAM1";
        public final static String COL_TEAM_2 = "TEAM2";
        public final static String COL_URL = "URL";

        public MyOpener(Context ctx) {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }


        //This function gets called if no database file exists.
        //Look on your device in the /data/data/package-name/database directory.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_TITLE + " text,"
                    + COL_DATE + " text,"
                    + COL_TEAM_1 + " text,"
                    + COL_TEAM_2 + " text,"
                    + COL_URL + " text);");  // add or remove columns
        }

        //this function gets called if the database version on your device is lower than VERSION_NUM
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            //Create the new table:
            onCreate(db);
        }


        //this function gets called if the database version on your device is higher than VERSION_NUM
        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            //Create the new table:
            onCreate(db);
        }
    }
}
