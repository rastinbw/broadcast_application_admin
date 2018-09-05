package com.mahta.rastin.broadcastapplicationadmin.activity.startup;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.main.MainActivity;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private Intent intent;
    private ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //check if token exists
        if (RealmController.getInstance().hasUserToken()) {

            contentValues = new ContentValues();
            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());

            new HttpCommand(HttpCommand.COMMAND_CHECK_TOKEN, contentValues).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {

                        G.i("Token is currect");
                        intent = new Intent(SplashActivity.this, MainActivity.class);

                        //getting group list
                        new HttpCommand(HttpCommand.COMMAND_GET_GROUP_LIST, contentValues).setOnResultListener(new OnResultListener() {
                            @Override
                            public void onResult(String result) {

                                RealmController.getInstance().clearAllGroups();
                                List<Group> list = JSONParser.parseGroups(result);

                                if (list != null) {
                                    for (Group group : list) {
                                        RealmController.getInstance().addGroup(group);
                                        G.i(group.getTitle());
                                    }
                                }

                            }
                        }).execute();

                    } else {
                        G.i("Token is incorrect");

                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                }
            }).execute();

        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }



        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();

    }

}
