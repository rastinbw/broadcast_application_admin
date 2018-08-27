package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.post.EditPostActivity;
import com.mahta.rastin.broadcastapplicationadmin.activity.post.PostContentActivity;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

public class ProgramContentActivity extends AppCompatActivity implements View.OnClickListener {

    private Program currentProgram;
    private LinearLayout lnlLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_content);

        currentProgram = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        setupToolbar();

        lnlLoading = findViewById(R.id.lnlLoading);
        WebView wbvContent = findViewById(R.id.wbvContent);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        String html = "<div dir='rtl'>" + currentProgram.getContent() + "</div>";

        wbvContent.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                lnlLoading.setVisibility(View.GONE);
            }
        });

        wbvContent.getSettings().setLoadWithOverviewMode(true);
        wbvContent.getSettings().setBuiltInZoomControls(true);
        wbvContent.getSettings().setDisplayZoomControls(false);
        wbvContent.loadDataWithBaseURL("", html, mimeType, encoding, "");

    }

    void setupToolbar(){

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.imgEdit).setOnClickListener(this);
        findViewById(R.id.imgDelete).setOnClickListener(this);

        ((TextViewPlus)findViewById(R.id.txtTitle)).setText(currentProgram.getTitle());

        Log.i("MYTAG", currentProgram.getContent());

    }

    @Override
    public void onClick(View v) {

        int id  = v.getId();

        if (id == R.id.imgBack) {
            finish();

        } else if (id == R.id.imgEdit) {

            Intent intent = new Intent(ProgramContentActivity.this, EditProgramActivity.class);
            intent.putExtra(Keys.KEY_EXTRA_FLAG, currentProgram);
            startActivity(intent);

        } else if (id == R.id.imgDelete) {

        }
    }
}
