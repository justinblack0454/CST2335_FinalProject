package com.example.cst2335_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SoccerFavourites extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_favourites);

        ListView favouritesList = findViewById(R.id.favouritesList);

    }

//    private class MatchListAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return SoccerActivity.favourites.size();
//        }
//
//        @Override
//        public SoccerActivity.Match getItem(int position) {
//
//            return matches.get(position);
//        }
//
//        @Override
//        //last week we returned (long) position. Now we return the object's database id that we get from line 71
//        public long getItemId(int position) {
//            return matches.get(position).getId();
////            return (long) position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            SoccerActivity.Match match = (SoccerActivity.Match) getItem(position);
//            LayoutInflater inflater = getLayoutInflater();
//            View matchDetailView = inflater.inflate(R.layout.match_details, parent, false);
//
//            TextView matchInfo = (TextView) matchDetailView.findViewById(R.id.matchInfo);
//            matchInfo.setText(getItem(position).getTitle());
//
//            TextView dateInfo = (TextView) matchDetailView.findViewById(R.id.dateInfo);
//            dateInfo.setText(getItem(position).getDate());
//
//            TextView team1Info = (TextView) matchDetailView.findViewById(R.id.team1Info);
//            team1Info.setText(getItem(position).getTeam1());
//
//            TextView team2Info = (TextView) matchDetailView.findViewById(R.id.team2Info);
//            team2Info.setText(getItem(position).getTeam2());
//
//            return matchDetailView;
//        }
}