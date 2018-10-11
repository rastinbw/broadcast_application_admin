package com.mahta.rastin.broadcastapplicationadmin.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.editor.RichEditor;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.ColorDialogListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDialogDeleteListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.UrlDialogListener;

public class EditorDialog extends Dialog {

    private OnDialogDeleteListener onDialogDeleteListener;

    private Activity activity;
    private RichEditor mEditor;


    public EditorDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to remove dialog default title space
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rich_editor);

        //adjusting dialog layout size
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        getWindow().setLayout((int) (0.9 * size.x) , ViewGroup.LayoutParams.WRAP_CONTENT);

        //to fix corner radius issue
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //cancel button
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });




        mEditor = findViewById(R.id.rich_editor);

        //this is needed to request focus when clicked on bottom of editor
        mEditor.setEditorHeight(200);

        mEditor.setEditorFontSize(17);
        mEditor.setEditorFontColor(Color.BLACK);

        mEditor.setPadding(10, 10, 10, 10);

        mEditor.setPlaceholder("متن");

//        String html = "<div dir='rtl'>" + currentPost.getContent() + "</div>";

        mEditor.setHtml("<table border='1' cellspacing=\"1\" style=\"width:445.5px; text-align: center\"> <tbody><tr>\n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">6 تا 11</span></p>\n" +
                "                        <p>جهال </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">7 تا 11</span></p>\n" +
                "                        <p>ریاضی </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td >\n" +
                "                         <p><span dir=\"RTL\">8 تا 11</span></p>\n" +
                "                         <p>عبدی </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">8 تا 10</span></p>\n" +
                "                         <p>فیزیک</p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><strong>شنبه</strong></p>\n" +
                "                    </td>\n" +
                "                </tr><tr>\n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">5 تا 7</span></p>\n" +
                "                        <p>جهاد </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">4 تا 7</span></p>\n" +
                "                        <p>عربی </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td >\n" +
                "                         <p><span dir=\"RTL\">6 تا 8</span></p>\n" +
                "                         <p>شیمی </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">5 تا 7</span></p>\n" +
                "                         <p>فیزیک</p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><strong>دوشنبه</strong></p>\n" +
                "                    </td>\n" +
                "                </tr><tr>\n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\"></span></p>\n" +
                "                        <p> </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\"></span></p>\n" +
                "                        <p> </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td >\n" +
                "                         <p><span dir=\"RTL\"></span></p>\n" +
                "                         <p> </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">8 تا 10</span></p>\n" +
                "                         <p>شیمی</p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><strong>چهارشنبه</strong></p>\n" +
                "                    </td>\n" +
                "                </tr><tr>\n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">6 تا 7</span></p>\n" +
                "                        <p>خرب </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">6 تا 7</span></p>\n" +
                "                        <p>ریاض </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td >\n" +
                "                         <p><span dir=\"RTL\">6 تا 7</span></p>\n" +
                "                         <p>شیمی </p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><span dir=\"RTL\">6 تا 7</span></p>\n" +
                "                         <p>فیریک</p>\n" +
                "                    </td>\n" +
                "                    \n" +
                "                    <td>\n" +
                "                        <p><strong>پنجشنبه</strong></p>\n" +
                "                    </td>\n" +
                "                </tr></tbody> </table>");

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





        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDialogDeleteListener.onConfirmDeleteItem(true);
                dismiss();
            }
        });

    }

    public void setOnDeleteListener(OnDialogDeleteListener onDialogDeleteListener) {
        this.onDialogDeleteListener = onDialogDeleteListener;
    }






}
