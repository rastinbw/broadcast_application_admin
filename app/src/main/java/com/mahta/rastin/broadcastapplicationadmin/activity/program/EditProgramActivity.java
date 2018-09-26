package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.ColorDialog;
import com.mahta.rastin.broadcastapplicationadmin.dialog.UrlDialog;
import com.mahta.rastin.broadcastapplicationadmin.editor.RichEditor;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.helper.Utils;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.ColorDialogListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.UrlDialogListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

import java.util.List;

public class EditProgramActivity extends AppCompatActivity implements View.OnClickListener{

    private Program currentProgram;
    private EditTextPlus edttitle, edtPreview;
    private RichEditor mEditor;
    private List<Group> groupList;
    private Spinner spinner;
    private ArrayAdapter adapter;
    String[] groups;
    boolean serverResponsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_edit);

        currentProgram = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        initViews();


        mEditor = findViewById(R.id.rich_editor);

        //this is needed to request focus when clicked on bottom of editor
        mEditor.setEditorHeight(200);

        mEditor.setEditorFontSize(17);
        mEditor.setEditorFontColor(Color.BLACK);

        mEditor.setPadding(10, 10, 10, 10);

        mEditor.setPlaceholder("متن");

        // to adjust text direction
        String html = "<div>" + currentProgram.getContent() + "<br/>" + "</div>";

        mEditor.setHtml(currentProgram.getContent());


        mEditor.getSettings().setLoadWithOverviewMode(true);
        mEditor.getSettings().setBuiltInZoomControls(true);
        mEditor.getSettings().setDisplayZoomControls(false);


        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {

            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {

                ColorDialog colorDialog = new ColorDialog(EditProgramActivity.this);
                colorDialog.setOnSelectColorListener(new ColorDialogListener() {
                    @Override
                    public void onSelectColor(int color) {

                        mEditor.setTextColor(color);

                    }
                });
                colorDialog.show();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                UrlDialog urlDialog = new UrlDialog(EditProgramActivity.this);

                urlDialog.setOnInsertDialogListener(new UrlDialogListener() {
                    @Override
                    public void onInsertUrl(String title, String url) {

                        mEditor.insertLink(url, title);
                    }
                });
                urlDialog.show();
            }
        });
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


        adapter = new ArrayAdapter<>(this, R.layout.layout_group_spinner_item, groups);

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
            final String content = mEditor.getHtml();

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
