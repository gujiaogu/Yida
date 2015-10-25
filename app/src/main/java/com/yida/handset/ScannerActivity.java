package com.yida.handset;

import android.app.ProgressDialog;
import android.content.Intent;
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

    public static final String SCANNER_TAG_RESULT = "scanner_result";

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

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Intent data = new Intent();
//                setResult(AppCompatActivity.RESULT_OK, data);
                finish();
            }
        });
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
//        new ScannerAsyncTask().execute(result);
        Intent data = new Intent();
        data.putExtra(SCANNER_TAG_RESULT, result.getText());
        setResult(AppCompatActivity.RESULT_OK, data);
        finish();
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

}
