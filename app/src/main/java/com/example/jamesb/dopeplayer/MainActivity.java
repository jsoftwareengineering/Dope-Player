package com.example.jamesb.dopeplayer;


public class MainActivity extends BaseActivity{


    @Override
    int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_playing;
    }

}
