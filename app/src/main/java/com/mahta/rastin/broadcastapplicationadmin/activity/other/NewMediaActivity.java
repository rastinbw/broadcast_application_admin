package com.mahta.rastin.broadcastapplicationadmin.activity.other;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;

public class NewMediaActivity extends AppCompatActivity implements View.OnClickListener {

    private Media media;
    private EditTextPlus edtTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_new);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        edtTitle = findViewById(R.id.edt_title);








    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {
            Toast.makeText(this, "ثبت", Toast.LENGTH_SHORT).show();
        }
    }
}
