package com.mahta.rastin.broadcastapplicationadmin.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.ColorDialogListener;

public class ColorDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private ColorDialogListener colorDialogListener;


    public ColorDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to remove dialog default title space
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_color);

        //adjusting dialog layout size
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        getWindow().setLayout((int) (0.8 * size.x) , ViewGroup.LayoutParams.WRAP_CONTENT);

        //to fix corner radius issue
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        findViewById(R.id.layout_blue).setOnClickListener(this);
        findViewById(R.id.layout_black).setOnClickListener(this);
        findViewById(R.id.layout_orange).setOnClickListener(this);
        findViewById(R.id.layout_red).setOnClickListener(this);
        findViewById(R.id.layout_green).setOnClickListener(this);
    }

    public void setOnSelectColorListener(ColorDialogListener colorListener){
        this.colorDialogListener = colorListener;
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        int color = 0;

        switch (id) {

            case R.id.layout_black:
                 color = activity.getResources().getColor(android.R.color.black);
                 break;

            case R.id.layout_blue:
                color = activity.getResources().getColor(R.color.colorBlue);
                break;

            case R.id.layout_orange:
                color = activity.getResources().getColor(android.R.color.holo_orange_dark);
                break;

            case R.id.layout_red:
                color = activity.getResources().getColor(R.color.colorRed);
                break;

            case R.id.layout_green:
                color = activity.getResources().getColor(R.color.colorGreen);
                break;
        }
        colorDialogListener.onSelectColor(color);
        dismiss();
    }
}
