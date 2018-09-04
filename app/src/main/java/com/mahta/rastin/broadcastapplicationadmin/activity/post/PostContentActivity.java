package com.mahta.rastin.broadcastapplicationadmin.activity.post;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Post;

import io.realm.Realm;

public class PostContentActivity extends AppCompatActivity implements View.OnClickListener {

    private Post currentPost;
    private LinearLayout lnlLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post);

        currentPost = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);


        setupToolbar();

        lnlLoading = findViewById(R.id.lnlLoading);
        WebView wbvContent = findViewById(R.id.wbvContent);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        String html = "<div dir='rtl'>" + currentPost.getContent() + "</div>";

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
        findViewById(R.id.imgDelete).setOnClickListener(this);

        ((TextViewPlus)findViewById(R.id.txtTitle)).setText(currentPost.getTitle());

    }

    @Override
    public void onClick(View v) {

        int id  = v.getId();

        switch (id) {

            case R.id.imgBack:
                finish();
                break;

            case R.id.imgEdit:
                Intent intent = new Intent(PostContentActivity.this, EditPostActivity.class);
                intent.putExtra(Keys.KEY_EXTRA_FLAG, currentPost);
                startActivity(intent);

                break;

            case R.id.imgDelete:

                ContentValues contentValues = new ContentValues();

                contentValues.put("token", RealmController.getInstance().getUserToken().getToken());

                new HttpCommand(HttpCommand.COMMAND_DELETE_POST, contentValues, currentPost.getId()+"" ).setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        if (JSONParser.getResultCodeFromJson(result) == 1000){

                            G.toastShort("اطلاعیه با موفقیت حذف شد", PostContentActivity.this);

                            finish();
                        }
                    }
                }).execute();

                break;

        }



    }
}
