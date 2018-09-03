package com.mahta.rastin.broadcastapplicationadmin.activity.media;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;


import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.main.MainActivity;
import com.mahta.rastin.broadcastapplicationadmin.activity.program.NewProgramActivity;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.controller.DialogSelectionListener;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.model.DialogConfigs;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.model.DialogProperties;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.view.FilePickerDialog;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpManager;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;

import java.io.File;

public class NewMediaActivity extends AppCompatActivity implements View.OnClickListener {

    private Media media;
    private EditTextPlus edtTitle, edtDesc;
    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_new);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        edtTitle = findViewById(R.id.edt_title);
        edtDesc = findViewById(R.id.edt_desc);

        findViewById(R.id.btn_selectfile).setOnClickListener(new View.OnClickListener() {
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

                        Log.i("MYTAG", files[0]);

                        file = new File(files[0]);

//                        Log.i("MYTAG", file.toString());

                    }
                });

                dialog.show();

            }
    });
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

            contentValues.put("token", RealmController.getInstance().getUserToken().getToken());
            contentValues.put("title", title);
            contentValues.put("description", description);


            new HttpCommand(HttpCommand.COMMAND_CREATE_MEDIA, file, title, description, null).setOnResultListener(new OnResultListener() {
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
