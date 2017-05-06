package com.example.jamesb.dopeplayer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class TraditionalActivity extends BaseActivity {

    //declare variables
    ImageButton playButton;
    ImageButton nextButton;
    ImageButton previousButton;
    TextView track;
    TextView time;
    TextView title;
    boolean currentlyListening;

    //define text color
    int textColor = Color.parseColor("#000000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //check if spotify is currently playing
        //set currentlyListening here with that test
        currentlyListening = true;

        //set play-pause button graphic based on playback state
        playButton = (ImageButton) findViewById(R.id.button_play);
        nextButton = (ImageButton) findViewById(R.id.button_next);
        previousButton = (ImageButton) findViewById(R.id.button_previous);
        setPlayButton(currentlyListening);


        //change color of text in base layout for the white background
        title = (TextView) findViewById(R.id.toolbar_title);
        time = (TextView) findViewById(R.id.text_timecode);
        track = (TextView) findViewById(R.id.text_trackno);
        title.setTextColor(textColor);
        track.setTextColor(textColor);
        time.setTextColor(textColor);

        //set buttonListeners
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(currentlyListening){
                    currentlyListening = false;
                } else {
                    currentlyListening = true;
                }

                setPlayButton(currentlyListening);
                changeSpotifyPlayback(currentlyListening);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                //send Spotify next message
                Log.d("Playback", "Next message to Spotify");
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                //send Spotify previous message
                Log.d("Playback", "Previous message to Spotify");
            }
        });
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_traditional;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_traditional;
    }

    //set play/pause button icon depending on playback state
    private void setPlayButton(boolean playBackState){

        //if currently playing, change playButton to pause icon
        if(playBackState){
            //set button image to pause
            playButton.setImageResource(R.drawable.ic_pause_black_48dp);
        } else{
            playButton.setImageResource(R.drawable.ic_play_arrow_black_48dp);
        }

    }

    private void changeSpotifyPlayback(boolean playBackState){
        if(playBackState) {
            //send play message
            Log.d("Playback", "Play message sent to Spotify SDK");

        } else{
            //send pause message
            Log.d("Playback", "Pause message sent to Spotify SDK");
        }
    }

}
