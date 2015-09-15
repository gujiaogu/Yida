package com.yida.handset.slide;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.yida.handset.DrawerAction;
import com.yida.handset.R;
import com.yida.handset.UpdateActivity;

/**
 * Created by gujiao on 15-9-11.
 */
public class UpdateAction implements DrawerAction {
    @Override
    public void act(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, UpdateActivity.class);
                context.startActivity(intent);
            }
        }, context.getResources().getInteger(R.integer.post_delay));
    }
}
