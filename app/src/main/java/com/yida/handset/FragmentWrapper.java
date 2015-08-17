package com.yida.handset;

import android.support.v4.app.Fragment;

public class FragmentWrapper {

    private int index;
    private Fragment fragment;
    private String name;

    public FragmentWrapper(int index, String name, Fragment fragment) {
        this.index = index;
        this.fragment = fragment;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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
}
