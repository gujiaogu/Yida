package com.yida.handset.slide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.DrawerAction;
import com.yida.handset.LoginActivity;
import com.yida.handset.R;
import com.yida.handset.entity.LogEntity;
import com.yida.handset.entity.User;
import com.yida.handset.sqlite.LogDao;

import java.util.Date;

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
                String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(LoginActivity.REFERENCE_USER, "");
                editor.apply();

                LogDao dao = new LogDao(context);
                Gson gson = new Gson();
                User userEntity = gson.fromJson(userStr, new TypeToken<User>() {}.getType());
                LogEntity entity = new LogEntity();
                entity.setUsername(userEntity.getUsername());
                entity.setTime(LoginActivity.format.format(new Date()));
                entity.setType(LogEntity.TYPE_LOGOUT);
                dao.insert(entity);

                ((Activity) context).finish();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        }, context.getResources().getInteger(R.integer.post_delay));

    }
}
