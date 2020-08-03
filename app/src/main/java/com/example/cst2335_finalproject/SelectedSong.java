package com.example.cst2335_finalproject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cst2335_finalproject.DeezerActivity.Song;

public class SelectedSong extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_song);

        Song song = (Song) getIntent().getSerializableExtra("song");

        TextView artistName = findViewById(R.id.selected_artist);
        artistName.setText(song.getArtist());

        TextView songName = findViewById(R.id.selected_song_title);
        songName.setText(song.getSongTitle());

        TextView songDuration = findViewById(R.id.selected_duration);
        songDuration.setText(song.getDuration());

        TextView songAlbum = findViewById(R.id.selected_album);
        songAlbum.setText(song.getAlbumTitle());





    }

}
