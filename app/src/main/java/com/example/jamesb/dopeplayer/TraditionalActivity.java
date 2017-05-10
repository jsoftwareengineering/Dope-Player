package com.example.jamesb.dopeplayer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class TraditionalActivity extends BaseActivity implements
        SpotifyPlayer.NotificationCallback {

    //declare variables
    ImageButton playButton;
    ImageButton nextButton;
    ImageButton previousButton;
    TextView track;
    TextView time;
    TextView title;
    TextView artist;
    TextView song;

    boolean currentlyListening;
    boolean isbuttonsettonPlay;

    //define text color
    int textColor = Color.parseColor("#000000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //define ImageButton views
        playButton = (ImageButton) findViewById(R.id.button_play);
        nextButton = (ImageButton) findViewById(R.id.button_next);
        previousButton = (ImageButton) findViewById(R.id.button_previous);

        //setPlayButton();
        setTraditionalViewSettings();
        setSongAndArtist();

        BaseActivity.mPlayer.addNotificationCallback(TraditionalActivity.this);


        currentlyListening = BaseActivity.mPlayer.getPlaybackState().isPlaying;
        if (currentlyListening) {
            setPauseButton();
        } else {
            setPlayButton();
        }

        //set play button listener
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentlyListening = mPlayer.getPlaybackState().isPlaying;
                if (currentlyListening) {
                    pausePlayback();
                } else {
                    resumePlayback();
                }

            }
        });

        //set next button listener
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeSong("next");
            }
        });

        //set previous button listener
        previousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
        Log.d("control", "setPlayButton-play button set to play");
        playButton.setImageResource(R.drawable.ic_play_arrow_black_48dp);
    }
    private void setPauseButton() {
        Log.d("control", "setPlayButton—play button set to pause");
        playButton.setImageResource(R.drawable.ic_pause_black_48dp);
    }
    private void pausePlayback(){
        BaseActivity.mPlayer.pause(new Player.OperationCallback() {
            @Override
            public void onSuccess() {

                //setPlayButton(false);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }
    private void resumePlayback(){
        BaseActivity.mPlayer.resume(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                //setPlayButton(true);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

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
        artist = (TextView) findViewById(R.id.textViewArtist);
        song = (TextView) findViewById(R.id.textViewSong);
        title.setTextColor(textColor);
        track.setTextColor(textColor);
        time.setTextColor(textColor);
        artist.setTextColor(textColor);
        song.setTextColor(textColor);
    }

    private void setSongAndArtist(){
        Metadata.Track track = BaseActivity.mPlayer.getMetadata().currentTrack;
        String s = track.name;
        String a = track.artistName;
        artist.setText(getResources().getText(R.string.artist) + "  " + a);
        song.setText(getResources().getText(R.string.song) + "  " + s);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {

        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyMetadataChanged: {
                setSongAndArtist();
                Log.d("TraditionalActivity", "Playback event received: kSpPlaybackNotifyMetadataChanged");
            }
            break;
            case kSpPlaybackNotifyPause: {
                setPlayButton();
                Log.d("TraditionalActivity", "Playback event received: kSpPlaybackNotifyPause");
            }
            break;
            case kSpPlaybackNotifyPlay: {
                setPauseButton();
                Log.d("TraditionalActivity", "Playback event received: kSpPlaybackNotifyPlay");
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {

    }
}
