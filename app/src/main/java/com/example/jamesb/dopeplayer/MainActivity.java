package com.example.jamesb.dopeplayer;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
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
    private RecordSlider slider;
    private ImageView recordImageView;
    private GifDrawable recordGif;
    private Drawable recordImage;
    private boolean touchedRecord;
    private double angle;
    private double previousAngle;
    private double degreesMovedSincePress;
    private static boolean listenerSet = false;
    int count = 0;
    int seekToMS = 0;
    long pausePosition;
    boolean wasPlaying;

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

        if(mPlayer == null) {
            spotifyLogin();
        } else {
            mPlayer.removeNotificationCallback(notificationCallback);
            notificationCallback = MainActivity.this;
            BaseActivity.mPlayer.addNotificationCallback(notificationCallback);
            setSongAndArtist();
            time.post(mUpdateTime);
        }

        recordSetup();
    }



    @Override
    protected void onDestroy() {

        //Spotify.destroyPlayer(this);
        super.onDestroy();
    }


    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyMetadataChanged: {
                setSongAndArtist();
            }
            break;
            case kSpPlaybackNotifyPause: {
                //Log.d("MainActivity", "Playback event received: kSpPlaybackNotifyPause");
                recordImageView.setImageDrawable(recordImage);
                time.post(mUpdateTime);
            }
            break;
            case kSpPlaybackNotifyPlay: {
                //Log.d("MainActivity", "Playback event received: kSpPlaybackNotifyPlay");
                recordGif.seekToFrame(0);
                recordImageView.setImageDrawable(recordGif);
                time.post(mUpdateTime);
            }
            break;
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

    private void recordSetup() {
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

        if(mPlayer != null && mPlayer.getPlaybackState().isPlaying) {
            recordImageView.setImageDrawable(recordGif);
        }

        recordImageView.setDrawingCacheEnabled(true);

        recordImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN: {
                        Bitmap bmp = Bitmap.createBitmap(recordImageView.getDrawingCache());
                        int touchX = (int) motionEvent.getX();
                        int touchY = (int) motionEvent.getY();
                        int color = bmp.getPixel(touchX, touchY);
                        touchedRecord = (color != Color.TRANSPARENT);

                        if(touchedRecord) {
                            //check to see if center area was pressed
                            recordImageView.measure(recordImageView.getMeasuredWidth(),
                                    recordImageView.getMeasuredHeight());
                            int height = recordImageView.getHeight();
                            int width = recordImageView.getWidth();
                            int rX = (int) recordImageView.getX();
                            int rY = (int) recordImageView.getY();
                            int centerX = (rX + width) / 2;
                            int centerY = (rY + height) / 2;
                            int touchDist = width / 10;

                            if(touchX > centerX - touchDist * 2 && touchX < centerX + touchDist &&
                                    touchY > centerY - touchDist * 2 && touchY < centerY + touchDist) {
                                count++;
                                Log.d("touch", "center touch " + count);
                                touchedRecord = false;
                                if(mPlayer == null) {
                                    spotifyLogin();
                                } else {
                                    if (mPlayer.getPlaybackState().isPlaying) {
                                        mPlayer.pause(null);
                                    } else {
                                        mPlayer.resume(null);
                                    }
                                }
                            } else {
                                //recordImageView.setImageDrawable(recordImage);
                                degreesMovedSincePress = 0;
                                slider.onTouchEventCustom(motionEvent, touchedRecord);
                                wasPlaying = mPlayer.getPlaybackState().isPlaying;
                                BaseActivity.mPlayer.pause(new Player.OperationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        pausePosition = BaseActivity.mPlayer.getPlaybackState().positionMs;
                                    }

                                    @Override
                                    public void onError(Error error) {

                                    }
                                });
                            }
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

                            //need to implement a check for valid position with regard to playlist
                            //and track
                            if(seekToMS < 0) {
                                //previous track
                                mPlayer.skipToPrevious(null);
                            } else if(seekToMS > mPlayer.getMetadata().currentTrack.durationMs) {
                                //next track
                                mPlayer.skipToNext(null);
                            } else {
                                BaseActivity.mPlayer.seekToPosition(null, seekToMS);
                                if(wasPlaying) {
                                    mPlayer.resume(null);
                                }
                            }

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

                //this math makes it 20 seconds per full rotation
                seekToMS = (int) (pausePosition + degreesMovedSincePress / .018);
                if(seekToMS > mPlayer.getMetadata().currentTrack.durationMs) {
                    time.setText(R.string.next);
                } else if(seekToMS < 0) {
                    time.setText(R.string.previous);
                } else {
                    updateTimeText(seekToMS);
                }

            }
        });
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_playing;
    }

    private void addNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent switchIntent = new Intent(getApplicationContext(), closeButtonListener.class);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                switchIntent, 0);

        Intent pauseIntent = new Intent(getApplicationContext(), PauseSong.class);
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(this, 0,
                pauseIntent, 0);

        Intent resumeIntent = new Intent(getApplicationContext(), resumeButtonListener.class);
        PendingIntent pendingResumeIntent = PendingIntent.getBroadcast(this, 0,
                resumeIntent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.record_control)
                        .setContentTitle("Dope Player")
                        .setContentText("")
                        .addAction(R.drawable.ic_play_arrow_black_24dp, "Pause", pendingPauseIntent)
                        .addAction(R.drawable.ic_pause_black_48dp, "Resume", pendingResumeIntent)
                        .addAction(R.drawable.ic_pause_black_48dp, "Close", pendingSwitchIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
    @Override
    public void onStop() {
        addNotification();
        super.onStop();

    }

    public static class closeButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //BaseActivity.mPlayer.destroy();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
            Spotify.destroyPlayer(this);
            System.exit(0);
        }
    }

    public static class resumeButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Here", "I am here");
            //BaseActivity.mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
            Player.OperationCallback operationCallback = new Player.OperationCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Error error) {

                }
            };
//            BaseActivity.mPlayer.pause(operationCallback);
            BaseActivity.mPlayer.resume(operationCallback);
        }
    }

    public static class PauseSong extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Here", "I am here");
            // BaseActivity.mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
            Player.OperationCallback operationCallback = new Player.OperationCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Error error) {

                }
            };
            BaseActivity.mPlayer.pause(operationCallback);
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
                        BaseActivity.mPlayer.addConnectionStateCallback(MainActivity.this);
                        notificationCallback = MainActivity.this;
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
