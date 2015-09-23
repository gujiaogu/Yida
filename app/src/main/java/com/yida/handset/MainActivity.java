package com.yida.handset;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rey.material.widget.Button;
import com.yida.handset.slide.AboutAction;
import com.yida.handset.slide.UpdateAction;
import com.yida.handset.slide.UpdatePwdAction;
import com.yida.handset.workorder.FragmentWrapper;
import com.yida.handset.workorder.WorkOrderFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final ArrayList<ActionWrapper> mSlideActions = new ArrayList<>();
    static {
        mSlideActions.add(new ActionWrapper(0, "软件升级", new UpdateAction()));
        mSlideActions.add(new ActionWrapper(1, "修改密码", new UpdatePwdAction()));
        mSlideActions.add(new ActionWrapper(2, "关于", new AboutAction()));
    }
    private static final ArrayList<FragmentWrapper> mPages = new ArrayList<>();
    static {
        mPages.add(new FragmentWrapper(0, "First", new ResourceFragment()));
        mPages.add(new FragmentWrapper(1, "Second", new WorkOrderFragment()));
    }

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.drawer_list)
    ListView mDrawerList;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.btn_work_order)
    Button mBtnWorkOrder;
    @Bind(R.id.btn_resource)
    Button mBtnResource;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager.setAdapter(new MainPageAdapter(getSupportFragmentManager(), mPages));

        mDrawerList.setAdapter(new DrawerAdapter(this, mSlideActions));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawers();
                }
                mSlideActions.get(i).getAction().act(MainActivity.this);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mBtnWorkOrder.setOnClickListener(this);
        mBtnResource.setOnClickListener(this);
        mBtnResource.setSelected(true);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.scanner:
                Intent intent = new Intent(this, ScannerActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_work_order:
                mViewPager.setCurrentItem(1);
                mBtnWorkOrder.setSelected(true);
                mBtnResource.setSelected(false);
                break;
            case R.id.btn_resource:
                mViewPager.setCurrentItem(0);
                mBtnResource.setSelected(true);
                mBtnWorkOrder.setSelected(false);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

        }
    }
}
