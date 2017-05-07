package com.example.jamesb.dopeplayer;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class MainActivity extends BaseActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;
    private RecordSlider slider;
    private ImageView recordImageView;
    private GifDrawable recordGif;
    private Drawable recordImage;
    private boolean touchedRecord;
    private double angle;
    private double previousAngle;
    private double degreesMovedSincePress;

    RelativeLayout layout;
    TextView textViewArtist;
    TextView textViewSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = (RelativeLayout) findViewById(R.id.background);
        layout.setBackgroundResource(R.drawable.background_gradient);
        textViewArtist = (TextView) findViewById(R.id.textViewArtist);
        textViewSong = (TextView) findViewById(R.id.textViewSong);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SpotifyConstants.cID,
                AuthenticationResponse.Type.TOKEN,
                SpotifyConstants.cRedirectURI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        slider = (RecordSlider) findViewById(R.id.slider);
        recordImageView = (ImageView) findViewById(R.id.gifImageViewRecord);
        touchedRecord = false;
        recordImage = getDrawable(R.drawable.record_control);
        angle = 0;

        try {
            recordGif = new GifDrawable(getResources(), R.raw.record_control_gif);
            recordGif.setSpeed(2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordImageView.setDrawingCacheEnabled(true);




        recordImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {



                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN: {
                        Bitmap bmp = Bitmap.createBitmap(recordImageView.getDrawingCache());
                        int color = bmp.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());
                        touchedRecord = (color != Color.TRANSPARENT);
                        if(touchedRecord) {
                            recordImageView.setImageDrawable(recordImage);
                            degreesMovedSincePress = 0;
                            slider.onTouchEventCustom(motionEvent, touchedRecord);
                        }
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if(touchedRecord) {
                            slider.onTouchEventCustom(motionEvent, touchedRecord);

                            //first two ifs account for when the slider goes from 360 to 1 or otherwise
                            if(angle > 340 && previousAngle < 20) {
                                degreesMovedSincePress += (360 - angle) - previousAngle;
                            } else if(previousAngle > 340 && angle < 20) {
                                degreesMovedSincePress += angle - (360 - previousAngle);
                            } else {
                                degreesMovedSincePress += angle - previousAngle ;
                            }
                            Log.d("degrees", degreesMovedSincePress + "");
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if(touchedRecord) {
                            long position = mPlayer.getPlaybackState().positionMs;
                            //need to implement a check for valid position with regard to playlist
                            //and track
                            mPlayer.seekToPosition(new Player.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    recordImageView.setImageDrawable(recordGif);
                                    recordGif.seekToFrame(0);
                                }

                                @Override
                                public void onError(Error error) {

                                }
                            }, (int) (position + degreesMovedSincePress / .018));
                            touchedRecord = false;
                        }

                        slider.onTouchEventCustom(motionEvent, touchedRecord);
                        break;
                    }

                }

                return true;
            }
        });


        slider.setOnSliderMovedListener(new RecordSlider.OnSliderMovedListener() {
            @Override
            public void onSliderMoved(double pos) {
                previousAngle = angle;
                angle = slider.getmStartAngle() + pos * 2 * Math.PI;
                angle = angle + Math.PI;
                angle = angle * 180 / Math.PI;
                angle = 360 - angle;
                Log.d("test", "slider position: " + angle);
                //Log.d("test", "slider start position: " + slider.getmStartAngle());
                //Log.d("test", "pos: " + pos);


                Animation a = new RotateAnimation( (float)previousAngle, (float)angle,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                a.setRepeatCount(-1);
                a.setDuration(10);
                recordImageView.startAnimation(a);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), SpotifyConstants.cID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyMetadataChanged: {
                Metadata.Track track = mPlayer.getMetadata().currentTrack;
                String song = track.name;
                String artist = track.artistName;
                textViewArtist.setText(getResources().getText(R.string.artist) + "  " + artist);
                textViewSong.setText(getResources().getText(R.string.song) + "  " + song);
                recordImageView.setImageDrawable(recordGif);
            }
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("SpotifyConnect", "User logged in");

        mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);

    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login Failed: " + error.toString());
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_playing;
    }

}
