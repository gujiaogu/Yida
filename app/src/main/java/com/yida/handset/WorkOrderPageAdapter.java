package com.yida.handset;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by gujiao on 15-8-19.
 */
public class WorkOrderPageAdapter extends FragmentPagerAdapter {

    private List<FragmentWrapper> listFragments;

    public WorkOrderPageAdapter(FragmentManager fm, List<FragmentWrapper> listFragments) {
        super(fm);
        this.listFragments = listFragments;
    }

    @Override
    public int getCount() {
        return listFragments != null ? listFragments.size() : 0;
    }

    @Override
    public Fragment getItem(int position) {
        return (listFragments != null && listFragments.size() > 0)
                ? listFragments.get(position).getFragment() : null;
    }
}
