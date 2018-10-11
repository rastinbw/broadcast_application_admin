package com.mahta.rastin.broadcastapplicationadmin.activity.media;

import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;
import com.mahta.rastin.broadcastapplicationadmin.model.Message;

public class MediaActivity extends AppCompatActivity {

    insertMediaFragment insertMediaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment);

        Media media = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        if (media == null) {
            insertMediaFragment = insertMediaFragment.newInstance(null);
        } else {
            insertMediaFragment = insertMediaFragment.newInstance(media);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, insertMediaFragment).commit();

    }

    private void replaceFragment (Fragment fragment){

        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.

            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.framelayout, fragment, Keys.KEY_FRAGMENT_TAG);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
