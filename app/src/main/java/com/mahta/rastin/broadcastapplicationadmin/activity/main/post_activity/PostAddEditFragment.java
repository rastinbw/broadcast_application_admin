package com.mahta.rastin.broadcastapplicationadmin.activity.main.post_activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.editor.RichEditor;
import com.mahta.rastin.broadcastapplicationadmin.model.Post;

public class PostAddEditFragment extends Fragment implements View.OnClickListener {

    public Post post;
    private RichEditor mEditor;

    public static PostAddEditFragment newInstance (Post post) {
        PostAddEditFragment fragment = new PostAddEditFragment();

//        Bundle args = new Bundle();
//        args.putInt("score" , score);
//        args.putString("name" , name);
//        args.putString("id" , id);
//
//        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_post_addedit , container , false);

        rootView.findViewById(R.id.imgBack).setOnClickListener(this);
        rootView.findViewById(R.id.txtApply).setOnClickListener(this);
        rootView.findViewById(R.id.txtBack).setOnClickListener(this);


        mEditor = rootView.findViewById(R.id.post_editor);

        //this is needed to request focus when clicked on bottom of editor
        mEditor.setEditorHeight(200);

        mEditor.setEditorFontSize(17);
        mEditor.setEditorFontColor(Color.BLACK);

        mEditor.setPadding(10, 10, 10, 10);

        mEditor.setPlaceholder("متن");

        // to adjust text direction
        String html = "<div dir='rtl'>" + "<br/>" + "</div>";

        mEditor.setHtml(html);


        //to start activity with no focus on fields
        mEditor.requestFocus();



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
            private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
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

        rootView.findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
                        "dachshund");
            }
        });

        rootView.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });





        return rootView;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
//            finish();

        } else if (id == R.id.txtApply) {
//            Toast.makeText(this, "ثبت", Toast.LENGTH_SHORT).show();
        }

    }
}
