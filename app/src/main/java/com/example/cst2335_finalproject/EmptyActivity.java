package com.example.cst2335_finalproject;

import android.os.Bundle;
import com.example.cst2335_finalproject.DeezerActivity.Song;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = getIntent().getExtras();
        //ArrayList<Song> tracklist = dataToPass.getParcelableArrayList("tracklist");

        DetailsFragment aFragment = new DetailsFragment();
        aFragment.setArguments(dataToPass);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLocation, aFragment)
                . commit();
    }


}
