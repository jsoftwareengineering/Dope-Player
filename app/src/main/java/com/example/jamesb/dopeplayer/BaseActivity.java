package com.example.jamesb.dopeplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

/*
 * Mike, 05-MAY-2017
 * This base activity provides the navigation functionality that all other activities will inherit.
 * Derived from http://stackoverflow.com/questions/41744219/bottomnavigationview-between-activities
 */

public abstract class BaseActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, SpotifyPlayer.NotificationCallback,
        ConnectionStateCallback {
    protected BottomNavigationView navigationView;
    public static Player mPlayer;
    public static String token;
    public static final int REQUEST_CODE = 1337;
    public static Player.NotificationCallback notificationCallback;
    TextView artist;
    TextView song;
    /*public static Player.OperationCallback mOperationCallback= = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            Log.d("Callback", "OperationCallback success");
        }

        @Override
        public void onError(Error error) {
            Log.d("Callback", "OperationCallack error: " + error);
        }
    };*/

    //on create, set the view and add listener to bottom navigation bar
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());


        //set listener for bottom navigation bar by id
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        artist = (TextView) findViewById(R.id.textViewArtist);
        song = (TextView) findViewById(R.id.textViewSong);
    }

    //on start, update the navigation bar state
    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    // Remove transition between activities to avoid screen tossing on tapping bottom navigation item
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationView.postDelayed(() -> {

            int itemId = item.getItemId();

            if (itemId == R.id.menu_traditional) {
                startActivity(new Intent(this, TraditionalActivity.class));
            } else if (itemId == R.id.menu_playing) {
                startActivity(new Intent(this, MainActivity.class));
            }
            else if ((itemId == R.id.menu_queue)) {
                startActivity(new Intent(this, PlaylistLauncher.class));

            }
            //else if (itemId == R.id.menu_queue) {
            //startActivity(new Intent(this, NotificationsActivity.class));
            //}
            finish();
        }, 300);
        return true;
    }



    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked = item.getItemId() == itemId;
            if (shouldBeChecked) {
                item.setChecked(true);
                break;
            }
        }
    }

    public void spotifyLogin() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SpotifyConstants.cID,
                AuthenticationResponse.Type.TOKEN,
                SpotifyConstants.cRedirectURI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    public void onTemporaryError() {
        Log.d("TraditionalActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onLoggedIn() {
        Log.d("SpotifyConnect", "User logged in");

        BaseActivity.mPlayer.playUri(null, "spotify:user:hendemic:playlist:4fWo8AAMu5GMnLtAhtPktC", 0, 0);

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
    public void onPlaybackError(Error error) {

    }


    public void setSongAndArtist(){
        Metadata.Track track = BaseActivity.mPlayer.getMetadata().currentTrack;
        String s = track.name;
        String a = track.artistName;
        artist.setText(getResources().getText(R.string.artist) + "  " + a);
        song.setText(getResources().getText(R.string.song) + "  " + s);
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
                        BaseActivity.mPlayer.addConnectionStateCallback(BaseActivity.this);
                        BaseActivity.mPlayer.addNotificationCallback(BaseActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("BaseActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    abstract int getContentViewId();
    abstract int getNavigationMenuItemId();

}
