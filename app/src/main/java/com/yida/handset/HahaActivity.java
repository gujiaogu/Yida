package com.yida.handset;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.entity.User;
import com.yida.handset.slide.AboutAction;
import com.yida.handset.slide.UpdateAction;
import com.yida.handset.slide.UpdatePwdAction;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HahaActivity extends AppCompatActivity implements View.OnClickListener{

    private static final ArrayList<ActionWrapper> mSlideActions = new ArrayList<>();
    static {
        mSlideActions.add(new ActionWrapper(0, "软件升级", new UpdateAction()));
        mSlideActions.add(new ActionWrapper(1, "修改密码", new UpdatePwdAction()));
        mSlideActions.add(new ActionWrapper(2, "关于", new AboutAction()));
    }

    @Bind(R.id.drawer_list)
    ListView mDrawerList;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.drawer_title)
    TextView mDrawerTitle;
    @Bind(R.id.drawer)
    LinearLayout mDrawer;
    @Bind(R.id.main_action_resource)
    LinearLayout mActionResource;
    @Bind(R.id.main_action_log)
    LinearLayout mActionLog;
    @Bind(R.id.main_action_work_order)
    LinearLayout mActionWorkOder;
    @Bind(R.id.main_action_sync)
    LinearLayout mActionSync;

    private ActionBarDrawerToggle mDrawerToggle;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haha);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();
        user = gson.fromJson(userStr, new TypeToken<User>(){}.getType());
        mDrawerTitle.setText(user.getUsername());

        mDrawerList.setAdapter(new DrawerAdapter(this, mSlideActions));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mDrawerLayout.isDrawerOpen(mDrawer)) {
                    mDrawerLayout.closeDrawers();
                }
                mSlideActions.get(i).getAction().act(HahaActivity.this);
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

        mActionResource.setOnClickListener(this);
        mActionLog.setOnClickListener(this);
        mActionWorkOder.setOnClickListener(this);
        mActionSync.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_action_resource:
                Intent intent = new Intent(this, ResourceActivity.class);
                startActivity(intent);
                break;
            case R.id.main_action_log:
                Intent intent1 = new Intent(this, LogActivity.class);
                startActivity(intent1);
                break;
            case R.id.main_action_work_order:
                Intent intent2 = new Intent(this, WorkOrderActivity.class);
                startActivity(intent2);
                break;
            case R.id.main_action_sync:
                break;
            default:
                break;
        }
    }
}
