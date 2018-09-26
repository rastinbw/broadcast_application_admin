package com.mahta.rastin.broadcastapplicationadmin.helper;

import android.app.Activity;
import android.view.View;

import com.mahta.rastin.broadcastapplicationadmin.R;

public class Utils {

    public static void changeLoadingResource(Activity activity, int state) {

        switch (state) {
            case 0:
                activity.findViewById(R.id.apply_loader).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.txtApply).setVisibility(View.GONE);
                break;

            case 1:
                activity.findViewById(R.id.apply_loader).setVisibility(View.GONE);
                activity.findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
                break;
        }
    }


}
