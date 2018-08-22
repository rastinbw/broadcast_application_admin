package com.mahta.rastin.broadcastapplicationadmin.activity.other;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;

import jp.wasabeef.richeditor.RichEditor;

public class EditMediaActivity extends AppCompatActivity {

    private Media media;
    private EditTextPlus edtTitle, edtDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_new);

        edtTitle = findViewById(R.id.edt_title);
        edtDesc = findViewById(R.id.edt_desc);

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextViewPlus) findViewById(R.id.txtTitle)).setText("بازگشت");

        media = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        edtTitle.setText(media.getTitle());
        edtDesc.setText(media.getDescription());





    }
}
