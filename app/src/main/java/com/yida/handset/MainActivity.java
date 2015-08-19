package com.yida.handset;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static ArrayList<FragmentWrapper> pages = new ArrayList<>();
    static {
        pages.add(new FragmentWrapper(0,"First", new FirstFragment()));
        pages.add(new FragmentWrapper(1, "Second", new SecondFragment()));
    }

    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private RecyclerView.LayoutManager mLayoutManager;
    private ViewPager mViewPager;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.drawer_list);
        mLayoutManager = new LinearLayoutManager(this);
        mDrawerList.setLayoutManager(mLayoutManager);
        mDrawerList.setAdapter(new DrawerAdapter(pages));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle(getString(R.string.drawer_opened));
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MainPageAdapter(getSupportFragmentManager(), pages));

    }

    private class DrawerAdapter extends RecyclerView.Adapter {

        private List<FragmentWrapper> mDataSet;

        public DrawerAdapter(List<FragmentWrapper> mDataSet) {
            this.mDataSet = mDataSet;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView mTextView;

            public ViewHolder(TextView itemView) {
                super(itemView);
                mTextView = itemView;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ((ViewHolder) viewHolder).mTextView.setText(mDataSet.get(i).getName());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            ViewHolder holder = new ViewHolder((TextView) view);
            return holder;
        }

        @Override
        public int getItemCount() {
            return mDataSet != null ? mDataSet.size() : 0;
        }
    }

    private class MainPageAdapter extends FragmentPagerAdapter {

        private List<FragmentWrapper> listFragments;

        public MainPageAdapter(FragmentManager fm, List<FragmentWrapper> listFragments) {
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

}
