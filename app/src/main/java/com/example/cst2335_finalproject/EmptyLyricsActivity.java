package com.example.cst2335_finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Empty Lyrics Activity
 * @author Aahuti Patel-040974663
 * @version 1.0
 */
public class EmptyLyricsActivity extends AppCompatActivity {
    /**
     * on Create for Empty Lyrics activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emptylyrics);
        Bundle dataToPass = getIntent().getExtras();
        DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
        dFragment.setArguments( dataToPass ); //pass it a bundle for information
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, dFragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
    }
}
