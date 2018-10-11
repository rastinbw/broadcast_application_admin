package com.mahta.rastin.broadcastapplicationadmin.activity.post;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnFragmentActionListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Post;

public class InsertPostFragment extends Fragment implements View.OnClickListener{

    private EditTextPlus edttitle, edtPreview;
    String title = "";
    String preview = "";

    private Activity activity;
    private OnFragmentActionListener onNextClickedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            this.activity = (Activity) context;
        }
    }

    public static InsertPostFragment newInstance (Post post) {

        InsertPostFragment fragment = new InsertPostFragment();

        if (post != null) {
            Bundle data = new Bundle();

            data.putInt(Keys.KEY_ID, post.getId());
            data.putString(Keys.KEY_TITLE, post.getTitle());
            data.putString(Keys.KEY_PREVIEW, post.getPreview());
            data.putString(Keys.KEY_CONTENT, post.getContent());

            fragment.setArguments(data);
        }

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_post_new_edit, container , false);

        rootView.findViewById(R.id.imgBack).setOnClickListener(this);
        rootView.findViewById(R.id.txtBack).setOnClickListener(this);

        TextViewPlus txtNext = rootView.findViewById(R.id.txtApply);
        txtNext.setText(R.string.next);
        txtNext.setOnClickListener(this);


        edttitle = rootView.findViewById(R.id.edt_title);
        edtPreview = rootView.findViewById(R.id.edt_preview);


        if (getArguments() != null) {

            edttitle.setText(getArguments().getString(Keys.KEY_TITLE));
            edtPreview.setText(getArguments().getString(Keys.KEY_PREVIEW));
        }

        return rootView;
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            activity.onBackPressed();


        } else if (id == R.id.txtApply) {

            title = edttitle.getText().toString().trim();
            preview = edtPreview.getText().toString().trim();

            if (title.isEmpty() || preview.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", activity);
                return;
            }


            Bundle bundle = new Bundle();

            bundle.putString(Keys.KEY_TITLE, title);
            bundle.putString(Keys.KEY_PREVIEW, preview);

            // if updating
            if (getArguments() != null){

                bundle.putBoolean(Keys.KEY_UPDATE, true);

                bundle.putInt(Keys.KEY_ID, getArguments().getInt(Keys.KEY_ID));
                bundle.putString(Keys.KEY_CONTENT, getArguments().getString(Keys.KEY_CONTENT));
            }

            onNextClickedListener.onAction(null, bundle);
        }
    }

    public void setOnNextListener(OnFragmentActionListener onNextClickedListener) {
        this.onNextClickedListener = onNextClickedListener;
    }
}
