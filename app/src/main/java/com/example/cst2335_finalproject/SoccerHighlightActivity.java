package com.example.cst2335_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SoccerHighlightActivity extends AppCompatActivity {
    TextView title;
    TextView urlText;
    String url;
    VideoView videoView;

    //https://stackoverflow.com/questions/22493734/android-listview-not-showing-anything/22493795

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highlight_layout);


        Intent fromMain = getIntent();
        url = fromMain.getStringExtra("URL");
        urlText.setText(fromMain.getStringExtra("URL"));

        videoView = findViewById(R.id.videoView);
        videoView.setVideoPath(url);
        videoView.start();
    }
}
