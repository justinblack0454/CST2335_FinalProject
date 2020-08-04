package com.example.cst2335_finalproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SongLyricsActivity extends AppCompatActivity {
    EditText searchartist;
    EditText searchtitle;
    ProgressBar progressBar;
    String songListUrl;
    ListView songLyricsListView;
    Button search;
    private TextView titlename;
    TextView trackListTitle;
    SQLiteDatabase db;
    Button save;
    Button help;
    Button lyrics;
    public static final String ITEM_TITLE = "TITLE";
    public static final String  ITEM_ARTIST= "ARTIST";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    ArrayList<SongLyrics> songLyricsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songlyrics);

        save= findViewById(R.id.savelyrics);
        searchartist= findViewById(R.id.searchartist);
        searchtitle=findViewById(R.id.searchtitle);
        progressBar= findViewById(R.id.songLyricsProgressBar);
        search = findViewById(R.id.searchlyrics);
        help=findViewById(R.id.helpLyrics);
        lyrics = findViewById(R.id.searchlyricsnow);
        boolean isTablet = findViewById(R.id.frame) != null;
        songLyricsListView= (ListView)findViewById(R.id.songlyricsList);
        loadDataFromDatabase();


         class SongLyricsList extends BaseAdapter {


            @Override
            public int getCount() {
                return songLyricsList.size();
            }

            @Override
            public SongLyrics getItem(int position) {
                return songLyricsList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return getItem(position).getId();

            }


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                SongLyrics songLyricsObj = (SongLyrics) getItem(position);
                LayoutInflater inflater = getLayoutInflater();
                View view = null;
               // if(songLyricsObj.search)
                    view = inflater.inflate(R.layout.music, parent, false);

                titlename = (TextView) view.findViewById(R.id.msgr);
                titlename.setText(songLyricsObj.title+" by "+songLyricsObj.artistName );

                return view;            }}


        SongLyricsList myAdapter =new SongLyricsList();
        songLyricsListView.setAdapter(myAdapter);
        search.setOnClickListener(new View.OnClickListener(){


            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.google.com/search?q="+ searchartist.getText()+"+"+searchtitle.getText()));
                startActivity(intent);


            }
        });

        save.setOnClickListener(e->{
            SongLyrics song = new SongLyrics(true,searchtitle.getText().toString(),searchartist.getText().toString());
            songLyricsList.add(song);

            ContentValues newRowValues = new ContentValues();
            ContentValues newRowValues1 = new ContentValues();
            newRowValues.put(SongLyricsDatabase.COL_TITLE, song.getSongTitle());
            newRowValues.put(SongLyricsDatabase.COL_ARTIST, song.getArtistName());

            long newId = db.insert(SongLyricsDatabase.TABLE_NAME, null, newRowValues);
            song.setId(newId);
            searchtitle.setText("");
            searchartist.setText("");
            myAdapter.notifyDataSetChanged();
            searchtitle.getText().clear();
            searchartist.getText().clear();
            Snackbar snackbar = Snackbar
                    .make(save, "Song saved to your List!", Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar snackbar1 = Snackbar.make(save, "Enjoy your lyrics by clicking LYRICS!", Snackbar.LENGTH_LONG);
                            snackbar1.show();
                        }
                    });
            snackbar.show();

        });

        help.setOnClickListener(e->{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle("Here are some instructions").setMessage("To save your favourite song and artist,  click SAVE\nTo get the song on Google, click SEARCH \nTo delete a song, long press on the song from the list\nTo get details of the click on the song from your list")

                    .setNegativeButton("Close", (click, arg) -> {
                    })
                    .create().show();

        });



        lyrics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongLyricsQuery songlyricsQuery = new SongLyricsQuery();
                    songlyricsQuery.execute("https://api.lyrics.ovh/v1/" + searchartist.getText() + "/" + searchtitle.getText());
                }

            });






        songLyricsListView.setOnItemLongClickListener( (p, b, position, id) -> {
            SongLyrics selectedMessage = songLyricsList.get(position);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            SongLyrics song = songLyricsList.get(position);
            alertDialogBuilder.setTitle("You want to delete this item").setMessage("Row is: " + position + " and database id is: " + id)
                    .setPositiveButton("Yes", (click, arg) -> {
                          deleteMessage(selectedMessage);
                        songLyricsList.remove(position);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", (click, arg) -> {
                    })
                    .create().show();


            return true;
        });

        songLyricsListView.setOnItemClickListener((p, b, position, id)->{
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TITLE, songLyricsList.get(position).getSongTitle());
            dataToPass.putString(ITEM_ARTIST, songLyricsList.get(position).getArtistName());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            if(isTablet)
            {
                DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(SongLyricsActivity.this, EmptyLyricsActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }


        });
    }

    public void loadDataFromDatabase () {
        //get a database connection:
        SongLyricsDatabase dbOpener = new SongLyricsDatabase(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        String[] columns = {SongLyricsDatabase.COL_ID, SongLyricsDatabase.COL_TITLE, SongLyricsDatabase.COL_ARTIST};
        Cursor results = db.query(false, SongLyricsDatabase.TABLE_NAME, columns, null, null, null, null, null, null);

        int messageColumnIndex = results.getColumnIndex(SongLyricsDatabase.COL_TITLE);
        int idColIndex = results.getColumnIndex(SongLyricsDatabase.COL_ID);
        int sideIndex = results.getColumnIndex(SongLyricsDatabase.COL_ARTIST);
        while (results.moveToNext()) {
            String titles = results.getString(messageColumnIndex);
            String artists = results.getString(sideIndex);
            int id = results.getInt(idColIndex);

            songLyricsList.add(new SongLyrics(id,titles,artists));
        }

    }

    private void deleteMessage(SongLyrics selectedMessage) {
        db.delete(SongLyricsDatabase.TABLE_NAME, SongLyricsDatabase.COL_ID + "= ?", new String[]{Long.toString(selectedMessage.getId())});
    }


//    protected void printCursor (Cursor c,int version){
//        Cursor d = db.rawQuery("SELECT * from " + SongLyricsDatabase.TABLE_NAME, null);
//        int colIndex = d.getColumnIndex("Messages");
//        db.getVersion();
//        Log.d("Columns", "Number of Columns:" + d.getColumnCount());
//        Log.d("name", "Column name:" + d.getColumnName(colIndex));
//        Log.d("rows", "Number of Rows:" + d.getCount());
//        for (int i = 0; i < d.getCount(); i++) {
//            String fn = d.getString(colIndex);
//            Log.d("messages", "Message: " + fn);
//            d.moveToNext();
//
//        }
//
//    }











        private class SongLyricsQuery extends AsyncTask<String, Integer, String>{
            String parameter = null;
        @Override
        protected String doInBackground(String ... args) {
            try {
                //create a URL object with server address from args
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = connection.getInputStream();


                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");
                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    publishProgress(50);
                    if (eventType == XmlPullParser.START_TAG) {


                        if (xpp.getName().equals("lyrics")) {

                            publishProgress(25);
                            parameter= xpp.getText();
                            publishProgress(75);
                            publishProgress(100);

                        }
            }
                    eventType = xpp.next();
                }




            }
            catch(Exception e){
            e.getMessage();
            }
        return parameter;
        }
        public void onProgressUpdate(Integer ...value) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        public void onPostExecute(String fromDoInBackground)
        {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://api.lyrics.ovh/v1/" + searchartist.getText() + "/" + searchtitle.getText()));
            intent.toString();
            startActivity(intent);
            progressBar.setVisibility(View.INVISIBLE);


        }}










        private class SongLyrics {
            String title;
            int duration; //check the format of this
            String artistName;
            long id;
            int ranking;
            int explicit;
            boolean search;

            private SongLyrics(boolean search,String title, String artistName) {
                this.search=search;
                this.title = title;
                this.artistName=artistName;
            }
            private SongLyrics(int id,String title, String artistName) {
                this.id=id;
                this.title = title;
                this.artistName=artistName;
            }



            private void setSongTitle(String title) {
                this.title = title;
            }

            private String getSongTitle() {
                return title;
            }

            private void setDuration(int duration) {
                this.duration = duration;
            }

            private int getDuration() {
                return duration;
            }

            private void setArtistName(String artistName) {
                this.artistName = artistName;
            }

            private String getArtistName() {
                return artistName;
            }

            public void setId(long id) {
                this.id = id;
            }

            public long getId() {
                return id;
            }

            public int getRanking(){
                return ranking;
            }
        }



}


