package com.mahta.rastin.broadcastapplicationadmin.activity.message;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;
import com.mahta.rastin.broadcastapplicationadmin.model.Message;

import java.util.List;

public class EditMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextPlus edtTitle, edtContent;

    private List<Group> groupList;
    private Spinner spinner;
    private ArrayAdapter adapter;
    String[] groups;

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

        spinner = findViewById(R.id.spinner_group);

        edtTitle = findViewById(R.id.edt_title);
        edtContent = findViewById(R.id.edt_content);

        edtTitle.setText(message.getTitle());
        edtContent.setText(message.getContent());

        groupList = RealmController.getInstance().getGroupList();

        groups = new String[groupList.size()];

        for (int i = 0; i < groupList.size(); i++) {

            groups[i] = groupList.get(i).getTitle();
        }

        adapter = new ArrayAdapter<>(this, R.layout.layout_group_spinner_item, groups);

        spinner.setAdapter(adapter);

        spinner.setSelection(adapter.getPosition(RealmController.getInstance().getGroupTitle(message.getGroup_id())));
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {

            String title = edtTitle.getText().toString().trim();
            int groupId = groupList.get(spinner.getSelectedItemPosition()).getId();
            String messageContent = edtContent.getText().toString().trim();

            if (title.isEmpty() || messageContent.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", EditMessageActivity.this);
                return;
            }

            ContentValues contentValues = new ContentValues();

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_CONTENT, messageContent);
            contentValues.put(Keys.KEY_GROUP_ID, groupId);

            new HttpCommand(HttpCommand.COMMAND_UPDATE_MESSAGE, contentValues, message.getId()+"").setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {
                        G.toastShort("پیام با موفقیت ویرایش شد", EditMessageActivity.this);
                        finish();
                    }

                }
            }).execute();

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}