package com.yida.handset.workorder;

import android.support.v4.app.Fragment;

/**
 * Created by gujiao on 15-8-21.
 */
public class FragmentWrapper {
    private int index;
    private Fragment fragment;
    private String name;

    public FragmentWrapper(int index, String name, Fragment fragment) {
        this.index = index;
        this.fragment = fragment;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
