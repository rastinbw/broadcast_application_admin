package com.mahta.rastin.broadcastapplicationadmin.activity.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.other.HelpActivity;
import com.mahta.rastin.broadcastapplicationadmin.activity.other.MediaActivity;
import com.mahta.rastin.broadcastapplicationadmin.activity.other.PostActivity;
import com.mahta.rastin.broadcastapplicationadmin.activity.other.ProgramActivity;

import jp.wasabeef.richeditor.RichEditor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView announceCard, mediaCard, helpCard, scheduleCard;

    //changing activity font
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        announceCard = findViewById(R.id.announce_card);
        mediaCard = findViewById(R.id.media_card);
        helpCard = findViewById(R.id.help_card);
        scheduleCard = findViewById(R.id.schedules_card);

        announceCard.setOnClickListener(this);
        mediaCard.setOnClickListener(this);
        helpCard.setOnClickListener(this);
        scheduleCard.setOnClickListener(this);

    }

//    @Override
//    public void onBackPressed() {
//
//    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.announce_card:

                intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);

                break;

            case R.id.media_card:

                intent = new Intent(MainActivity.this, MediaActivity.class);
                startActivity(intent);

                break;

            case R.id.schedules_card:

                intent = new Intent(MainActivity.this, ProgramActivity.class);
                startActivity(intent);

                break;

            case R.id.help_card:

                intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);

                break;
        }
    }
}