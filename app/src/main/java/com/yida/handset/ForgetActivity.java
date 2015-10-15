package com.yida.handset;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yida.handset.entity.LoginResult;
import com.yida.handset.entity.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgetActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String FORGET_PWD_TAG = "tag_forget_pwd";
    public static final String PWD_RESET = "com.yida.handset.action.PWD_RESET";

    @Bind(R.id.enter_new_pwd)
    MaterialEditText mEnterNewPwd;
    @Bind(R.id.confirm_new_pwd)
    MaterialEditText mConfirmNewPwd;
    @Bind(R.id.enter_username)
    MaterialEditText mUsername;
    @Bind(R.id.confirm_new_pwd_btn)
    Button mConfirmBtn;

    private String mQuestion;
    private String mAnswer;

    private ProgressDialog pd;

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
        Intent intent = getIntent();
        mQuestion = intent.getStringExtra(PwdProtectActivity.QUESTION);
        mAnswer = intent.getStringExtra(PwdProtectActivity.ANSWER);

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_new_pwd_btn:
                resetPwd();
                break;
            default:
                break;
        }
    }

    public void resetPwd() {
        String newPwd = mEnterNewPwd.getText().toString().trim();
        String newPwdAgain = mConfirmNewPwd.getText().toString().trim();
        String username = mUsername.getText().toString().trim();
        if ("".equals(newPwd) || "".equals(newPwdAgain) || "".equals(username)) {
            Toast.makeText(this, R.string.please_enter_correctly, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPwd.equals(newPwdAgain)) {
            Toast.makeText(this, R.string.please_enter_correctly, Toast.LENGTH_SHORT).show();
            return;
        }

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(FORGET_PWD_TAG);
            }
        });
        pd.show();

        String quest = "";
        String ans = "";
        try {
            quest = URLEncoder.encode(URLEncoder.encode(mQuestion, "utf-8"), "utf-8");
            ans = URLEncoder.encode(mAnswer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String params = "?question=" + quest + "&answer=" + ans + "&newPwd=" + newPwd + "&loginName=" + username;
        LogWrapper.d(Constants.FORGET_PASSWORD + params);
        StringRequest forgetPwdRequest = new StringRequest(Request.Method.POST, Constants.FORGET_PASSWORD + params, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogWrapper.d(s);
                Gson gson = new Gson();
                LoginResult result = gson.fromJson(s, new TypeToken<LoginResult>() {
                        }.getType());
                if (LoginActivity.CODE_SUCCESS.equals(result.getCode())) {
                    Intent pwdUpdated = new Intent();
                    pwdUpdated.setAction(PWD_RESET);
                    sendBroadcast(pwdUpdated);
                    Toast.makeText(ForgetActivity.this, R.string.pwd_update_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else if(LoginActivity.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(ForgetActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismiss();
                volleyError.printStackTrace();
            }
        });
        forgetPwdRequest.setTag(FORGET_PWD_TAG);

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(forgetPwdRequest);
    }

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }
}
