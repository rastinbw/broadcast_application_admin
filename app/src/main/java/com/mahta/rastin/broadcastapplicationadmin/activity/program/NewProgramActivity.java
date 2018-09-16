package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.post.NewPostActivity;
import com.mahta.rastin.broadcastapplicationadmin.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.ProgramFormDialog;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnProgramFormSaveListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;

import java.util.List;

public class NewProgramActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextPlus edtTitle, edtPreview;
    private ButtonPlus btnSaturday, btnSunday, btnMonday, btnTuesday, btnWednesday, btnThursday, btnFriday;

    private List<Group> groupList;
    private Spinner spinner;
    private ArrayAdapter adapter;
    String[] groups;
    private String programContent;
    String[] dayContent = {"", "", "", "", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_new);

        initViews();
    }

    private void initViews() {

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        btnSunday = findViewById(R.id.btn_sunday);
        btnMonday = findViewById(R.id.btn_monday);
        btnTuesday = findViewById(R.id.btn_tuesday);
        btnWednesday = findViewById(R.id.btn_wednesday);
        btnThursday = findViewById(R.id.btn_thursday);
        btnSaturday = findViewById(R.id.btn_saturday);

        btnSaturday.setOnClickListener(this);
        btnSunday.setOnClickListener(this);
        btnMonday.setOnClickListener(this);
        btnTuesday.setOnClickListener(this);
        btnWednesday.setOnClickListener(this);
        btnThursday.setOnClickListener(this);

        spinner = findViewById(R.id.spinner_group);

        edtTitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);

        groupList = RealmController.getInstance().getGroupList();

        groups = new String[groupList.size()];

        for (int i = 0; i < groupList.size(); i++) {

            groups[i] = groupList.get(i).getTitle();
        }

        adapter = new ArrayAdapter<>(this, R.layout.layout_group_spinner_item, groups);

        spinner.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btn_sunday) {
            showProgramDialog(getString(R.string.sunday));

        }else if (id == R.id.btn_saturday) {
            showProgramDialog(getString(R.string.saturday));

        }else if (id == R.id.btn_monday) {
            showProgramDialog(getString(R.string.monday));

        }else if (id == R.id.btn_tuesday ) {
            showProgramDialog(getString(R.string.tuesday));

        }else  if (id == R.id.btn_wednesday ){
            showProgramDialog(getString(R.string.wednesday));

        }else if (id == R.id.btn_thursday) {
            showProgramDialog(getString(R.string.thursday));

        }else if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {

            String title = edtTitle.getText().toString().trim();
            String preview = edtPreview.getText().toString().trim();
            int groupId = groupList.get(spinner.getSelectedItemPosition()).getId();

            if (title.isEmpty() || preview.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", NewProgramActivity.this);
                return;
            }

            programContent = "<table border='1' cellspacing=\"1\" style=\"width:445.5px; text-align: center\"> <tbody>"
                    + dayContent[0]
                    + dayContent[1]
                    + dayContent[2]
                    + dayContent[3]
                    + dayContent[4]
                    + dayContent[5]
                    + "</tbody> </table>";


            G.i(programContent);

            ContentValues contentValues = new ContentValues();

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_PREVIEW, preview);
            contentValues.put(Keys.KEY_CONTENT, programContent);
            contentValues.put(Keys.KEY_GROUP_ID, groupId);

            new HttpCommand(HttpCommand.COMMAND_CREATE_PROGRAM, contentValues).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_COUNT_LIMIT){

                        G.toastShort("تعداد برنامه مجاز : " + JSONParser.getLimitationCode(result) , NewProgramActivity.this);

                    } else if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {
                        G.toastShort("اطلاعیه جدید افزوده شد", NewProgramActivity.this);
                        finish();
                    }

                }
            }).execute();

        }
    }

    private void showProgramDialog(String day) {

        ProgramFormDialog dialog = new ProgramFormDialog(this, day);

        dialog.setOnSaveListener(new OnProgramFormSaveListener() {
            @Override
            public void onSave(String day, String content) {

                switch (day) {

                    case "شنبه":
                        dayContent[0] = content;
                        btnSaturday.setBackgroundResource(R.drawable.rb_b);
                        break;

                    case "یکشنبه":
                        dayContent[1] = content;
                        btnSunday.setBackgroundResource(R.drawable.rb_b);
                        break;

                    case "دوشنبه":
                        dayContent[2] = content;
                        btnMonday.setBackgroundResource(R.drawable.rb_b);
                        break;

                    case "سه شنبه":
                        dayContent[3] = content;
                        btnTuesday.setBackgroundResource(R.drawable.rb_b);
                        break;

                    case "چهارشنبه":
                        dayContent[4] = content;
                        btnWednesday.setBackgroundResource(R.drawable.rb_b);
                        break;

                    case "پنجشنبه":
                        dayContent[5] = content;
                        btnThursday.setBackgroundResource(R.drawable.rb_b);
                        break;
                }
            }
        });

        dialog.show();
    }

}
