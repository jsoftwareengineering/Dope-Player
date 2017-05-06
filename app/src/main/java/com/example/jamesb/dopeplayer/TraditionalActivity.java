package com.example.jamesb.dopeplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TraditionalActivity extends BaseActivity {

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traditional);
    }
    */

    @Override
    int getContentViewId() {
        return R.layout.activity_traditional;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_traditional;
    }
}
