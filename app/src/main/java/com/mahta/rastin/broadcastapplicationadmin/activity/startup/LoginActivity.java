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
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layoutLogin;
    private EditTextPlus edtEmail, edtPass;
    private String pass, email;
    private AVLoadingIndicatorView indicator;
    private TextViewPlus txtLogin;
    boolean serverResponsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_pass);
        txtLogin = findViewById(R.id.txt_login);
        layoutLogin = findViewById(R.id.layout_login);

        indicator = findViewById(R.id.login_loader);

        layoutLogin.setOnClickListener(this);

        //setting tollbar title
        ((TextViewPlus) findViewById(R.id.txtTitle)).setText("پنل مدیریت");

    }

    @Override
    public void onClick(View v) {

        pass = edtPass.getText().toString().trim();
        email = edtEmail.getText().toString().trim();

        if (pass.isEmpty() || email.isEmpty()) {

            G.toastShort("اطلاعات ورودی ناکافی است", LoginActivity.this);
            return;
        }

        //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!serverResponsed) {
                    changeLoadingResource(1);
                }
            }
        }, Constant.SERVER_RESPONSE_TIME);


        authenticate();

    }

    private void authenticate() {

        changeLoadingResource(0);

        if (!G.isNetworkAvailable(LoginActivity.this)) {

            G.toastLong("عدم اتصال به اینترنت", LoginActivity.this);

            changeLoadingResource(1);
        } else {

            final ContentValues contentValues = new ContentValues();

            contentValues.put(Keys.KEY_EMAIL, email);
            contentValues.put(Keys.KEY_PASSWORD, pass);

            new HttpCommand(HttpCommand.COMMAND_LOGIN, contentValues).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    serverResponsed = true;

                    switch (JSONParser.getResultCodeFromJson(result)) {

                        case 1000:
                            RealmController.getInstance().addUserToken(JSONParser.parseToken(result));

                            contentValues.clear();
                            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());

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

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            break;

                        case 1102:

                            G.toastShort("رمز عبور غیر قابل قبول است", LoginActivity.this);
                            changeLoadingResource(1);
                            break;

                        case 1113:

                            G.toastShort("ایمیل غیر قابل قبول است", LoginActivity.this);
                            changeLoadingResource(1);
                            break;
                    }

                }
            }).execute();

        }
    }

    private void changeLoadingResource(int state) {

        switch (state) {
            case 0:
                indicator.setVisibility(View.VISIBLE);
                txtLogin.setVisibility(View.GONE);
                break;

            case 1:
                indicator.setVisibility(View.GONE);
                txtLogin.setVisibility(View.VISIBLE);
                break;
        }
    }
}
