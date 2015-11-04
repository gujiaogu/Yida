package com.yida.handset;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.yida.handset.entity.LoginResult;
import com.yida.handset.workorder.SettingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{

    public static final String REFERENCE_NAME = "yida";
    public static final String REFERENCE_USER = "yida.user";

    public static final String CODE_SUCCESS = "0";
    public static final String CODE_FAILURE = "-1";
    public static final String LOGIN_TAG = "tag_login";
    public static final int REQUEST_CODE_FOR_SET_IP = 1;

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
    @Bind(R.id.forget_pwd)
    TextView mForgetPwd;
    @Bind(R.id.set_ip_text)
    TextView mSetIp;

    private ProgressDialog pd;

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

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String IP = preferences.getString(SettingActivity.PREFERENCE_IP_ADDRESS, "");
        String port = preferences.getString(SettingActivity.PREFERENCE_PORT, "");
        if ("".equals(IP) || "".equals(port)) {
            setIPTextVisible(true);
        }

        mBtnLogin.setOnClickListener(this);
        mSwitch.setOnCheckedChangeListener(this);
        mForgetPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.forget_pwd:
                forgetPwd();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_FOR_SET_IP && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                boolean isSuccess = data.getBooleanExtra(SettingActivity.PREFERENCE_SET_SUCCESS, false);
                if (isSuccess) {
                    setIPTextVisible(false);
                }
            }
        }
    }

    private void login() {
        final String userName = mEditTextUsername.getText().toString().trim();
        final String password = mEditTextPwd.getText().toString().trim();
        if (userName.equals("") || password.equals("")) {
            Toast.makeText(this, R.string.toast_hint_for_username_pwd, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Constants.SUPER_USER.equals(userName)
                && Constants.SUPER_PASSWORD.equals(password)) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivityForResult(intent, REQUEST_CODE_FOR_SET_IP);
            mEditTextUsername.setText("");
            mEditTextPwd.setText("");
            return;
        }

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(LOGIN_TAG);
            }
        });
        pd.show();

        String params = "?loginName=" + userName + "&password=" + password;
        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.LOGIN + params;
        StringRequest mLoginRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                LoginResult result = null;
                try {
                    result = gson.fromJson(response, new TypeToken<LoginResult>() {
                    }.getType());
                } catch (Exception e) {
                    dismiss();
                    e.printStackTrace();
                }
                if (result == null) {
                    dismiss();
                    return;
                }
                if (CODE_SUCCESS.equals(result.getCode())) {
                    SharedPreferences preferences = getSharedPreferences(REFERENCE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    String user = gson.toJson(result.getObject());
                    editor.putString(REFERENCE_USER, user);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, HahaActivity.class);
                    startActivity(intent);
                    finish();
                } else if(CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
        mLoginRequest.setTag(LOGIN_TAG);

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(mLoginRequest);
    }

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    private void forgetPwd() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, PwdProtectActivity.class);
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

    public void setIPTextVisible(boolean visible) {
        if (visible) {
            mSetIp.setVisibility(View.VISIBLE);
        } else {
            mSetIp.setVisibility(View.GONE);
        }
    }
}
