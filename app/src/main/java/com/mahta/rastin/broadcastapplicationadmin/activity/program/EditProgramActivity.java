package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.helper.Utils;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

import java.util.List;

public class EditProgramActivity extends AppCompatActivity implements View.OnClickListener{

    private Program currentProgram;
    private EditTextPlus edttitle, edtPreview;
    private List<Group> groupList;
    private Spinner spinner;
    private ArrayAdapter adapter;
    String[] groups;
    boolean serverResponsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_insert_program);

        currentProgram = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        initViews();
    }

    private void initViews() {

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        spinner = findViewById(R.id.spinner_group);

        edttitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);

        edttitle.setText(currentProgram.getTitle());
        edtPreview.setText(currentProgram.getPreview());

        groupList = RealmController.getInstance().getGroupList();

        groups = new String[groupList.size()];

        for (int i = 0; i < groupList.size(); i++) {

            groups[i] = groupList.get(i).getTitle();
        }


        adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, groups);

        spinner.setAdapter(adapter);

        //setting default choice
        spinner.setSelection(adapter.getPosition(RealmController.getInstance().getGroupTitle(currentProgram.getGroup_id())));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        goBackToContent();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            goBackToContent();

        } else if (id == R.id.txtApply) {

            final String title = edttitle.getText().toString();
            final String preview = edtPreview.getText().toString();
            final String content = "";

            if (title.isEmpty() || preview.isEmpty() || content.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", EditProgramActivity.this);
                return;
            }

            Utils.changeLoadingResource(EditProgramActivity.this, 0);

            //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!serverResponsed) {
                        Utils.changeLoadingResource(EditProgramActivity.this, 1);
                    }
                }
            }, Constant.SERVER_RESPONSE_TIME);

            ContentValues contentValues = new ContentValues();

            final int groupId = groupList.get(spinner.getSelectedItemPosition()).getId();

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_PREVIEW, preview);
            contentValues.put(Keys.KEY_CONTENT, content);
            contentValues.put(Keys.KEY_GROUP_ID, groupId);

            new HttpCommand(HttpCommand.COMMAND_UPDATE_PROGRAM, contentValues, currentProgram.getId()+"" ).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {

                        G.toastShort("برنامه با موفقیت ویرایش شد", EditProgramActivity.this);

                        Intent intent = new Intent(EditProgramActivity.this, ProgramContentActivity.class);

                        currentProgram.setTitle(title);
                        currentProgram.setPreview(preview);
                        currentProgram.setContent(content);
                        currentProgram.setGroup_id(groupId);

                        intent.putExtra(Keys.KEY_EXTRA_FLAG, currentProgram);
                        startActivity(intent);

                        finish();
                    }

                }
            }).execute();

        }
    }

    private void goBackToContent() {

        Intent intent = new Intent(EditProgramActivity.this, ProgramContentActivity.class);
        intent.putExtra(Keys.KEY_EXTRA_FLAG, currentProgram);
        startActivity(intent);

        finish();
    }

}
