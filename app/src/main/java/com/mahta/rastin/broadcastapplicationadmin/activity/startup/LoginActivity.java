package com.mahta.rastin.broadcastapplicationadmin.activity.startup;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.main.MainActivity;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.UserToken;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditTextPlus edtEmail, edtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_pass);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(this);

        //setting tollbar title
        ((TextViewPlus) findViewById(R.id.txtTitle)).setText("پنل مدیریت");

    }

    @Override
    public void onClick(View v) {

        String pass = edtPass.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        final ContentValues params = new ContentValues();

        params.put("email", email);
        params.put("password", pass);

        new HttpCommand( HttpCommand.COMMAND_LOGIN , params).setOnResultListener(new OnResultListener() {
            @Override
            public void onResult(String result) {

                int resultCode = JSONParser.getResultCodeFromJson(result);

                switch (resultCode) {

                    case 1000:
                        RealmController.getInstance().addUserToken(JSONParser.parseToken(result));

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        break;

                    case 1102:

                        Toast.makeText(LoginActivity.this,"رمز عبور غیر قابل قبول است", Toast.LENGTH_SHORT).show();
                        break;

                    case 1113:

                        Toast.makeText(LoginActivity.this,"نام کاربری غیر قابل قبول است", Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        }).execute();


    }
}
