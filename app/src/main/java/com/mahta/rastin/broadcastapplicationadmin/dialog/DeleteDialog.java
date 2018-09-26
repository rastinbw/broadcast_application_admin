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
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDialogDeleteListener;

public class DeleteDialog extends Dialog {

    private OnDialogDeleteListener onDialogDeleteListener;

    private Activity activity;


    public DeleteDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to remove dialog default title space
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_item);

        //adjusting dialog layout size
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        getWindow().setLayout((int) (0.8 * size.x) , ViewGroup.LayoutParams.WRAP_CONTENT);

        //to fix corner radius issue
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //cancel button
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
