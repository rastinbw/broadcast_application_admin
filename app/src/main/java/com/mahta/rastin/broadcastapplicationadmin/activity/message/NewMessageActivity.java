package com.mahta.rastin.broadcastapplicationadmin.activity.message;

import android.content.ContentValues;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.helper.Utils;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Field;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;

import java.util.List;

public class NewMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextPlus edtTitle, edtContent;

    private List<Group> groupList;
    private List<Field> fieldList;

    private Spinner gradeSpinner, fieldSpinner;
    private ArrayAdapter adapter;

    private ButtonPlus btnGirl, btnBoy;

    String[] groups;
    String[] fields;

    private String messageContent, title;
    boolean serverResponsed = false;
    private int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        initViews();
    }

    private void initViews() {

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        btnBoy = findViewById(R.id.btn_male);
        btnGirl = findViewById(R.id.btn_female);

        gradeSpinner = findViewById(R.id.spinner_group);
        fieldSpinner = findViewById(R.id.spinner_field);

        edtTitle = findViewById(R.id.edt_title);
        edtContent = findViewById(R.id.edt_content);

        groupList = RealmController.getInstance().getGroupList();
        fieldList = RealmController.getInstance().getFieldList();

        groups = new String[groupList.size()];
        fields = new String[fieldList.size()];

        for (int i = 0; i < groupList.size(); i++) {
            groups[i] = groupList.get(i).getTitle();
        }

        for (int i = 0; i < fieldList.size(); i++) {
            fields[i] = fieldList.get(i).getTitle();
        }

        adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, groups);
        gradeSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, fields);
        fieldSpinner.setAdapter(adapter);

        btnBoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gender = 1;

                btnBoy.setBackgroundResource(R.drawable.rb_b);
                btnGirl.setBackgroundResource(R.drawable.shape_button_white);
            }
        });

        btnGirl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gender = 0;

                btnBoy.setBackgroundResource(R.drawable.shape_button_white);
                btnGirl.setBackgroundResource(R.drawable.rb_b);

            }
        });

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {

            title = edtTitle.getText().toString().trim();
            messageContent = edtContent.getText().toString().trim();

            int groupId = groupList.get(gradeSpinner.getSelectedItemPosition()).getId();
            int fieldId = fieldList.get(fieldSpinner.getSelectedItemPosition()).getId();

            if (title.isEmpty() || messageContent.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", NewMessageActivity.this);
                return;
            }

            Utils.changeLoadingResource(NewMessageActivity.this, 0);

            //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!serverResponsed) {
                        Utils.changeLoadingResource(NewMessageActivity.this, 1);
                    }
                }
            }, Constant.SERVER_RESPONSE_TIME);

            ContentValues contentValues = new ContentValues();

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_CONTENT, messageContent);
            contentValues.put(Keys.KEY_GROUP_ID, groupId);
            contentValues.put(Keys.KEY_FIELD_ID, fieldId);
            contentValues.put(Keys.KEY_GENDER, gender);

            new HttpCommand(HttpCommand.COMMAND_CREATE_MESSAGE, contentValues).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {
                        G.toastShort("پیام جدید افزوده شد", NewMessageActivity.this);
                        finish();
                    }

                }
            }).execute();

        }
    }

}
