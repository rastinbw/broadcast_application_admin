package com.mahta.rastin.broadcastapplicationadmin.activity.startup;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.main.MainActivity;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private ContentValues contentValues;
    Intent intent;
    private LinearLayout lnlNoNetwork;
    private AVLoadingIndicatorView indicator;
    boolean serverResponsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lnlNoNetwork = findViewById(R.id.layout_noNetwork);
        indicator = findViewById(R.id.loader);

        checkToken();

    }

    private void checkToken() {

        //check if token exists
        if (RealmController.getInstance().hasUserToken()) {

            //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!serverResponsed) {
                        handleFailedConnection();
                    }
                }
            }, Constant.SERVER_RESPONSE_TIME);

            validateToken();

        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void validateToken() {

        if (!G.isNetworkAvailable(SplashActivity.this)) {

            G.toastLong("عدم اتصال به اینترنت", SplashActivity.this);

            handleFailedConnection();

        } else {

            contentValues = new ContentValues();
            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());

            new HttpCommand(HttpCommand.COMMAND_CHECK_TOKEN, contentValues).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

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
                                startIntent(intent);
                            }
                        }).execute();

                    } else {
                        G.i("Token is incorrect");

                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startIntent(intent);
                    }

                }
            }).execute();

        }
    }

    private void startIntent(Intent intent) {

        if (serverResponsed) {

            startActivity(intent);
            finish();
        } else {

            handleFailedConnection();
        }
    }

    private void handleFailedConnection() {

        changeLoadingResource(0);

        findViewById(R.id.btn_tryagain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeLoadingResource(1);
                checkToken();
            }
        });
    }

    private void changeLoadingResource(int state) {

        switch (state) {
            case 0:
                lnlNoNetwork.setVisibility(View.VISIBLE);
                indicator.setVisibility(View.GONE);
                break;

            case 1:
                lnlNoNetwork.setVisibility(View.GONE);
                indicator.setVisibility(View.VISIBLE);
                break;
        }
    }

}
