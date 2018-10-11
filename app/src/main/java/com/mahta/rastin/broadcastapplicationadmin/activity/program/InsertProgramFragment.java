package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnFragmentActionListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Field;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

import java.util.List;

public class InsertProgramFragment  extends Fragment implements View.OnClickListener{

    private EditTextPlus edttitle, edtPreview;
    private Spinner gradeSpinner, fieldSpinner;

    private ArrayAdapter<String> gradeAdapter;
    private ArrayAdapter<String> fieldAdapter;

    private List<Group> groupList;
    private List<Field> fieldList;

    String[] groups;
    String[] fields;

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

    public static InsertProgramFragment newInstance (Program program) {

        InsertProgramFragment fragment = new InsertProgramFragment();

        if (program != null) {
            Bundle data = new Bundle();

            data.putInt(Keys.KEY_ID, program.getId());
            data.putString(Keys.KEY_TITLE, program.getTitle());
            data.putString(Keys.KEY_PREVIEW, program.getPreview());
            data.putString(Keys.KEY_CONTENT, program.getContent());

            // these two may be null
            data.putInt(Keys.KEY_GROUP_ID, program.getGroup_id());
            data.putInt(Keys.KEY_FIELD_ID, program.getField_id());

            fragment.setArguments(data);
        }

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_insert_program, container , false);

        rootView.findViewById(R.id.imgBack).setOnClickListener(this);
        rootView.findViewById(R.id.txtBack).setOnClickListener(this);

        TextViewPlus txtNext = rootView.findViewById(R.id.txtApply);
        txtNext.setText(R.string.next);
        txtNext.setOnClickListener(this);

        edttitle = rootView.findViewById(R.id.edt_title);
        edtPreview = rootView.findViewById(R.id.edt_preview);

        gradeSpinner = rootView.findViewById(R.id.spinner_group);
        fieldSpinner = rootView.findViewById(R.id.spinner_field);

        groupList = RealmController.getInstance().getGroupList();
        fieldList = RealmController.getInstance().getFieldList();

        //adding 1 additional String for no Group/Field choice
        groups = new String[groupList.size() + 1];
        fields = new String[fieldList.size() + 1];

        groups[0] = "-";
        fields[0] = "-";

        for (int i = 0; i < groupList.size(); i++) {

            groups[i + 1] = groupList.get(i).getTitle();
        }
        for (int i = 0; i < fieldList.size(); i++) {

            fields[i + 1] = fieldList.get(i).getTitle();
        }


        gradeAdapter = new ArrayAdapter<>(activity, R.layout.layout_spinner_item, groups);
        gradeSpinner.setAdapter(gradeAdapter);

        fieldAdapter = new ArrayAdapter<>(activity, R.layout.layout_spinner_item, fields);
        fieldSpinner.setAdapter(fieldAdapter);


        if (getArguments() != null) {

            edttitle.setText(getArguments().getString(Keys.KEY_TITLE));
            edtPreview.setText(getArguments().getString(Keys.KEY_PREVIEW));

            int currentGradeId = getArguments().getInt(Keys.KEY_GROUP_ID);
            int currentFieldId = getArguments().getInt(Keys.KEY_FIELD_ID);

            try {
                gradeSpinner.setSelection(gradeAdapter.getPosition(RealmController.getInstance().getGroupTitle(currentGradeId)));
            }catch (NullPointerException e) {
                G.i("GradeId was null");
            }

            try {
                fieldSpinner.setSelection(fieldAdapter.getPosition(RealmController.getInstance().getFieldTitle(currentFieldId)));
            }catch (NullPointerException e) {
                G.i("FieldId was null");

            }
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

            int selectedGradeItem = gradeSpinner.getSelectedItemPosition();
            int selectedFieldItem = fieldSpinner.getSelectedItemPosition();


            Bundle bundle = new Bundle();

            bundle.putString(Keys.KEY_TITLE, title);
            bundle.putString(Keys.KEY_PREVIEW, preview);

            // puting gradeId and fieldId in bundle only if they are choosen
            if (selectedGradeItem != 0) {
                bundle.putInt(Keys.KEY_GROUP_ID, groupList.get(selectedGradeItem - 1).getId());
            }
            if (selectedFieldItem != 0) {
                bundle.putInt(Keys.KEY_FIELD_ID, fieldList.get(selectedFieldItem - 1).getId());
            }



            //if updating current program
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
