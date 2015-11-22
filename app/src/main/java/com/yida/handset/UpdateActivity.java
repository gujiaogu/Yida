package com.yida.handset;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import junit.runner.Version;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.check_update)
    Button mBtnCheckUpdate;
    @Bind(R.id.download_new)
    Button mBtnDownloadNew;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBtnCheckUpdate.setVisibility(View.GONE);
            mBtnDownloadNew.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBtnCheckUpdate.setOnClickListener(this);
        mBtnDownloadNew.setOnClickListener(this);

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        int versionCode = preferences.getInt(VersionTask.VERSION_CODE, 0);
        String versionName = preferences.getString(VersionTask.VERSION_NAME, "");

        PackageManager pm = getPackageManager();
        if (versionCode > 0 && !"".equals(versionName)) {
            try {
                PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
                if (info.versionCode < versionCode) {
                    mBtnCheckUpdate.setVisibility(View.GONE);
                    mBtnDownloadNew.setVisibility(View.VISIBLE);
                } else {
                    mBtnCheckUpdate.setVisibility(View.VISIBLE);
                    mBtnDownloadNew.setVisibility(View.GONE);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            mBtnCheckUpdate.setVisibility(View.VISIBLE);
            mBtnDownloadNew.setVisibility(View.GONE);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(VersionTask.ACTION_VERSION_CHECKED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_update:
                new VersionTask(this, VersionTask.TAG_MANUAL).execute();
                break;
            case R.id.download_new:
                download();
                break;
            default:
                break;
        }
    }

    private void download() {
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(Constants.DOWNLOAD_APP);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.allowScanningByMediaScanner();
        long reference = dm.enqueue(request);
        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(VersionTask.DOWNLOAD_REFERENCE, reference);
        editor.apply();
//        Intent intent = new Intent(VersionTask.ACTION_APP_DOWNLOADING);
//        intent.putExtra(VersionTask.DOWNLOAD_REFERENCE, reference);
//        sendBroadcast(intent);
    }
}
