package com.example.jamesb.dopeplayer;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.SpotifyPlayer;

import pl.droidsonroids.gif.GifDrawable;

public class MainActivity extends BaseActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final int REQUEST_CODE = 1337;

    private Button playTestButton;
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
    Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = (RelativeLayout) findViewById(R.id.background);
        layout.setBackgroundResource(R.drawable.background_gradient);

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
