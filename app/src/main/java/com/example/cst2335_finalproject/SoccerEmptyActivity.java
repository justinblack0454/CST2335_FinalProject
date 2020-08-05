package com.example.cst2335_finalproject;

import android.content.Intent;
//import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;
import com.example.cst2335_finalproject.SoccerActivity.Match;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SoccerEmptyActivity extends AppCompatActivity {
    TextView title;
    TextView urlText;
    String url;
    VideoView videoView;

    //https://stackoverflow.com/questions/22493734/android-listview-not-showing-anything/22493795
    //https://www.rightpoint.com/rplabs/overlapping-android-fragment-new-instance-with-videoviews

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soccer_empty_activity_layout);
        Bundle dataToPass = new Bundle();
        ArrayList<Match> favourites = (ArrayList<Match>) getIntent().getSerializableExtra("favourites");
        dataToPass.putSerializable("favourites", favourites);
        SoccerDetailsFragment fragment = new SoccerDetailsFragment();
        fragment.setArguments(dataToPass);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLocation, fragment).commit();

    }
}
