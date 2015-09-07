package com.yida.handset;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_hidden:
                break;
            default:
                break;
        }
    }

    private void login() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
