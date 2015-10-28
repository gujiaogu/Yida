package com.yida.handset.slide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.yida.handset.DrawerAction;
import com.yida.handset.LoginActivity;
import com.yida.handset.R;

/**
 * Created by gujiao on 2015/10/27.
 */
public class ExitAction implements DrawerAction {
    @Override
    public void act(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = context.getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(LoginActivity.REFERENCE_USER, "");
                editor.apply();
                ((Activity) context).finish();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        }, context.getResources().getInteger(R.integer.post_delay));

    }
}
