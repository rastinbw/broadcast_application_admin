package com.mahta.rastin.broadcastapplicationadmin.activity.message;

import android.content.ContentValues;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import com.mahta.rastin.broadcastapplicationadmin.model.Message;

import java.util.List;

public class EditMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextPlus edtTitle, edtContent;

    private List<Group> groupList;
    private List<Field> fieldList;

    private Spinner gradeSpinner, fieldSpinner;
    private ArrayAdapter gradeAdapter, fieldAdapter;

    private ButtonPlus btnGirl, btnBoy;
    private LinearLayout layoutField, layoutGrade;

    String[] groups;
    String[] fields;

    private String messageContent, title;
    boolean serverResponsed = false;
    private int gender;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        message = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

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

        edtTitle.setText(message.getTitle());
        edtContent.setText(message.getContent());

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

        gradeAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, groups);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setSelection(gradeAdapter.getPosition(RealmController.getInstance().getGroupTitle(message.getGroup_id())));


        fieldAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, fields);
        fieldSpinner.setAdapter(fieldAdapter);
        fieldSpinner.setSelection(fieldAdapter.getPosition(RealmController.getInstance().getFieldTitle(message.getField_id())));


        fieldSpinner.setEnabled(false);
        gradeSpinner.setEnabled(false);

        if (message.getGender() == 0) {
            btnGirl.setBackgroundResource(R.drawable.rb_b);

        } else {
            btnBoy.setBackgroundResource(R.drawable.rb_b);
        }



        layoutField = findViewById(R.id.layout_field);
        layoutGrade = findViewById(R.id.layout_grade);

        btnBoy.setOnClickListener(this);
        btnGirl.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {

            String title = edtTitle.getText().toString().trim();
            int groupId = groupList.get(gradeSpinner.getSelectedItemPosition()).getId();
            String messageContent = edtContent.getText().toString().trim();

            if (title.isEmpty() || messageContent.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", EditMessageActivity.this);
                return;
            }

            Utils.changeLoadingResource(EditMessageActivity.this, 0);

            //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!serverResponsed) {
                        Utils.changeLoadingResource(EditMessageActivity.this, 1);
                    }
                }
            }, Constant.SERVER_RESPONSE_TIME);

            ContentValues contentValues = new ContentValues();

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_CONTENT, messageContent);
            contentValues.put(Keys.KEY_GROUP_ID, groupId);

            new HttpCommand(HttpCommand.COMMAND_UPDATE_MESSAGE, contentValues, message.getId()+"").setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {
                        G.toastShort("پیام با موفقیت ویرایش شد", EditMessageActivity.this);
                        finish();
                    }

                }
            }).execute();

        } else if (id == btnBoy.getId() || id == btnGirl.getId()) {

            G.toastShort("این ویژگی غیر قابل تغییر است", EditMessageActivity.this);
        }
    }

}