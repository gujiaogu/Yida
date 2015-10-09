package com.yida.handset;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgetActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.enter_new_pwd)
    MaterialEditText mEnterNewPwd;
    @Bind(R.id.confirm_new_pwd)
    MaterialEditText mConfirmNewPwd;
    @Bind(R.id.confirm_new_pwd_btn)
    Button mConfirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mConfirmBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_new_pwd_btn:
                break;
            default:
                break;
        }
    }
}
