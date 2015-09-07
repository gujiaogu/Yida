package com.yida.handset;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yida.handset.workorder.ThirdFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ActionWrapper> pages = new ArrayList<>();

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.drawer_list)
    ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pages.add(new ActionWrapper(0, "First", new FirstFragment()));
        pages.add(new ActionWrapper(1, "Second", new SecondFragment()));
        pages.add(new ActionWrapper(2, "Third", new ThirdFragment()));

        pages.get(0).getAction().act(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerList.setAdapter(new DrawerAdapter(this, pages));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawers();
                }
                pages.get(i).getAction().act(MainActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

        }
    }
}
