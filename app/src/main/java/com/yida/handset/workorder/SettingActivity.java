package com.yida.handset.workorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.yida.handset.Constants;
import com.yida.handset.LoginActivity;
import com.yida.handset.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PREFERENCE_IP_ADDRESS = "ip_address";
    public static final String PREFERENCE_PORT = "port";
    public static final String PREFERENCE_SET_SUCCESS = "set_success";

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.settingIP)
    MaterialEditText mSettingIP;
    @Bind(R.id.settingPort)
    MaterialEditText mSettingPort;
    @Bind(R.id.set_btn)
    Button mSetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mSetBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_btn:
                set();
                break;
            default:
                break;
        }
    }

    private void set() {
        String ip = mSettingIP.getText().toString().trim();
        String port = mSettingPort.getText().toString().trim();
//        if (ip.equals("") || port.equals("") || !isIpv4(ip)) {
//            Toast.makeText(this, R.string.setting_toast_text, Toast.LENGTH_SHORT).show();
//            return;
//        }

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_IP_ADDRESS, ip);
        editor.putString(PREFERENCE_PORT, port);
        editor.apply();
        Constants.IP = ip;
        Constants.PORT = port;

        Intent data = new Intent();
        data.putExtra(PREFERENCE_SET_SUCCESS, true);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    public static boolean isIpv4(String ipAddress) {
        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();

    }
}
