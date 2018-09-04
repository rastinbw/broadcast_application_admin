package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.content.ContentValues;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.adapter.GroupListAdapter;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.editor.RichEditor;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

import java.util.List;

public class EditProgramActivity extends AppCompatActivity implements View.OnClickListener{

    private Program program;
    private EditTextPlus edttitle, edtPreview;
    private RichEditor mEditor;
    private GroupListAdapter groupListAdapter;
    private RecyclerView rcvGroups;
    private List<Group> groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new);

        program = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        initViews();


        groupList = RealmController.getInstance().getGroupList();

        groupListAdapter = new GroupListAdapter(this, groupList);

        groupListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {

                G.toastShort(groupList.get(position).toString(), EditProgramActivity.this);
            }
        });

        rcvGroups.setLayoutManager(new GridLayoutManager(this, 3));

        rcvGroups.setAdapter(groupListAdapter);




        mEditor = findViewById(R.id.post_editor);

        //this is needed to request focus when clicked on bottom of editor
        mEditor.setEditorHeight(200);

        mEditor.setEditorFontSize(17);
        mEditor.setEditorFontColor(Color.BLACK);

        mEditor.setPadding(10, 10, 10, 10);

        mEditor.setPlaceholder("متن");

        // to adjust text direction
//        String html = "<div dir='rtl'>" + "<br/>" + "</div>";

        mEditor.setHtml(program.getContent());


        //to start activity with no focus on fields
        mEditor.requestFocus();



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
            private boolean isChanged;

            @Override public void onClick(View v) {

                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
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

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
                        "dachshund");
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
    }

    private void initViews() {

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        rcvGroups = findViewById(R.id.rcvGroups);


        edttitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);

        edttitle.setText(program.getTitle());
        edtPreview.setText(program.getPreview());

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

            ContentValues contentValues = new ContentValues();

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_PREVIEW, preview);
            contentValues.put(Keys.KEY_CONTENT, content);

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
