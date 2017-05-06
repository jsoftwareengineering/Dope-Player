package com.example.jamesb.dopeplayer;


import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class TraditionalActivity extends BaseActivity {

    TextView track;
    TextView time;
    TextView title;
    int textColor = Color.parseColor("#000000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        title = (TextView) findViewById(R.id.toolbar_title);
        time = (TextView) findViewById(R.id.text_timecode);
        track = (TextView) findViewById(R.id.text_trackno);
        title.setTextColor(textColor);
        track.setTextColor(textColor);
        time.setTextColor(textColor);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_traditional;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_traditional;
    }
}
