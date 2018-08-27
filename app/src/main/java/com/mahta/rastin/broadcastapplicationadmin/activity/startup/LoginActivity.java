package com.mahta.rastin.broadcastapplicationadmin.activity.startup;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.main.MainActivity;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditTextPlus edtEmail, edtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_pass);

        //setting tollbar title
        ((TextViewPlus) findViewById(R.id.txtTitle)).setText("پنل مدیریت");


        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        //fd98f651272ad756be3fca610e4a3d25  token

        String pass = edtPass.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        ContentValues params = new ContentValues();

        params.put("email", email);
        params.put("password", pass);

//        new HttpCommand( HttpCommand.COMMAND_LOGIN , params ).setOnResultListener(new OnResultListener() {
//            @Override
//            public void onResult(String result) {
//
//                   Log.i("MYTAG", result);
//            }
//        }).execute();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
