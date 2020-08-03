package com.example.cst2335_finalproject;

import android.os.Bundle;
import com.example.cst2335_finalproject.DeezerActivity.Song;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DeezerEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_deezer);

        Bundle dataToPass = new Bundle();
        ArrayList<Song> tracklist = (ArrayList<Song>) getIntent().getSerializableExtra("tracklist");
        dataToPass.putSerializable("tracklist", tracklist);

        DeezerDetailsFragment aFragment = new DeezerDetailsFragment();
        aFragment.setArguments(dataToPass);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLocation, aFragment)
                . commit();
    }


}
