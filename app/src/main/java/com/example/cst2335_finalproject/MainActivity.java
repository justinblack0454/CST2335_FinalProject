package com.example.cst2335_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button deezerButton = findViewById(R.id.deezerButton);
        Button soccerButton = findViewById(R.id.soccerButton);
        Button songLyricsButton = findViewById(R.id.songlyrics);

        deezerButton.setOnClickListener(btn -> {
            Intent goToDeezer = new Intent(MainActivity.this, DeezerActivity.class);
            startActivity(goToDeezer);
        });

        soccerButton.setOnClickListener(btn -> {
            Intent goToSoccer = new Intent(MainActivity.this, SoccerActivity.class);
            startActivity(goToSoccer);
        });

        songLyricsButton.setOnClickListener(btn -> {
            Intent goToSongLyrics = new Intent(MainActivity.this, SongLyricsActivity.class);
            startActivity(goToSongLyrics);
           // Log.e(ACTIVITY_NAME, "In function:" + "onCreate");
        });

    }
}
