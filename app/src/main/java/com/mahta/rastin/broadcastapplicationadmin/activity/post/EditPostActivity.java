package com.mahta.rastin.broadcastapplicationadmin.activity.post;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import com.mahta.rastin.broadcastapplicationadmin.model.Post;


public class EditPostActivity extends AppCompatActivity implements View.OnClickListener {

    private Post currentPost;
    private EditTextPlus edttitle, edtPreview;
    private RichEditor mEditor;
    boolean serverResponsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_edit);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        edttitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);

        currentPost = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        edttitle.setText(currentPost.getTitle());
        edtPreview.setText(currentPost.getPreview());


        mEditor = findViewById(R.id.rich_editor);

        //this is needed to request focus when clicked on bottom of editor
        mEditor.setEditorHeight(200);

        mEditor.setEditorFontSize(17);
        mEditor.setEditorFontColor(Color.BLACK);

        mEditor.setPadding(10, 10, 10, 10);

        mEditor.setPlaceholder("متن");

        String html = "<div dir='rtl'>" + currentPost.getContent() + "</div>";

        mEditor.setHtml(html);

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

        goBackToContent();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            goBackToContent();

        } else if (id == R.id.txtApply) {

            final String title = edttitle.getText().toString().trim();
            final String preview = edtPreview.getText().toString().trim();
            final String html = mEditor.getHtml();

            if (title.isEmpty() || preview.isEmpty() || html.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", EditPostActivity.this);
                return;
            }

            Utils.changeLoadingResource(EditPostActivity.this, 0);

            //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!serverResponsed) {
                        Utils.changeLoadingResource(EditPostActivity.this, 1);
                    }
                }
            }, Constant.SERVER_RESPONSE_TIME);

            ContentValues contentValues = new ContentValues();

            contentValues.put("token", RealmController.getInstance().getUserToken().getToken());
            contentValues.put("title", title);
            contentValues.put("preview_content", preview);
            contentValues.put("content", html);

            new HttpCommand(HttpCommand.COMMAND_UPDATE_POST, contentValues, currentPost.getId()+"").setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

                    if (JSONParser.getResultCodeFromJson(result) == 1000){

                        Toast.makeText(EditPostActivity.this, "اطلاعیه با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EditPostActivity.this, PostContentActivity.class);

                        currentPost.setTitle(title);
                        currentPost.setPreview(preview);
                        currentPost.setContent(html);

                        intent.putExtra(Keys.KEY_EXTRA_FLAG, currentPost);
                        startActivity(intent);

                        finish();
                    }

                }
            }).execute();

        }
    }

    private void goBackToContent() {

        Intent intent = new Intent(EditPostActivity.this, PostContentActivity.class);
        intent.putExtra(Keys.KEY_EXTRA_FLAG, currentPost);
        startActivity(intent);

        finish();
    }
}
