package com.example.jamesb.dopeplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.SpotifyPlayer;

/*
 * Mike, 05-MAY-2017
 * This base activity provides the navigation functionality that all other activities will inherit.
 * Derived from http://stackoverflow.com/questions/41744219/bottomnavigationview-between-activities
 */

public abstract class BaseActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, SpotifyPlayer.NotificationCallback {
    protected BottomNavigationView navigationView;
    public static Player mPlayer;
    public static String token;
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

    abstract int getContentViewId();
    abstract int getNavigationMenuItemId();

}
