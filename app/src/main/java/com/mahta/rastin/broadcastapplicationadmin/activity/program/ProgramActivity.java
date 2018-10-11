package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnFragmentActionListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

public class ProgramActivity extends AppCompatActivity {

    InsertProgramFragment insertProgramFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment);

        Program program = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);


        if (program == null) {

            insertProgramFragment = InsertProgramFragment.newInstance(null);
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, insertProgramFragment).commit();
        } else {

            insertProgramFragment = InsertProgramFragment.newInstance(program);
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, insertProgramFragment).commit();
        }

        insertProgramFragment.setOnNextListener(new OnFragmentActionListener() {
            @Override
            public void onAction(View view, Bundle data) {

                // if updating
                if (data.getBoolean(Keys.KEY_UPDATE)) {

                    ProgramEditContentFragment fragment = ProgramEditContentFragment.newInstance(data);
                    replaceFragment(fragment);

                } else { //if creating

                    ProgramCreateContentFragment fragment = ProgramCreateContentFragment.newInstance(data);
                    replaceFragment(fragment);
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        //takes care of fragment back stack
        super.onBackPressed();

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