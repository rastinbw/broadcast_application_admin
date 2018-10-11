package com.mahta.rastin.broadcastapplicationadmin.activity.media;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;


import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.controller.DialogSelectionListener;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.model.DialogConfigs;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.model.DialogProperties;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.view.FilePickerDialog;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.helper.Utils;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;

import java.io.File;

public class NewMediaActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextPlus edtTitle, edtDesc;
    private TextViewPlus txtFile;
    private File file;
    private LinearLayout loadingLayout;

    private LinearLayout bottomSheetLinearLayout;
    private BottomSheetBehavior bottomSheetBehavior;

    private String path;
    boolean serverResponsed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_insert_media);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        txtFile = findViewById(R.id.txt_file);
        edtTitle = findViewById(R.id.edt_title);
        edtDesc = findViewById(R.id.edt_desc);
        loadingLayout = findViewById(R.id.layout_loading);


        edtTitle.setOnClickListener(this);
        edtDesc.setOnClickListener(this);

        findViewById(R.id.lay_inStorage).setOnClickListener(this);
        findViewById(R.id.lay_sdStorage).setOnClickListener(this);

        bottomSheetLinearLayout = findViewById(R.id.bottomsheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        txtFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                } else {
                    // hiding keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtDesc.getWindowToken(), 0);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
    }

    private void showFileChooser() {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        DialogProperties properties = new DialogProperties();

        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(path);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        //user "/" in order to browse all files

        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(NewMediaActivity.this, properties);

        dialog.setTitle("Select a File");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {

                HandleSelectedFile(files);
            }
        });

        dialog.show();
    }


    private void HandleSelectedFile(String[] files) {

        File selectedFile = new File(files[0]);

        G.i( files[0]);

        if (!files[0].contains(".mp3") ) {

            G.toastShort("خطا : فرمت فایل", NewMediaActivity.this);

        } else if ((selectedFile.length()) / 1024 >= 10000) {

            G.i(("File Size: " + selectedFile.length()/1024) + "KB");
            G.toastShort("خطا : حجم فایل", NewMediaActivity.this);

        } else {

            file = selectedFile;
            txtFile.setText(file.getName());
        }
    }

    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {

            String title = edtTitle.getText().toString().trim();
            String description = edtDesc.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || file == null) {

                G.toastShort("اطلاعات ورودی ناکافی است", NewMediaActivity.this);
                return;
            }

            Utils.changeLoadingResource(NewMediaActivity.this, 0);

            //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!serverResponsed) {
                        Utils.changeLoadingResource(NewMediaActivity.this, 1);
                    }
                }
            }, Constant.UPLOAD_TIME_OUT);

//            loadingLayout.setVisibility(View.VISIBLE);



            ContentValues contentValues = new ContentValues();

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_DESCRIPTION, description);

//            new UploadHelper(NewMediaActivity.this, file, contentValues);

            new HttpCommand(HttpCommand.COMMAND_CREATE_MEDIA, file, contentValues).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_COUNT_LIMIT){

                        G.toastShort("تعداد رسانه مجاز : " + JSONParser.getLimitationCode(result) , NewMediaActivity.this);

                    } else if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {
                        G.toastShort("اطلاعیه جدید افزوده شد", NewMediaActivity.this);
                        finish();
                    }
//                    loadingLayout.setVisibility(View.GONE);
                }
            }).execute();

        } else if (id == R.id.lay_inStorage) {

            path = DialogConfigs.DEFAULT_DIR;
            showFileChooser();

        } else if (id == R.id.lay_sdStorage) {

            path = "/sdcard1";
            showFileChooser();

        } else if (id == R.id.edt_title || id == R.id.edt_desc) {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

}
