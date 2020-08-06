package com.example.cst2335_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

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
    public static final long serialVersionUID = 1L;
//    private SharedPreferences prefs;
//    private String savedString;

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

        Toolbar soccerMyToolbar = (Toolbar) findViewById(R.id.soccerToolBar);
        setSupportActionBar(soccerMyToolbar);



        //Takes user to favourites page, accesses the database

        goToFavourites = findViewById(R.id.favouritesButton);
        goToFavourites.setOnClickListener(v -> {
            Intent goToFavourites = new Intent(SoccerActivity.this, SoccerEmptyActivity.class);
            goToFavourites.putExtra("favourites", favourites);
            startActivity(goToFavourites);
        });

        Snackbar.make(goToFavourites, R.string.SoccerFetch, Snackbar.LENGTH_LONG).show();

        ListView listOfGameTitles = (ListView) findViewById(R.id.gameTitlesList);
        listOfGameTitles.setAdapter(matchAdapter = new MatchListAdapter());

        //add to favourites with long click
        listOfGameTitles.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Match longSelectedMatch = matches.get(position);
            alertDialogBuilder.setTitle(longSelectedMatch.getTitle() + "\n" + R.string.soccerAddToFavs);

            //what is the message:
            alertDialogBuilder.setMessage(R.string.soccerDateIs + longSelectedMatch.getDate() + "\n\n" + R.string.team1Is + longSelectedMatch.getTeam1()
                    + "\n\n" + R.string.team2Is + longSelectedMatch.getTeam2());

            //What the yes button does
            alertDialogBuilder.setPositiveButton(R.string.soccerYes, (click, arg) -> {
                favourites.add(longSelectedMatch);

                ContentValues newRow = new ContentValues();
                newRow.put(MySoccerOpener.COL_TITLE, longSelectedMatch.getTitle());
                newRow.put(MySoccerOpener.COL_DATE, longSelectedMatch.getDate());
                newRow.put(MySoccerOpener.COL_TEAM_1, longSelectedMatch.getTeam1());
                newRow.put(MySoccerOpener.COL_TEAM_2, longSelectedMatch.getTeam2());
                newRow.put(MySoccerOpener.COL_URL, longSelectedMatch.getUrl());

                db.insert(MySoccerOpener.TABLE_NAME, null, newRow);

                //prevents size of list from increasing endlessly - by Justin Black
                if (favourites.size() > 0) {
                    favourites.clear();
                }


                loadDataFromDatabase();
                matchAdapter.notifyDataSetChanged();
                //add toast or snack bar here perhaps

                Toast.makeText(this, R.string.soccerAddedToFavs, Toast.LENGTH_SHORT).show();

            });

            //What the no button does:
            alertDialogBuilder.setNegativeButton(R.string.soccerNo, (click, arg) -> {

            });

            //Show the dialog:
            alertDialogBuilder.create().show();

            return true;
        });

        //watch highlights with item click
        listOfGameTitles.setOnItemClickListener((list, item, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Match selectedMatch = matches.get(position);
            alertDialogBuilder.setTitle(selectedMatch.getTitle() + "\n" + R.string.soccerWatchHighlights);

            //what is the message:
            alertDialogBuilder.setMessage(R.string.soccerDateIs + selectedMatch.getDate() + "\n\n" + R.string.team1Is + selectedMatch.getTeam1()
                    + "\n\n" + R.string.team2Is + selectedMatch.getTeam2());

            //What the yes button does
            alertDialogBuilder.setPositiveButton(R.string.soccerYes, (click, arg) -> {
                //takes user to soccer highlights page

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedMatch.getUrl()));
                startActivity(browserIntent);

                //add toast or snackbar here perhaps
            });

            //What the no button does:
            alertDialogBuilder.setNegativeButton(R.string.soccerNo, (click, arg) -> {

            });

            //Show the dialog:
            alertDialogBuilder.create().show();
        });
    }

    protected void removeMatch(Match match) {
        db.delete(MySoccerOpener.TABLE_NAME, MySoccerOpener.COL_ID + "= ?", new String[]{Long.toString(match.getId())});
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
    public static class Match implements Serializable {
        String title;
        String team1;
        String team2;
        String url;
        String date;
        long id;

        public Match(String title, String date, String team1, String team2, String url) {
            this.title = title;
            this.date = date;
            this.team1 = team1;
            this.team2 = team2;
            this.url = url;
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


        public String getUrl() {
            return url;
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
                publishProgress(20);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream response = urlConnection.getInputStream();

                //JSON reading:
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                publishProgress(40);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string

                publishProgress(60);


                // convert string to JSON: Look at slide 27:
                JSONArray matchReport = new JSONArray(result);
                publishProgress(80);
                for (int i = 0; i < matchReport.length(); i++) {
                    JSONObject match = matchReport.getJSONObject(i);
                    String title = match.getString("title");
                    String date = match.getString("date");
                    String team1 = match.getJSONObject("side1").getString("name");
                    String team2 = match.getJSONObject("side2").getString("name");

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
                    publishProgress(80);
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
        MySoccerOpener dbOpener = new MySoccerOpener(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String[] columns = {MySoccerOpener.COL_ID, MySoccerOpener.COL_TITLE, MySoccerOpener.COL_DATE, MySoccerOpener.COL_TEAM_1, MySoccerOpener.COL_TEAM_2, MySoccerOpener.COL_URL};
        //query all the results from the database:
        Cursor results = db.query(false, MySoccerOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int titleColIndex = results.getColumnIndex(MySoccerOpener.COL_TITLE);
        int dateColIndex = results.getColumnIndex(MySoccerOpener.COL_DATE);
        int idColIndex = results.getColumnIndex(MySoccerOpener.COL_ID);
        int team1ColIndex = results.getColumnIndex(MySoccerOpener.COL_TEAM_1);
        int team2ColIndex = results.getColumnIndex(MySoccerOpener.COL_TEAM_2);
        int urlColIndex = results.getColumnIndex(MySoccerOpener.COL_URL);

        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            String title = results.getString(titleColIndex);
            String date = results.getString(dateColIndex);
            String team1 = results.getString(team1ColIndex);
            String team2 = results.getString(team2ColIndex);
            String url = results.getString(urlColIndex);
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            favourites.add(new Match(title, date, team1, team2, url));
        }
        //At this point, the contactsList array has loaded every row from the cursor.
        printCursor(results, db.getVersion());
    }

    protected void printCursor(Cursor c, int version) {
        Log.v("Match Object", String.valueOf(db.getVersion()));
        Log.v("Match number of cols", String.valueOf(c.getColumnCount()));
        Log.v("Match col names", Arrays.toString(c.getColumnNames()));
        Log.v("Match number of rows", String.valueOf(c.getCount()));
        Log.v("Match Object", DatabaseUtils.dumpCursorToString(c));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.soccer_menu, menu);
        return true;
    }

//    private void saveSharedPrefs(String stringToSave) {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("Value", stringToSave);
//        editor.commit();
//    }

}
