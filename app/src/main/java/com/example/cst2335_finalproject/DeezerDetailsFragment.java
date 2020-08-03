package com.example.cst2335_finalproject;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.cst2335_finalproject.DeezerActivity.Song;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeezerDetailsFragment extends Fragment {

    DeezerDB db;
    private Bundle dataFromActivity;
    ArrayList<Song> tracklist = null;
    Bitmap bm;
    List<Bitmap> albumsCovers = new ArrayList<>();
    private long id;
    int isSend;
    private AppCompatActivity parentActivity;
    TrackListAdapter adapter;



    public DeezerDetailsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DeezerDB(getContext());
        db.getWritableDatabase();
        tracklist = db.getAll();

        dataFromActivity = getArguments();

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_deezer_details, container, false);

        //show the message
        TextView message = (TextView)result.findViewById(R.id.fragmessage);
        message.setText("Your Favourites:");

        //favsView
        ListView favsView = (ListView)result.findViewById(R.id.favView);
        favsView.setAdapter(adapter = new TrackListAdapter());

        favsView.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            Song song = tracklist.get(pos);

            alertDialogBuilder.setTitle("Here are some extra details: ")

                    //What is the message:
                    .setMessage("The selected row is: " + pos + "\n\n" +
                            "Song title: " + song.getSongTitle() + "\n\n" +
                            "Song duration: " + song.getDuration() + "\n\n" +
                            "Album title: " + song.getAlbumTitle() + "\n\n" +
                            "Album cover: " + song.getAlbumCover() //generate albumView xml to display this cover actually
                    )

                    .setNegativeButton("Delete song", (click, arg) -> {
                        db.deleteSong(song);
                        //albumsCovers.remove(pos);
                        tracklist = db.getAll();
                        adapter.notifyDataSetChanged();


                    })

                    //Show the dialog
                    .create().show();

            return true;
        });

        // button hides the favourites fragment
        Button hideButton = (Button)result.findViewById(R.id.hideButton);
        hideButton.setOnClickListener(btn -> {
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            getActivity().finish();
        });

        return result;
    }



    private class TrackListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tracklist.size();
        }

        @Override
        public Object getItem(int position) {
            return tracklist.get(position);
        }

        @Override
        //last week we returned (long) position. Now we return the object's database id that we get from line 71
        public long getItemId(int position)
        {
            return  tracklist.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DeezerActivity.Song song = (DeezerActivity.Song) getItem(position);
            //Bitmap cover = albumsCovers.get(position);
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.searchedsong, parent, false);

            TextView songInfo = newView.findViewById(R.id.songDetails);
            songInfo.setText(song.getSongTitle());

//            ImageView coverInfo = newView.findViewById((R.id.albumImage));
//            coverInfo.setImageBitmap(cover);

            return newView;
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }

}
