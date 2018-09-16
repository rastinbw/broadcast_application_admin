package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.content.ContentValues;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.post.EditPostActivity;
import com.mahta.rastin.broadcastapplicationadmin.activity.post.NewPostActivity;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.ColorDialog;
import com.mahta.rastin.broadcastapplicationadmin.dialog.UrlDialog;
import com.mahta.rastin.broadcastapplicationadmin.editor.RichEditor;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.ColorDialogListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.UrlDialogListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;
import com.mahta.rastin.broadcastapplicationadmin.model.Message;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

import java.security.Key;
import java.util.List;

import io.realm.Realm;

public class EditProgramActivity extends AppCompatActivity implements View.OnClickListener{

    private Program program;
    private EditTextPlus edttitle, edtPreview;
    private RichEditor mEditor;
    private RecyclerView rcvGroups;
    private List<Group> groupList;
    private Spinner spinner;
    private ArrayAdapter adapter;
    String[] groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_edit);

        program = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        initViews();





        mEditor = findViewById(R.id.post_editor);

        //this is needed to request focus when clicked on bottom of editor
        mEditor.setEditorHeight(200);

        mEditor.setEditorFontSize(17);
        mEditor.setEditorFontColor(Color.BLACK);

        mEditor.setPadding(10, 10, 10, 10);

        mEditor.setPlaceholder("متن");

        // to adjust text direction
        String html = "<div>" + program.getContent() + "<br/>" + "</div>";

        mEditor.setHtml(program.getContent());


        //to start activity with no focus on fields
        mEditor.requestFocus();


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

        edttitle.setText(program.getTitle());
        edtPreview.setText(program.getPreview());

        groupList = RealmController.getInstance().getGroupList();

        groups = new String[groupList.size()];

        for (int i = 0; i < groupList.size(); i++) {

            groups[i] = groupList.get(i).getTitle();
        }


        adapter = new ArrayAdapter<>(this, R.layout.layout_group_spinner_item, groups);

        spinner.setAdapter(adapter);

        //setting default choice
        spinner.setSelection(adapter.getPosition(RealmController.getInstance().getGroupTitle(program.getGroup_id())));
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {

            String title = edttitle.getText().toString();
            String preview = edtPreview.getText().toString();
            String content = mEditor.getHtml();

            if (title.isEmpty() || preview.isEmpty() || content.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", EditProgramActivity.this);
                return;
            }

            String groupTitle = spinner.getSelectedItem().toString();

            G.i(groupTitle);

            ContentValues contentValues = new ContentValues();

            G.i(spinner.getSelectedItemPosition()+"");

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_PREVIEW, preview);
            contentValues.put(Keys.KEY_CONTENT, content);
            contentValues.put(Keys.KEY_GROUP_ID, groupList.get(spinner.getSelectedItemPosition()).getId());

            new HttpCommand(HttpCommand.COMMAND_UPDATE_PROGRAM, contentValues, program.getId()+"" ).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {

                        G.toastShort("برنامه با موفقیت ویرایش شد", EditProgramActivity.this);
                        finish();
                    }

                }
            }).execute();

        }

    }

}
