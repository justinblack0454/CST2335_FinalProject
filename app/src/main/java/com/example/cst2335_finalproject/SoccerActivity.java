package com.example.cst2335_finalproject;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer);

        progressBar = findViewById(R.id.soccerProgressBar);

        MatchQuery query = new MatchQuery();
        query.execute("https://www.scorebat.com/video-api/v1/");

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

                //add toast or snack bar here perhaps

            });

            //What the no button does:
            alertDialogBuilder.setNegativeButton("No", (click, arg) -> {

            });

            //Show the dialog:
            alertDialogBuilder.create().show();

            return true;
        });

        //add to watch highlights with item click
        listOfGameTitles.setOnItemClickListener((list, item, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Match selectedMatch = matches.get(position);
            alertDialogBuilder.setTitle(selectedMatch.getTitle() + "\n\nWould you like to watch the match highlights?");

            //what is the message:
            alertDialogBuilder.setMessage("The date is: " + selectedMatch.getDate() + "\n\nTeam 1 is: " + selectedMatch.getTeam1() + "\n\nTeam 2 is: " + selectedMatch.getTeam2());

            //What the yes button does
            alertDialogBuilder.setPositiveButton("Yes", (click, arg) -> {
                //takes user to soccer highlights page
                    Intent goToHighlightVideo = new Intent(SoccerActivity.this, SoccerHighlightActivity.class);
                    //sends the title and url to the next activity, where it is used by the videoview
                    goToHighlightVideo.putExtra("TITLE", selectedMatch.getTitle());
                    goToHighlightVideo.putExtra("URL", selectedMatch.getUrl());
                    startActivity(goToHighlightVideo);
                //add toast or snackbard here perhaps
            });

            //What the no button does:
            alertDialogBuilder.setNegativeButton("No", (click, arg) -> {

            });

            //Show the dialog:
            alertDialogBuilder.create().show();
        });

        goToFavourites = findViewById(R.id.favouritesButton);
//        goToFavourites.setOnClickListener();
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
//            return matches.get(position).getId();
            return (long) position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Match match = (Match) getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View matchDetailView = inflater.inflate(R.layout.match_details, parent, false);

            TextView matchInfo = (TextView) matchDetailView.findViewById(R.id.matchInfo);
            matchInfo.setText(getItem(position).getTitle());

            TextView dateInfo = matchDetailView.findViewById(R.id.dateInfo);
            dateInfo.setText(getItem(position).getDate());

            TextView team1Info = matchDetailView.findViewById(R.id.team1Info);
            team1Info.setText(getItem(position).getTeam1());

            TextView team2Info = matchDetailView.findViewById(R.id.team2Info);
            team2Info.setText(getItem(position).getTeam2());

            return matchDetailView;
        }
    }

    private class Match {
        String title;
        String team1;
        String team2;
        String embed;
        String url;
        String thumbnail; //possible switch to image
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
}
