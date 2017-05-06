package com.example.jamesb.dopeplayer;


import android.os.Bundle;
import android.widget.RelativeLayout;

public class MainActivity extends BaseActivity{

    RelativeLayout layout;

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
