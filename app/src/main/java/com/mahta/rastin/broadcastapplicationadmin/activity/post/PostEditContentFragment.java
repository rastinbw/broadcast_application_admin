package com.mahta.rastin.broadcastapplicationadmin.activity.post;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
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

public class PostEditContentFragment extends Fragment implements View.OnClickListener{

    private RichEditor mEditor;
    private String html, title, preview;
    private ContentValues contentValues;

    private Activity activity;

    boolean serverResponsed = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            this.activity = (Activity) context;
        }
    }

    public static PostEditContentFragment newInstance (Bundle data) {

        PostEditContentFragment fragment = new PostEditContentFragment();

        data.get(Keys.KEY_TITLE);
        data.get(Keys.KEY_PREVIEW);

        // need to check KEY_UPDATE in other functions in this class
        data.getBoolean(Keys.KEY_UPDATE);

        if (data.getBoolean(Keys.KEY_UPDATE)) {

            data.getInt(Keys.KEY_ID);
            data.getString(Keys.KEY_CONTENT);
        }

        fragment.setArguments(data);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_post_editor, container , false);

        rootView.findViewById(R.id.imgBack).setOnClickListener(this);
        rootView.findViewById(R.id.txtApply).setOnClickListener(this);
        rootView.findViewById(R.id.txtBack).setOnClickListener(this);

        mEditor = rootView.findViewById(R.id.rich_editor);

        //this is needed to request focus when clicked on bottom of editor
        mEditor.setEditorHeight(200);

        mEditor.setEditorFontSize(17);
        mEditor.setEditorFontColor(Color.BLACK);

        mEditor.setPadding(10, 10, 10, 10);


        if (getArguments().getString(Keys.KEY_CONTENT) != null) {

            html = getArguments().getString(Keys.KEY_CONTENT);
        } else {

            // to fix direction issue
            html = "<div dir='rtl'>" + "<br/>" + "</div>";
        }

        mEditor.setHtml(html);

        //to start activity with no focus on fields
        mEditor.focusEditor();


        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {

            }
        });

        rootView.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBold();
            }
        });

        rootView.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        rootView.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        rootView.findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {

                ColorDialog colorDialog = new ColorDialog(activity);
                colorDialog.setOnSelectColorListener(new ColorDialogListener() {
                    @Override
                    public void onSelectColor(int color) {

                        mEditor.setTextColor(color);

                    }
                });
                colorDialog.show();

            }
        });

        rootView.findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        rootView.findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        rootView.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        rootView.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                UrlDialog urlDialog = new UrlDialog(activity);

                urlDialog.setOnInsertDialogListener(new UrlDialogListener() {
                    @Override
                    public void onInsertUrl(String title, String url) {

                        mEditor.insertLink(url, title);
                    }
                });
                urlDialog.show();
            }
        });


        return rootView;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            activity.onBackPressed();

        } else if (id == R.id.txtApply) {

            title = getArguments().getString(Keys.KEY_TITLE);
            preview = getArguments().getString(Keys.KEY_PREVIEW);
            html = mEditor.getHtml();

            if (html.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", activity);
                return;
            }

            Utils.changeLoadingResource(activity, 0);

            //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!serverResponsed) {
                        Utils.changeLoadingResource(activity, 1);
                    }
                }
            }, Constant.SERVER_RESPONSE_TIME);

            contentValues = new ContentValues();

            contentValues.put("token", RealmController.getInstance().getUserToken().getToken());
            contentValues.put("title", title);
            contentValues.put("preview_content", preview);
            contentValues.put("content", html);

            if (getArguments().getBoolean(Keys.KEY_UPDATE)) {
                updatePost();

            } else {
                createPost();
            }
        }
    }

    private void createPost() {

        new HttpCommand(HttpCommand.COMMAND_CREATE_POST, contentValues).setOnResultListener(new OnResultListener() {
            @Override
            public void onResult(String result) {

                serverResponsed = true;

                if (JSONParser.getResultCodeFromJson(result) == 1000){

                    Toast.makeText(activity, "اطلاعیه جدید افزوده شد", Toast.LENGTH_SHORT).show();
                    activity.finish();

                }
            }
        }).execute();
    }

    private void updatePost() {

        final int postId = getArguments().getInt(Keys.KEY_ID);

        new HttpCommand(HttpCommand.COMMAND_UPDATE_POST, contentValues, postId+"").setOnResultListener(new OnResultListener() {
            @Override
            public void onResult(String result) {

                serverResponsed = true;

                if (JSONParser.getResultCodeFromJson(result) == 1000){

                    G.toastShort("اطلاعیه با موفقیت ویرایش شد", activity);

                    Intent intent = new Intent(activity, PostContentActivity.class);

                    Post currentPost = new Post();

                    currentPost.setId(postId);
                    currentPost.setTitle(title);
                    currentPost.setPreview(preview);
                    currentPost.setContent(html);

                    intent.putExtra(Keys.KEY_EXTRA_FLAG, currentPost);
                    startActivity(intent);

                    activity.finish();
                }
            }
        }).execute();
    }
}
