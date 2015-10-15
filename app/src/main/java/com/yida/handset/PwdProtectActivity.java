package com.yida.handset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Spinner;
import com.yida.handset.entity.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PwdProtectActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String ANSWER = "answer";
    public static final String QUESTION = "question";
    public static final String[] FORGOT_PASSWORD_QUESTION_LIST = {"你的父亲叫什么名字？","你的母亲叫什么名字？","你的出生地在哪里？","你就读的小学叫什么名字？","你就读的高中叫什么名字？",
            "你最喜欢的歌手是谁？","你的初恋是在多少岁？","你的身高是多少厘米？","你最喜欢的服装品牌是哪个？","你穿多少码的鞋子？"};

    @Bind(R.id.spinner_question)
    Spinner mSpinnerQuestion;
    @Bind(R.id.answer)
    MaterialEditText mAnswer;
    @Bind(R.id.next_btn)
    Button mNextBtn;

    private ArrayAdapter adapter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ForgetActivity.PWD_RESET.equals(intent.getAction())) {
                finish();
            }
        }
    };

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

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, FORGOT_PASSWORD_QUESTION_LIST);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerQuestion.setAdapter(adapter);
        mNextBtn.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ForgetActivity.PWD_RESET);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                checkQuestion();
                break;
            default:
                break;
        }
    }

    private void checkQuestion() {
        String selectedQuestion = mSpinnerQuestion.getSelectedItem().toString();
        String answer = mAnswer.getText().toString().trim();

        Intent intent = new Intent(this, ForgetActivity.class);
        intent.putExtra(ANSWER, answer);
        intent.putExtra(QUESTION, selectedQuestion);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
