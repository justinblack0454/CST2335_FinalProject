package com.example.cst2335_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button songLyricsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button deezerButton = findViewById(R.id.deezerButton);

        deezerButton.setOnClickListener(btn -> {
            Intent goToDeezer = new Intent(MainActivity.this, DeezerActivity.class);
            startActivity(goToDeezer);
        });

        songLyricsButton = findViewById(R.id.songlyrics);
        songLyricsButton.setOnClickListener(btn -> {
            Intent goToSongLyrics = new Intent(MainActivity.this, SongLyricsActivity.class);
            startActivity(goToSongLyrics);
           // Log.e(ACTIVITY_NAME, "In function:" + "onCreate");
        });

    }
}
