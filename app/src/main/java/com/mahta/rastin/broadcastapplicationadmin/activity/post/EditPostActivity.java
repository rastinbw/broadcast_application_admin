package com.mahta.rastin.broadcastapplicationadmin.activity.post;

import android.content.ContentValues;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
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
import com.mahta.rastin.broadcastapplicationadmin.model.Post;


public class EditPostActivity extends AppCompatActivity implements View.OnClickListener {

    private Post post;
    private EditTextPlus edttitle, edtPreview;
    private RichEditor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_edit);



        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        edttitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);

        post = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        edttitle.setText(post.getTitle());
        edtPreview.setText(post.getPreview());


        mEditor = findViewById(R.id.post_editor);

        //this is needed to request focus when clicked on bottom of editor
        mEditor.setEditorHeight(200);

        mEditor.setEditorFontSize(17);
        mEditor.setEditorFontColor(Color.BLACK);

        mEditor.setPadding(10, 10, 10, 10);

        mEditor.setPlaceholder("متن");

        String html = "<div dir='rtl'>" + post.getContent() + "</div>";

        mEditor.setHtml(html);

        //to start activity with no focus on fields
        mEditor.requestFocus();



//        Log.i("MYTAG", post.getContent());


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

                ColorDialog colorDialog = new ColorDialog(EditPostActivity.this);
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

                UrlDialog urlDialog = new UrlDialog(EditPostActivity.this);

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

            String title = edttitle.getText().toString().trim();
            String preview = edtPreview.getText().toString().trim();
            String html = mEditor.getHtml();

            if (title.isEmpty() || preview.isEmpty() || html.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", EditPostActivity.this);
                return;
            }

            ContentValues contentValues = new ContentValues();

            contentValues.put("token", RealmController.getInstance().getUserToken().getToken());
            contentValues.put("title", title);
            contentValues.put("preview_content", preview);
            contentValues.put("content", html);

            new HttpCommand(HttpCommand.COMMAND_UPDATE_POST, contentValues, post.getId()+"").setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    if (JSONParser.getResultCodeFromJson(result) == 1000){

                        Toast.makeText(EditPostActivity.this, "اطلاعیه با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
            }).execute();



        }
    }
}
