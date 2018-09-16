package com.mahta.rastin.broadcastapplicationadmin.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnProgramFormSaveListener;

public class ProgramFormDialog extends Dialog {

    private String currentDay;
    private String programContent;
    private OnProgramFormSaveListener onSaveListener;

    private Activity activity;
    private EditTextPlus edtStart1, edtStart2, edtStart3, edtStart4,
            edtEnd1, edtEnd2, edtEnd3, edtEnd4,
            edtLesson1, edtLesson2, edtLesson3, edtLesson4;

    public ProgramFormDialog(@NonNull Activity activity, String day) {
        super(activity);

        this.activity = activity;
        this.currentDay = day;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to remove dialog default title space
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_program);

        //adjusting dialog layout size
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        getWindow().setLayout((int) (0.8 * size.x) , ViewGroup.LayoutParams.WRAP_CONTENT);

        //to fix corner radius issue
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((TextViewPlus)findViewById(R.id.txt_day)).setText(currentDay);

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String startTime1 = ((EditTextPlus)findViewById(R.id.edt_start1)).getText().toString().trim();
                String startTime2 = ((EditTextPlus)findViewById(R.id.edt_start2)).getText().toString().trim();;
                String startTime3 = ((EditTextPlus)findViewById(R.id.edt_start3)).getText().toString().trim();;
                String startTime4 = ((EditTextPlus)findViewById(R.id.edt_start4)).getText().toString().trim();;

                String endTime1 = ((EditTextPlus)findViewById(R.id.edt_end1)).getText().toString().trim();
                String endTime2 = ((EditTextPlus)findViewById(R.id.edt_end2)).getText().toString().trim();
                String endTime3 = ((EditTextPlus)findViewById(R.id.edt_end3)).getText().toString().trim();
                String endTime4 = ((EditTextPlus)findViewById(R.id.edt_end4)).getText().toString().trim();

                String lesson1 = ((EditTextPlus)findViewById(R.id.edt_lesson1)).getText().toString().trim();
                String lesson2 = ((EditTextPlus)findViewById(R.id.edt_lesson2)).getText().toString().trim();
                String lesson3 = ((EditTextPlus)findViewById(R.id.edt_lesson3)).getText().toString().trim();
                String lesson4 = ((EditTextPlus)findViewById(R.id.edt_lesson4)).getText().toString().trim();

                //times
                String firstPeriod = "";
                String secondPeriod = "";
                String thirdPeriod = "";
                String fourthPeriod = "";

                if (!startTime1.isEmpty()) {
                    firstPeriod = startTime1 + " تا " + endTime1;
                }
                if (!startTime2.isEmpty()) {
                    secondPeriod= startTime2 + " تا " + endTime2;
                }
                if (!startTime3.isEmpty()) {
                    thirdPeriod = startTime3 + " تا " + endTime3;
                }
                if (!startTime4.isEmpty()) {
                    fourthPeriod = startTime4 + " تا " + endTime4;
                }

                programContent =
                        "<tr>\n" +
                                "                    <td>\n" +
                                "                        <p><span dir=\"RTL\">"+ fourthPeriod+"</span></p>\n" +
                                "                        <p>"+ lesson4 +" </p>\n" +
                                "                    </td>\n" +
                                "                    \n" +
                                "                    <td>\n" +
                                "                        <p><span dir=\"RTL\">"+ thirdPeriod +"</span></p>\n" +
                                "                        <p>"+ lesson3 + " </p>\n" +
                                "                    </td>\n" +
                                "                    \n" +
                                "                    <td >\n" +
                                "                         <p><span dir=\"RTL\">"+ secondPeriod  +"</span></p>\n" +
                                "                         <p>" + lesson2 + " </p>\n" +
                                "                    </td>\n" +
                                "                    \n" +
                                "                    <td>\n" +
                                "                        <p><span dir=\"RTL\">" + firstPeriod + "</span></p>\n" +
                                "                         <p>"+ lesson1 + "</p>\n" +
                                "                    </td>\n" +
                                "                    \n" +
                                "                    <td>\n" +
                                "                        <p><strong>" + currentDay + "</strong></p>\n" +
                                "                    </td>\n" +
                                "                </tr>";


                onSaveListener.onSave(currentDay, programContent);

                G.toastShort("برنامه " + currentDay + " ذخیره شد", activity);

                dismiss();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public void setOnSaveListener(OnProgramFormSaveListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }
}
