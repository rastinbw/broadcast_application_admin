package com.mahta.rastin.broadcastapplicationadmin.activity.other;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.model.Post;

import jp.wasabeef.richeditor.RichEditor;

public class EditPostActivity extends AppCompatActivity implements View.OnClickListener {

    private Post post;
    private EditTextPlus edttitle, edtPreview;
    private RichEditor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        edttitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);

        post = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        edttitle.setText(post.getTitle());
        edtPreview.setText(post.getPreview());



        mEditor = findViewById(R.id.post_editor);

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.RED);

        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);

        mEditor.setPadding(10, 10, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");

        mEditor.setPlaceholder("Insert text here...");
        //mEditor.setInputEnabled(false);


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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {
            Toast.makeText(this, "ثبت", Toast.LENGTH_SHORT).show();
        }
    }
}
