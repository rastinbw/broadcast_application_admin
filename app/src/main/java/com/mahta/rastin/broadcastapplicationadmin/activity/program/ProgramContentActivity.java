package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.DeleteDialog;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDialogDeleteListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
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

        String html = "<div>" + currentProgram.getContent() + "</div>";

        wbvContent.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                lnlLoading.setVisibility(View.GONE);
            }
        });

//        wbvContent.getSettings().setUseWideViewPort(true);
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

            finish();

        } else if (id == R.id.imgDelete) {

            DeleteDialog deleteDialog = new DeleteDialog(ProgramContentActivity.this);

            deleteDialog.setOnDeleteListener(new OnDialogDeleteListener() {
                @Override
                public void onConfirmDeleteItem(boolean confirm) {

                    if (confirm) {

                        ContentValues contentValues = new ContentValues();

                        contentValues.put("token", RealmController.getInstance().getUserToken().getToken());

                        new HttpCommand(HttpCommand.COMMAND_DELETE_PROGRAM, contentValues, currentProgram.getId() + "").setOnResultListener(new OnResultListener() {
                            @Override
                            public void onResult(String result) {
                                if (JSONParser.getResultCodeFromJson(result) == 1000) {

                                    G.toastShort("برنامه با موفقیت حذف شد", ProgramContentActivity.this);

                                    finish();
                                }
                            }
                        }).execute();
                    }
                }
            });
            deleteDialog.show();

        }
    }
}
