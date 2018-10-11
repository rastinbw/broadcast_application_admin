package com.mahta.rastin.broadcastapplicationadmin.activity.program;

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

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.post.PostContentActivity;
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
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

public class ProgramEditContentFragment  extends Fragment implements View.OnClickListener{

    private String title, preview, content;
    private int gradeId, fieldId;
    private ContentValues contentValues;

    private RichEditor mEditor;

    private Activity activity;

    boolean serverResponsed = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            this.activity = (Activity) context;
        }
    }

    public static ProgramEditContentFragment newInstance (Bundle data) {

        ProgramEditContentFragment fragment = new ProgramEditContentFragment();

        data.get(Keys.KEY_TITLE);
        data.get(Keys.KEY_PREVIEW);

        // these two may be null
        data.getInt(Keys.KEY_GROUP_ID);
        data.getInt(Keys.KEY_FIELD_ID);

        if (data.getBoolean(Keys.KEY_UPDATE)) {

            data.getInt(Keys.KEY_ID);
            data.getString(Keys.KEY_CONTENT);
        }

        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        mEditor.setHtml(getArguments().getString(Keys.KEY_CONTENT));

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

            content = mEditor.getHtml();

            gradeId = getArguments().getInt(Keys.KEY_GROUP_ID);
            fieldId = getArguments().getInt(Keys.KEY_FIELD_ID);


            if (content.isEmpty()) {
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

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_PREVIEW, preview);
            contentValues.put(Keys.KEY_CONTENT, content);
            contentValues.put(Keys.KEY_GROUP_ID, gradeId);
            contentValues.put(Keys.KEY_FIELD_ID, fieldId);

            final int programId = getArguments().getInt(Keys.KEY_ID);

            new HttpCommand(HttpCommand.COMMAND_UPDATE_PROGRAM, contentValues, programId+"").setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_COUNT_LIMIT){

                        G.toastShort("تعداد برنامه مجاز : " + JSONParser.getLimitationCode(result) , activity);

                    } else if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {

                        G.toastShort("برنامه با موفقیت ویرایش شد", activity);

                        Intent intent = new Intent(activity, ProgramContentActivity.class);

                        Program currentProgram = new Program();

                        currentProgram.setId(programId);
                        currentProgram.setTitle(title);
                        currentProgram.setPreview(preview);
                        currentProgram.setContent(content);

                        currentProgram.setGroup_id(gradeId);
                        currentProgram.setField_id(fieldId);

                        intent.putExtra(Keys.KEY_EXTRA_FLAG, currentProgram);
                        startActivity(intent);

                        activity.finish();
                    }

                }
            }).execute();

        }
    }

}