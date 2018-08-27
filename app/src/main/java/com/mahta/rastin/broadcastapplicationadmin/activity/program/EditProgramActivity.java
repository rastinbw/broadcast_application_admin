package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

public class EditProgramActivity extends AppCompatActivity implements View.OnClickListener {

    private Program program;
    private EditTextPlus edtTitle, edtPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_new);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        program = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        edtTitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);

        edtTitle.setText(program.getTitle());
        edtPreview.setText(program.getPreview());



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
