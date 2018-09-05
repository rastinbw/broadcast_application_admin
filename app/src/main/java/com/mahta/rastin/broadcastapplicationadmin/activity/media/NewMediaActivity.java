package com.mahta.rastin.broadcastapplicationadmin.activity.media;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.controller.DialogSelectionListener;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.model.DialogConfigs;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.model.DialogProperties;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.view.FilePickerDialog;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;

import java.io.File;

public class NewMediaActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextPlus edtTitle, edtDesc;
    private TextViewPlus txtFile;
    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_new);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);
        txtFile = findViewById(R.id.txt_file);

        edtTitle = findViewById(R.id.edt_title);
        edtDesc = findViewById(R.id.edt_desc);

        txtFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogProperties properties = new DialogProperties();

                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(DialogConfigs.DEFAULT_DIR);
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
    });
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
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {

            String title = edtTitle.getText().toString().trim();
            String description = edtDesc.getText().toString().trim();

            ContentValues contentValues = new ContentValues();

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_DESCRIPTION, description);


            new HttpCommand(HttpCommand.COMMAND_CREATE_MEDIA, file, contentValues, null).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    if (JSONParser.getResultCodeFromJson(result) == 1000){

                        Toast.makeText(NewMediaActivity.this, "رسانه جدید افزوده شد", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }).execute();

        }
    }
}
