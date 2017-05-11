package com.example.jamesb.dopeplayer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
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

        if(mPlayer == null) {
            spotifyLogin();
        } else {
            setSongAndArtist();
            setUpControls();
            mPlayer.removeNotificationCallback(notificationCallback);
            notificationCallback = TraditionalActivity.this;
            BaseActivity.mPlayer.addNotificationCallback(notificationCallback);
            time.post(mUpdateTime);
            setTrackNumber();
        }
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_traditional;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_traditional;
    }

    private void setUpControls() {

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
                time.post(mUpdateTime);
            }
            break;
            case kSpPlaybackNotifyPlay: {
                setPauseButton();
                Log.d("TraditionalActivity", "Playback event received: kSpPlaybackNotifyPlay");
                setUpControls();
                time.post(mUpdateTime);
            }
            break;
            case kSpPlaybackNotifyTrackChanged: {
                setTrackNumber();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                BaseActivity.token = response.getAccessToken();
                Config playerConfig = new Config(this, response.getAccessToken(), SpotifyConstants.cID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        BaseActivity.mPlayer = spotifyPlayer;
                        BaseActivity.mPlayer.addConnectionStateCallback(TraditionalActivity.this);
                        notificationCallback = TraditionalActivity.this;
                        BaseActivity.mPlayer.addNotificationCallback(notificationCallback);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("BaseActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }
}
