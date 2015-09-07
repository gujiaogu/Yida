package com.yida.handset;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

/**
 * Created by gujiao on 15-8-21.
 */
public class ScannerAction implements DrawerAction {

    @Override
    public void act(FragmentActivity context) {
        Intent intent = new Intent(context, ScannerActivity.class);
        context.startActivity(intent);
    }
}
