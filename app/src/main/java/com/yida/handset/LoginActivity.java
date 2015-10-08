package com.yida.handset;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{

    public static final String REFENCE_USERNAME = "yida.username";
    public static final String REFENCE_PASSWORD = "yida.password";
    public static final String REFENCE_NAME = "yida";

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.login_btn)
    Button mBtnLogin;
    @Bind(R.id.switch_hidden)
    CheckBox mSwitch;
    @Bind(R.id.password)
    MaterialEditText mEditTextPwd;
    @Bind(R.id.user_name)
    MaterialEditText mEditTextUsername;
    @Bind(R.id.modify_text)
    TextView mModifyPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBtnLogin.setOnClickListener(this);
        mSwitch.setOnCheckedChangeListener(this);
        mModifyPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.modify_text:
                modifyPwd();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_hidden:
                hiddenPasswordOrNot(compoundButton.isChecked());
                break;
            default:
                break;
        }
    }

    private void login() {
        String userName = mEditTextUsername.getText().toString().trim();
        String password = mEditTextPwd.getText().toString().trim();
        if (userName.equals("") || password.equals("")) {
            Toast.makeText(this, R.string.toast_hint_for_username_pwd, Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences preferences = getSharedPreferences(REFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(REFENCE_USERNAME, userName);
        editor.putString(REFENCE_PASSWORD, password);
        editor.apply();

        Intent intent = new Intent(LoginActivity.this, ResourceActivity.class);
        startActivity(intent);
        finish();
    }

    private void modifyPwd() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, UpdatePwdActivity.class);
                startActivity(intent);
            }
        }, getResources().getInteger(R.integer.post_delay));
    }

    private void hiddenPasswordOrNot(boolean isChecked) {
        if (isChecked) {
            mEditTextPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            mEditTextPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
