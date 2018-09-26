package com.mahta.rastin.broadcastapplicationadmin.activity.main;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.media.MediaListActivity;
import com.mahta.rastin.broadcastapplicationadmin.activity.message.MessageListActivity;
import com.mahta.rastin.broadcastapplicationadmin.activity.post.PostListActivity;
import com.mahta.rastin.broadcastapplicationadmin.activity.program.ProgramListActivity;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.G;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting tollbar title
        ((TextViewPlus) findViewById(R.id.txtTitle)).setText("داشبورد");

        findViewById(R.id.announce_card).setOnClickListener(this);
        findViewById(R.id.media_card).setOnClickListener(this);
        findViewById(R.id.help_card).setOnClickListener(this);
        findViewById(R.id.schedules_card).setOnClickListener(this);
        findViewById(R.id.users_card).setOnClickListener(this);
        findViewById(R.id.message_card).setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        G.toastShort(G.getStringFromResource(
                R.string.click_twice,
                getApplicationContext()),
                getApplicationContext()
        );

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2200);
    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.announce_card:

                intent = new Intent(MainActivity.this, PostListActivity.class);
                startActivity(intent);

                break;

            case R.id.media_card:

                intent = new Intent(MainActivity.this, MediaListActivity.class);
                startActivity(intent);

                break;

            case R.id.schedules_card:

                intent = new Intent(MainActivity.this, ProgramListActivity.class);
                startActivity(intent);

                break;

            case R.id.help_card:

                intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);

                break;

            case R.id.message_card:

                intent = new Intent(MainActivity.this, MessageListActivity.class);
                startActivity(intent);

                break;

            case R.id.users_card:

                intent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(intent);

                break;
        }
    }
}