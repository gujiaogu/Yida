package com.yida.handset.slide;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.yida.handset.AboutActivity;
import com.yida.handset.DrawerAction;
import com.yida.handset.R;

/**
 * Created by gujiao on 15-9-11.
 */
public class AboutAction implements DrawerAction {
    @Override
    public void act(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, AboutActivity.class);
                context.startActivity(intent);
            }
        }, context.getResources().getInteger(R.integer.post_delay));

    }
}
