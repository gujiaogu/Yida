package com.yida.handset;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PwdProtectActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.spinner_question)
    Spinner mSpinnerQuestion;
    @Bind(R.id.answer)
    MaterialEditText mAnswer;
    @Bind(R.id.next_btn)
    Button mNextBtn;

    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_protect);
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

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.pwd_protect));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerQuestion.setAdapter(adapter);
        mNextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                Intent intent = new Intent(this, ForgetActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
