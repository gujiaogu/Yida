package com.yida.handset;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.yida.handset.workorder.SettingActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        final String user = preferences.getString(LoginActivity.REFERENCE_USER, "");
        final String IP = preferences.getString(SettingActivity.PREFERENCE_IP_ADDRESS, "");
        final String port = preferences.getString(SettingActivity.PREFERENCE_PORT, "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                Intent intent = null;
                if ("".equals(IP) || "".equals(port)) {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                } else {
                    Constants.IP = IP;
                    Constants.PORT = port;
                }
                if (user.equals("")) {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, HahaActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }).start();
    }

}
