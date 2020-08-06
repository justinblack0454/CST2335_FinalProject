package com.example.cst2335_finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335_finalproject.SoccerActivity.Match;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SoccerDetailsFragment extends Fragment {

    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;
    private String title;
    private String date;
    private String team1;
    private String team2;
    private String url;
    ArrayList<Match> favourites = null;
    MatchListAdapter matchAdapter;

    MySoccerOpener db;

    public SoccerDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = new MySoccerOpener(getContext());
        db.getWritableDatabase();
        favourites = db.getAll();

        dataFromActivity = getArguments();

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.soccer_fragment_details, container, false);

        TextView message = (TextView) result.findViewById(R.id.titleFavourites);

        ListView listOfFavourites = (ListView) result.findViewById(R.id.favouritesList);
        listOfFavourites.setAdapter(matchAdapter = new MatchListAdapter());

        listOfFavourites.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            Match longSelectedMatch = favourites.get(position);
            alertDialogBuilder.setTitle(longSelectedMatch.getTitle() + "\n" + R.string.soccerRemoveFav);

            //what is the message:
            alertDialogBuilder.setMessage(R.string.soccerDateIs + longSelectedMatch.getDate() + "\n\n" + R.string.team1Is + longSelectedMatch.getTeam1()
                    + "\n\n" + R.string.team2Is + longSelectedMatch.getTeam2());

            //What the yes button does
            alertDialogBuilder.setPositiveButton(R.string.soccerYes, (click, arg) -> {
                removeMatch(longSelectedMatch);
                favourites.remove(position);
                matchAdapter.notifyDataSetChanged();
                //add toast or snack bar here perhaps

                Toast.makeText(getActivity(), R.string.soccerRemovedFavs, Toast.LENGTH_SHORT).show();

            });
            //What the no button does:
            alertDialogBuilder.setNegativeButton(R.string.soccerNo, (click, arg) -> {
            });
            //Show the dialog:
            alertDialogBuilder.create().show();

            return true;
        });

        return inflater.inflate(R.layout.soccer_fragment_details, container, false);
    }

    protected void removeMatch(Match match) {
        db.removeMatch(match);
    }


    private class MatchListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return favourites.size();
        }

        @Override
        public Match getItem(int position) {
            return favourites.get(position);
        }

        @Override
        //last week we returned (long) position. Now we return the object's database id that we get from line 71
        public long getItemId(int position) {
            return favourites.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            Match match = (Match) getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View matchDetailView = inflater.inflate(R.layout.match_details, parent, false);

            TextView matchInfo = matchDetailView.findViewById(R.id.matchInfo);
            matchInfo.setText(getItem(position).getTitle());

            TextView dateInfo = matchDetailView.findViewById(R.id.dateInfo);
            dateInfo.setText(getItem(position).getDate());

            TextView team1Info = matchDetailView.findViewById(R.id.team1Info);
            team1Info.setText(getItem(position).getTeam1());

            TextView team2Info = matchDetailView.findViewById(R.id.team2Info);
            team2Info.setText(getItem(position).getTeam2());

            return matchDetailView;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }


}
