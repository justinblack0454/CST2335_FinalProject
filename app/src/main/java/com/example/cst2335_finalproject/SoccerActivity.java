package com.example.cst2335_finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Retrieves a list of recent soccer matches that were played from https://www.scorebat.com/video-api/v1/
 */
public class SoccerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer);
    }

    private class Match{
        String title;
        String embed;
        String url;
        String thumbnail; //possible switch to image
        String date;
    }
}
