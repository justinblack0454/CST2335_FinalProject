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

//        Bundle dataToPass = getIntent().getExtras();
//
//        SoccerDetailsFragment highlightFrag = new SoccerDetailsFragment();
//        highlightFrag.setArguments(dataToPass);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLocation, highlightFrag).commit();

//        Intent fromMain = getIntent();
//        url = fromMain.getStringExtra("URL");
//        videoView = (VideoView) findViewById(R.id.videoView);
//        Uri vidUri = Uri.parse(url);
//
//        try {
////            videoView.setVideoURI(vidUri);
////            videoView.setVideoURI(vidUri);
////            videoView.setOnPreparedListener(mediaPlayer -> {
////                videoView.setVisibility(View.VISIBLE);
////                videoView.start();
////            });
////        videoView.requestFocus();
////        videoView.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        videoView.setOnPreparedListener(mediaPlayer ->{
//            videoView.setVisibility(View.VISIBLE);
//                videoView.start();
//
//        });

//        MediaController controller = new MediaController(this);
//        controller.setAnchorView(videoView);
//        controller.setMediaPlayer(videoView);
//        videoView.setMediaController(controller);


//        videoView.setVideoURI(Uri.parse(yourVideoPath));

//        videoView.setOnPreparedListener(mp -> {
//            ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//            float videoWidth = mp.getVideoWidth();
//            float videoHeight = mp.getVideoHeight();
//            float viewWidth = videoView.getWidth();
//            lp.height = (int) (viewWidth * (videoHeight / videoWidth));
//            videoView.setLayoutParams(lp);
////            playVideo();
//            //  if (!videoView.isPlaying()) optional
//            videoView.start();
//        });
//        videoView.setMediaController(new MediaController(getContext()));


//        this was close, but just says "Can't play this video".
//        videoView = findViewById(R.id.videoView);
//        videoView.setVideoPath(url);
//        videoView.start();
    }
}
