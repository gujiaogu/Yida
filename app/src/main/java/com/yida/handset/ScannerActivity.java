package com.yida.handset;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.Result;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    @Bind(R.id.scanner_view)
    ZXingScannerView mScannerView;
    @Bind(R.id.toolbar_scanner)
    Toolbar mToolbar;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
    }

    @Override
    protected void onResume() {
        mScannerView.setResultHandler(this);
        super.onResume();
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_scanner, menu);
        return true;
    }

    @Override
    public void handleResult(Result result) {
        LogWrapper.d(result.getText());
        LogWrapper.d(result.getBarcodeFormat().toString());
        new ScannerAsyncTask().execute(result);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }

    private class ScannerAsyncTask extends AsyncTask<Result, Void, Void> {
        @Override
        protected Void doInBackground(Result... results) {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ScannerActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            Intent data = new Intent();
            setResult(AppCompatActivity.RESULT_OK, data);
            finish();
        }
    }
}
