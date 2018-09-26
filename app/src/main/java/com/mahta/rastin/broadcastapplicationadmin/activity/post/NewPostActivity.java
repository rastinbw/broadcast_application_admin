package com.mahta.rastin.broadcastapplicationadmin.activity.post;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.ColorDialog;
import com.mahta.rastin.broadcastapplicationadmin.dialog.EditorDialog;
import com.mahta.rastin.broadcastapplicationadmin.dialog.UrlDialog;
import com.mahta.rastin.broadcastapplicationadmin.editor.RichEditor;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.helper.Utils;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.ColorDialogListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.UrlDialogListener;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextPlus edttitle, edtPreview, edtText;
    private RichEditor mEditor;
    boolean serverResponsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_edit);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);
        findViewById(R.id.edt_text).setOnClickListener(this);

        edttitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);
        edtText = findViewById(R.id.edt_text);



        // to adjust text direction
        String html = "<div dir='rtl'>" +

                "<table border='1' cellspacing=\\\"1\\\" style=\\\"width:445.5px; text-align: center\\\"> <tbody><tr>\\n                    <td>\\n                        <p><span dir=\\\"RTL\\\">2 \\u062a\\u0627 4<\\/span><\\/p>\\n                        <p>\\u0631\\u06cc\\u0627\\u0636\\u06cc <\\/p>\\n                    <\\/td>\\n                    \\n                    <td>\\n                        <p><span dir=\\\"RTL\\\">5 \\u062a\\u0627 5<\\/span><\\/p>\\n                        <p>\\u0641\\u06cc\\u0632\\u06cc <\\/p>\\n                    <\\/td>\\n                    \\n                    <td >\\n                         <p><span dir=\\\"RTL\\\">2 \\u062a\\u0627 9<\\/span><\\/p>\\n                         <p>\\u0641\\u06cc\\u0632\\u06cc\\u06a9 <\\/p>\\n                    <\\/td>\\n                    \\n                    <td>\\n                        <p><span dir=\\\"RTL\\\">7 \\u062a\\u0627 8<\\/span><\\/p>\\n                         <p>\\u0634\\u06cc\\u0645\\u06cc<\\/p>\\n                    <\\/td>\\n                    \\n                    <td>\\n                        <p><strong>\\u0634\\u0646\\u0628\\u0647<\\/strong><\\/p>\\n                    <\\/td>\\n                <\\/tr><tr>\\n                    <td>\\n                        <p><span dir=\\\"RTL\\\"><\\/span><\\/p>\\n                        <p> <\\/p>\\n                    <\\/td>\\n                    \\n                    <td>\\n                        <p><span dir=\\\"RTL\\\">10 \\u062a\\u0627 6<\\/span><\\/p>\\n                        <p>\\u0641\\u0627\\u0631\\u0633 <\\/p>\\n                    <\\/td>\\n                    \\n                    <td >\\n                         <p><span dir=\\\"RTL\\\">9 \\u062a\\u0627 7<\\/span><\\/p>\\n                         <p>\\u0639\\u0631\\u0628 <\\/p>\\n                    <\\/td>\\n                    \\n                    <td>\\n                        <p><span dir=\\\"RTL\\\">8 \\u062a\\u0627 6<\\/span><\\/p>\\n                         <p>\\u0639\\u0628\\u06cc<\\/p>\\n                    <\\/td>\\n                    \\n                    <td>\\n                        <p><strong>\\u062f\\u0648\\u0634\\u0646\\u0628\\u0647<\\/strong><\\/p>\\n                    <\\/td>\\n                <\\/tr><tr>\\n                    <td>\\n                        <p><span dir=\\\"RTL\\\">8 \\u062a\\u0627 1<\\/span><\\/p>\\n                        <p>\\u0641\\u0642\\u0632\\u06cc\\u06a9 <\\/p>\\n                    <\\/td>\\n                    \\n                    <td>\\n                        <p><span dir=\\\"RTL\\\">8 \\u062a\\u0627 2<\\/span><\\/p>\\n                        <p>\\u0622\\u0628\\u06cc <\\/p>\\n                    <\\/td>\\n                    \\n                    <td >\\n                         <p><span dir=\\\"RTL\\\">8 \\u062a\\u0627 3<\\/span><\\/p>\\n                         <p>\\u0642\\u062f\\u0633 <\\/p>\\n                    <\\/td>\\n                    \\n                    <td>\\n                        <p><span dir=\\\"RTL\\\">8 \\u062a\\u0627 9<\\/span><\\/p>\\n                         <p>\\u0631\\u06cc\\u0627\\u0636<\\/p>\\n                    <\\/td>\\n                    \\n                    <td>\\n                        <p><strong>\\u067e\\u0646\\u062c\\u0634\\u0646\\u0628\\u0647<\\/strong><\\/p>\\n                    <\\/td>\\n                <\\/tr><\\/tbody> <\\/table>"

                 + "<br/>" + "</div>";
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

                G.toastShort("اطلاعات ورودی ناکافی است", NewPostActivity.this);
                return;
            }

            Utils.changeLoadingResource(NewPostActivity.this, 0);

            //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!serverResponsed) {
                        Utils.changeLoadingResource(NewPostActivity.this, 1);
                    }
                }
            }, Constant.SERVER_RESPONSE_TIME);


            ContentValues contentValues = new ContentValues();

            contentValues.put("token", RealmController.getInstance().getUserToken().getToken());
            contentValues.put("title", title);
            contentValues.put("preview_content", preview);
            contentValues.put("content", html);

            new HttpCommand(HttpCommand.COMMAND_CREATE_POST, contentValues).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

                    if (JSONParser.getResultCodeFromJson(result) == 1000){

                        Toast.makeText(NewPostActivity.this, "اطلاعیه جدید افزوده شد", Toast.LENGTH_SHORT).show();
                        finish();

                        G.i(mEditor.getHtml());
                    }


                }
            }).execute();

        } else if (id == R.id.edt_text) {


            EditorDialog editorDialog = new EditorDialog(NewPostActivity.this);
//            colorDialog.setOnSelectColorListener(new ColorDialogListener() {
//                @Override
//                public void onSelectColor(int color) {
//
//                    mEditor.setTextColor(color);
//
//                }
//            });
            editorDialog.show();


        }
    }
}
