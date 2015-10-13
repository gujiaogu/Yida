package com.yida.handset;

import android.content.Context;
import android.content.Intent;
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

    @Bind(R.id.spinner_question)
    Spinner mSpinnerQuestion;
    @Bind(R.id.answer)
    MaterialEditText mAnswer;
    @Bind(R.id.next_btn)
    Button mNextBtn;

    private ArrayAdapter adapter;
    private User user;

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
        user = gson.fromJson(userStr, new TypeToken<User>(){}.getType());

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
                checkQuestion();
                break;
            default:
                break;
        }
    }

    private void checkQuestion() {
        String selectedQuestion = mSpinnerQuestion.getSelectedItem().toString();
        if (!user.getQuestion().equals(selectedQuestion)) {
            Toast.makeText(this, R.string.question_is_not_correct, Toast.LENGTH_SHORT).show();
            return;
        }

        String answer = mAnswer.getText().toString().trim();
        if (!user.getAnswer().equals(Md5.getMD5Str(answer))) {
            Toast.makeText(this, R.string.answer_is_not_correct, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ForgetActivity.class);
        intent.putExtra(ANSWER, answer);
        intent.putExtra(QUESTION, user.getQuestion());
        startActivity(intent);

    }
}
