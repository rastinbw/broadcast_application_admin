package com.mahta.rastin.broadcastapplicationadmin.activity.main.post_activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.model.Post;

public class PostActivity extends AppCompatActivity implements PostListFragment.CallBacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        PostListFragment postListFragment = new PostListFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flayout_post, postListFragment)
                .commit();


    }

    @Override
    public void onBackPressed(){

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {

            G.i("pop");
            fm.popBackStack();

        } else {
            finish();
        }
    }

    @Override
    public void onItemSelected(Post post) {

        PostAddEditFragment postAddEditFragment = PostAddEditFragment.newInstance(post);

        getSupportFragmentManager().beginTransaction().replace(R.id.flayout_post, postAddEditFragment).commit();
    }
}
