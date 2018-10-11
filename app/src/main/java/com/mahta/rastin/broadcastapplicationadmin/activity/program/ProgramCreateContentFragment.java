package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.helper.Utils;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;

public class ProgramCreateContentFragment extends Fragment implements View.OnClickListener{

    private String title, preview, content = "";
    private int gradeId, fieldId;
    private ContentValues contentValues;

    private EditTextPlus[] edtTime = new EditTextPlus[5];
    EditTextPlus[][] edtDays = new EditTextPlus[6][5];

    private Activity activity;

    boolean serverResponsed = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            this.activity = (Activity) context;
        }
    }

    public static ProgramCreateContentFragment newInstance (Bundle data) {

        ProgramCreateContentFragment fragment = new ProgramCreateContentFragment();

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_create_program, container , false);

        rootView.findViewById(R.id.imgBack).setOnClickListener(this);
        rootView.findViewById(R.id.txtApply).setOnClickListener(this);
        rootView.findViewById(R.id.txtBack).setOnClickListener(this);

        initSchedule(rootView);

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

            gradeId = getArguments().getInt(Keys.KEY_GROUP_ID);
            fieldId = getArguments().getInt(Keys.KEY_FIELD_ID);

            createProgramContent();

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

            new HttpCommand(HttpCommand.COMMAND_CREATE_PROGRAM, contentValues).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_COUNT_LIMIT){

                        G.toastShort("تعداد برنامه مجاز : " + JSONParser.getLimitationCode(result) , activity);

                    } else if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {

                        G.toastShort("اطلاعیه جدید افزوده شد", activity);
                        activity.finish();
                    }

                }
            }).execute();

        }
    }

    private void initSchedule(View view) {

        edtTime[0] = view.findViewById(R.id.edt_time1);
        edtTime[1] = view.findViewById(R.id.edt_time2);
        edtTime[2] = view.findViewById(R.id.edt_time3);
        edtTime[3] = view.findViewById(R.id.edt_time4);
        edtTime[4] = view.findViewById(R.id.edt_time5);

        edtDays[0][0] = view.findViewById(R.id.edt_sat1);
        edtDays[0][1] = view.findViewById(R.id.edt_sat2);
        edtDays[0][2] = view.findViewById(R.id.edt_sat3);
        edtDays[0][3] = view.findViewById(R.id.edt_sat4);
        edtDays[0][4] = view.findViewById(R.id.edt_sat5);

        edtDays[1][0] = view.findViewById(R.id.edt_sun1);
        edtDays[1][1] = view.findViewById(R.id.edt_sun2);
        edtDays[1][2] = view.findViewById(R.id.edt_sun3);
        edtDays[1][3] = view.findViewById(R.id.edt_sun4);
        edtDays[1][4] = view.findViewById(R.id.edt_sun5);

        edtDays[2][0] = view.findViewById(R.id.edt_mon1);
        edtDays[2][1] = view.findViewById(R.id.edt_mon2);
        edtDays[2][2] = view.findViewById(R.id.edt_mon3);
        edtDays[2][3] = view.findViewById(R.id.edt_mon4);
        edtDays[2][4] = view.findViewById(R.id.edt_mon5);

        edtDays[3][0] = view.findViewById(R.id.edt_tue1);
        edtDays[3][1] = view.findViewById(R.id.edt_tue2);
        edtDays[3][2] = view.findViewById(R.id.edt_tue3);
        edtDays[3][3] = view.findViewById(R.id.edt_tue4);
        edtDays[3][4] = view.findViewById(R.id.edt_tue5);

        edtDays[4][0] = view.findViewById(R.id.edt_wed1);
        edtDays[4][1] = view.findViewById(R.id.edt_wed2);
        edtDays[4][2] = view.findViewById(R.id.edt_wed3);
        edtDays[4][3] = view.findViewById(R.id.edt_wed4);
        edtDays[4][4] = view.findViewById(R.id.edt_wed5);

        edtDays[5][0] = view.findViewById(R.id.edt_thu1);
        edtDays[5][1] = view.findViewById(R.id.edt_thu2);
        edtDays[5][2] = view.findViewById(R.id.edt_thu3);
        edtDays[5][3] = view.findViewById(R.id.edt_thu4);
        edtDays[5][4] = view.findViewById(R.id.edt_thu5);
    }

    private void createProgramContent() {

        int columns;
        int count;
        boolean[] addDay = new boolean[edtDays.length];

        for (int i = 0; i < edtDays.length; i++) {
            addDay[i] = true;
        }

        // check column number
        for (columns = 0; columns < edtTime.length; columns++) {

            if (edtTime[columns].getText().toString().trim().isEmpty())
                break;
        }

        //checkig to find empty days
        for (int i = 0; i < edtDays.length; i++) {

            count = 0;

            for (int j = 0; j < edtDays[i].length; j++) {

                if (edtDays[i][j].getText().toString().trim().isEmpty()) {
                    count++;
                }
            }
            if (count == edtDays[i].length) {

                addDay[i] = false;
            }
        }

        content = "<table dir=\"rtl\" border='1' cellspacing=\"1\" style=\"width:445.5px; text-align: center\"> <tbody>";

        String tempContent;


        // for row counts
        for (int i = 0; i < edtDays.length + 1; i++) {

            tempContent = "<tr>";

            // if adding first row
            if (i == 0) {

                tempContent += "<td> <p> <strong>" + "ساعت" + "</strong> </p> </td>";

            } else {

                if (addDay[i - 1]) {

                    switch (i) {

                        case 1:
                            tempContent += "<td> <p> <strong>" + "شنبه" + "</strong> </p> </td>";
                            break;

                        case 2:
                            tempContent += "<td> <p> <strong>" + "یکشنبه" + "</strong> </p> </td>";
                            break;

                        case 3:
                            tempContent += "<td> <p> <strong>" + "دوشنبه" + "</strong> </p> </td>";
                            break;

                        case 4:
                            tempContent += "<td> <p> <strong>" + "سه شنبه" + "</strong> </p> </td>";
                            break;

                        case 5:
                            tempContent += "<td> <p> <strong>" + "چهارشنبه" + "</strong> </p> </td>";
                            break;

                        case 6:
                            tempContent += "<td> <p> <strong>" + "پنجشنبه" + "</strong> </p> </td>";
                            break;
                    }
                }
            }

            for (int j = 0; j < columns; j++) {

                // if adding time row
                if (i == 0) {
                    tempContent +=  "<td> <p> <strong>"+ edtTime[j].getText().toString().trim() + "</strong> </p> </td>";

                } else { // adding day rows

                    if (addDay[i - 1]) {

                      tempContent += "<td> <p>" + edtDays[i - 1][j].getText().toString().trim() + "</p> </td>";
                        }
                    }
                }

            tempContent += "<tr>";

            // adding row on right condition
            if (i == 0 || addDay[i - 1]) {
                content += tempContent;
            }
            }

        content +=  "</tbody> </table>";
    }
}