package com.example.cst2335_finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class SongLyricsMenu extends AppCompatActivity {
    Toolbar tBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songlyrics);

         tBar = (Toolbar)findViewById(R.id.ToolBarLyrics);

        setSupportActionBar(tBar);





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_songlyrics_menu, menu);


	    /* slide 15 material:
	    MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchView sView = (SearchView)searchItem.getActionView();
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }  });
	    */

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.geo:
                Intent goToGeo = new Intent(SongLyricsMenu.this, SongLyricsActivity.class);
                startActivity(goToGeo);
                break;
            case R.id.deezer:
                Intent goToDeezer = new Intent(SongLyricsMenu.this, DeezerActivity.class);
                startActivity(goToDeezer);
                break;
            case R.id.soccer:
                Intent goToSoccer = new Intent(SongLyricsMenu.this, SongLyricsActivity.class);
                startActivity(goToSoccer);
                break;
            case R.id.about:
                Toast.makeText(this, "This is the Lyrics Search activity, written by Aahuti Patel", Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }
}
