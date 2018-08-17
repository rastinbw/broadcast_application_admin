package com.mahta.rastin.broadcastapplicationadmin.activity.other;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.mahta.rastin.broadcastapplicationadmin.R;

import jp.wasabeef.richeditor.RichEditor;

public class MediaActivity extends AppCompatActivity {

    private RichEditor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        mEditor = findViewById(R.id.media_editor);

        mEditor.setPadding(10, 10, 10, 10);
//        mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");

        mEditor.setPlaceholder("توضیحات");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mEditor.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        }
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {

            }
        });


    }
}
