package com.example.jamesb.dopeplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class PlaylistLauncher extends BaseActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback, Search.View {
    private static final int REQUEST_CODE = 1337;

    private Button playTestButton;
    private Button logoutTestButton;
    private RecordSlider slider;
    public static int index;

    private Search.ActionListener mActionListener;

    private LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    private PlaylistLauncher.ScrollListener mScrollListener = new PlaylistLauncher.ScrollListener(mLayoutManager);
    private SearchResultsAdapter mAdapter;

    public void setIndex(int index) {
        this.index = index;

    }

    private class ScrollListener extends ResultListScrollListener {

        public ScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        public void onLoadMore() {
            mActionListener.loadMoreResults();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_song_list);
        if (mPlayer == null) {
            spotifyLogin();
        } else {
            mActionListener = new SearchPresenter(this, this);
            mActionListener.init(BaseActivity.token);
            mActionListener.getPlaylist();

            // Setup search results list
            mAdapter = new SearchResultsAdapter(this, new SearchResultsAdapter.ItemSelectedListener() {
                @Override
                public void onItemSelected(View itemView, Track item) {
                    mActionListener.selectTrack(item);
                    Log.d("Index", String.valueOf(index));

                    BaseActivity.mPlayer.playUri(null, "spotify:user:hendemic:playlist:4fWo8AAMu5GMnLtAhtPktC", index, 0);
                }
            });

            RecyclerView resultsList = (RecyclerView) findViewById(R.id.search_results);
            resultsList.setHasFixedSize(true);
            resultsList.setLayoutManager(mLayoutManager);
            resultsList.setAdapter(mAdapter);
            resultsList.addOnScrollListener(mScrollListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        mActionListener = new SearchPresenter(this, this);
        mActionListener.init(BaseActivity.token);
        mActionListener.getPlaylist();

        // Setup search results list
        mAdapter = new SearchResultsAdapter(this, new SearchResultsAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item) {
                mActionListener.selectTrack(item);
                Log.d("Index", String.valueOf(index));

                BaseActivity.mPlayer.playUri(null, "spotify:user:hendemic:playlist:4fWo8AAMu5GMnLtAhtPktC", index, 0);
            }
        });

        RecyclerView resultsList = (RecyclerView) findViewById(R.id.search_results);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(mLayoutManager);
        resultsList.setAdapter(mAdapter);
        resultsList.addOnScrollListener(mScrollListener);
        // Check if result comes from the correct activity

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
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

//        BaseActivity.mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
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
    public void reset() {

    }

    @Override
    public void addData(List<Track> items) {
        mAdapter.addData(items);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_song_list;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_queue;
    }
}


