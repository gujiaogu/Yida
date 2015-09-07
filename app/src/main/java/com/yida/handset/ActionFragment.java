package com.yida.handset;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by gujiao on 15-8-21.
 */
public class ActionFragment extends Fragment implements DrawerAction {

    @Override
    public void act(FragmentActivity context) {
        context.getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentPanelLayout, this)
                .commit();
    }
}
