package com.example.cst2335_finalproject;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    public DetailsFragment() {
        // Required empty public constructor
    }
    private Bundle dataFromActivity;
    TextView messagehere;
    TextView id;

    private AppCompatActivity parentActivity;
    Button lyricsit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        long id1;
        String lyrics1;
        Button hide;
        dataFromActivity = getArguments();
        id1 = dataFromActivity.getLong(SongLyricsActivity.ITEM_ID );

        View result =  inflater.inflate(R.layout.frament_lyricsdetails, container, false);
        String url = "https://api.lyrics.ovh/v1/" + dataFromActivity.getString(SongLyricsActivity.ITEM_ARTIST) + "/" + dataFromActivity.getString(SongLyricsActivity.ITEM_TITLE);
        messagehere=(TextView) result.findViewById(R.id.messagehere);
        messagehere.setText("Your Favourite song Details is here!\n"+dataFromActivity.getString(SongLyricsActivity.ITEM_TITLE)+" by "+dataFromActivity.getString(SongLyricsActivity.ITEM_ARTIST));
        id=(TextView) result.findViewById(R.id.id);
        id.setText("ID=" + id1);




        //setArguments(dataFromActivity);
        lyricsit= (Button)result.findViewById(R.id.lyricsit);
        lyricsit.setOnClickListener( clk -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://api.lyrics.ovh/v1/" + dataFromActivity.getString(SongLyricsActivity.ITEM_ARTIST) + "/" + dataFromActivity.getString(SongLyricsActivity.ITEM_TITLE)));
            intent.toString();
            startActivity(intent);

                });
        hide= (Button)result.findViewById(R.id.hide);
        hide.setOnClickListener( clk -> {
            Toast.makeText(parentActivity.getApplicationContext(),
                    "Song Details Hidden",
                    Toast.LENGTH_LONG).show();
            //Tell the parent activity to remove
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        });
        // Inflate the layout for this fragment
        return result;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}

