package com.example.cst2335_finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.example.cst2335_finalproject.DeezerActivity.Song;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = new Bundle();
        ArrayList<Song> tracklist = (ArrayList<Song>) getIntent().getSerializableExtra("tracklist");
        dataToPass.putSerializable("tracklist", tracklist);

        DetailsFragment aFragment = new DetailsFragment();
        aFragment.setArguments(dataToPass);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLocation, aFragment)
                . commit();
    }


}
