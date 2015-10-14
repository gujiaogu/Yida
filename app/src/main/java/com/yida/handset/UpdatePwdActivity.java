package com.yida.handset;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdatePwdActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String UPDATE_PWD_TAG = "tag_update_pwd";
    public static final String PWD_UPDATED = "com.yida.handset.action.PWD_UPDATED";

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.old_password)
    MaterialEditText mEditOldPassword;
    @Bind(R.id.new_password)
    MaterialEditText mEditNewPassword;
    @Bind(R.id.new_password_again)
    MaterialEditText mEditNewPasswordAgain;
    @Bind(R.id.confirm_to_modify)
    Button mBtnConfirm;

    private User user;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
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
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();
        user = gson.fromJson(userStr, new TypeToken<User>(){}.getType());

        mBtnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_to_modify:
                confirm();
                break;
            default:
                break;
        }
    }

    public void confirm() {
        String oldPassword = mEditOldPassword.getText().toString().trim();
        final String newPassword = mEditNewPassword.getText().toString().trim();
        String newPasswordAgain = mEditNewPasswordAgain.getText().toString().trim();

        if ("".equals(oldPassword) || "".equals(newPassword)
                || "".equals(newPasswordAgain)) {
            Toast.makeText(this, R.string.please_enter_correctly, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(newPasswordAgain)) {
            Toast.makeText(this, R.string.please_enter_correctly, Toast.LENGTH_SHORT).show();
            return;
        }


        String encryptedPassword = Md5.getMD5Str(oldPassword);
        if (!user.getPassword().equals(encryptedPassword)) {
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
                RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(UPDATE_PWD_TAG);
            }
        });
        pd.show();

        String params = "?oldPwd=" + user.getPassword() + "&newPwd=" + newPassword + "&token=" + user.getToken();
        StringRequest updatePwdRequest = new StringRequest(Request.Method.POST, Constants.MODIFY_PASSWORD + params, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                LoginResult result = gson.fromJson(s, new TypeToken<LoginResult>() {
                    }.getType());
                if (LoginActivity.CODE_SUCCESS.equals(result.getCode())) {
                    user.setPassword(newPassword);
                    String newUserInfo = gson.toJson(user, new TypeToken<LoginResult>() {
                            }.getType());
                    SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(LoginActivity.REFERENCE_USER, newUserInfo);
                    editor.apply();

                    Intent pwdUpdated = new Intent();
                    pwdUpdated.setAction(PWD_UPDATED);
                    sendBroadcast(pwdUpdated);
                    finish();
                } else if(LoginActivity.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(UpdatePwdActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                LogWrapper.d(s);
                dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismiss();
                volleyError.printStackTrace();
            }
        });
        updatePwdRequest.setTag(UPDATE_PWD_TAG);

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(updatePwdRequest);

    }

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

}
