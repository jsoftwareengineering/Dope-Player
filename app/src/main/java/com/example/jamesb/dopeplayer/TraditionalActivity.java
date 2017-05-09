package com.example.jamesb.dopeplayer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;

public class TraditionalActivity extends BaseActivity {

    //declare variables
    ImageButton playButton;
    ImageButton nextButton;
    ImageButton previousButton;
    TextView track;
    TextView time;
    TextView title;
    //playCheck playUIthread;
    boolean currentlyListening;

    //define text color
    int textColor = Color.parseColor("#000000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //define ImageButton views
        playButton = (ImageButton) findViewById(R.id.button_play);
        nextButton = (ImageButton) findViewById(R.id.button_next);
        previousButton = (ImageButton) findViewById(R.id.button_previous);

        setPlayButton();
        setTraditionalViewSettings();

        //playUIthread = new playCheck();
        //playUIthread.start();

        //set play button listener
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentlyListening = mPlayer.getPlaybackState().isPlaying;
                if(currentlyListening){
                    pausePlayback();
                } else{
                    resumePlayback();
                }

            }
        });

        //set next button listener
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                changeSong("next");
            }
        });

        //set previous button listener
        previousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                changeSong("previous");
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
    private void setPlayButton(){

        //if currently playing, change playButton to pause icon
        if(mPlayer.getPlaybackState().isPlaying){
            //set button image to pause
            playButton.setImageResource(R.drawable.ic_pause_black_48dp);
        } else{
            playButton.setImageResource(R.drawable.ic_play_arrow_black_48dp);
        }

    }
    private void pausePlayback(){
        BaseActivity.mPlayer.pause(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                setPlayButton();
            }

            @Override
            public void onError(Error error) {
                Log.d("Playback", "Error pausing Spotify player");
            }
        });
    }
    private void resumePlayback(){
        BaseActivity.mPlayer.resume(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                setPlayButton();
            }

            @Override
            public void onError(Error error) {
                Log.d("Playback", "Error playing Spotify player");
            }
        });
    }
/*
    private class playCheck extends Thread {

        boolean threadActive;

        public void onStart(){
            threadActive = true;
        }

        public void run() {

            boolean playBackState;

            try {
                while(threadActive) {
                    setPlayButton();
                    playBackState = BaseActivity.mPlayer.getPlaybackState().isPlaying;
                    System.out.println("playback state is:  " + String.valueOf(playBackState));
                    wait(1000);
                }
            } catch (InterruptedException e) {
                Log.e("thread", "Thread interupted");
                threadActive = false;
            }
        }
    }
*/
    private void changeSong(String direction){
        if (direction.toLowerCase() == "next") {
            BaseActivity.mPlayer.skipToNext(new Player.OperationCallback(){
                public void onSuccess() {
                    Log.d("Playback", "Next message sent to Spotify player");
                }

                @Override
                public void onError(Error error) {
                    Log.d("Playback", "Error skip to next in Spotify player");
                }

            });
        } else if(direction.toLowerCase()=="previous") {
            BaseActivity.mPlayer.skipToPrevious(new Player.OperationCallback(){
                public void onSuccess() {
                    Log.d("Playback", "Previous message sent to Spotify player");
                }

                @Override
                public void onError(Error error) {
                    Log.d("Playback", "Error skip to previous in Spotify player");
                }

            });
        } else {
            Log.e("Command", "invalid command used");
        }

    }
    private void setTraditionalViewSettings(){
        title = (TextView) findViewById(R.id.toolbar_title);
        time = (TextView) findViewById(R.id.text_timecode);
        track = (TextView) findViewById(R.id.text_trackno);
        title.setTextColor(textColor);
        track.setTextColor(textColor);
        time.setTextColor(textColor);
    }
}
