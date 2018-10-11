package com.mahta.rastin.broadcastapplicationadmin.activity.post;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnFragmentActionListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Post;

public class PostActivity extends AppCompatActivity {

    InsertPostFragment insertPostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment);

        Post post = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        if (post == null) {

            insertPostFragment = InsertPostFragment.newInstance(null);
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, insertPostFragment).commit();
        } else {

            insertPostFragment = InsertPostFragment.newInstance(post);
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, insertPostFragment).commit();
        }





        insertPostFragment.setOnNextListener(new OnFragmentActionListener() {
            @Override
            public void onAction(View view, Bundle data) {

                PostEditContentFragment postEditContentFragment = PostEditContentFragment.newInstance(data);
                replaceFragment(postEditContentFragment);

            }
        });

    }

    @Override
    public void onBackPressed() {
        //takes care of fragment back stack
        super.onBackPressed();

//        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(Keys.KEY_FRAGMENT_TAG);
//
//        if (currentFragment instanceof InsertPostFragment) {
//
//            G.i("i m here");
//        }
//
//        if (currentFragment instanceof PostEditContentFragment) {
//            replaceFragment(insertPostFragment);
//
//            G.i("i m here");
//        }

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
