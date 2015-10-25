package com.yida.handset;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.text_station)
    TextView mTextStation;
    @Bind(R.id.text_rack)
    TextView mTextRack;
    @Bind(R.id.text_frame)
    TextView mTextFrame;
    @Bind(R.id.text_disk)
    TextView mTextDisk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String info = getIntent().getStringExtra(ResourceActivity.TAG_INFO);
        try {
            JSONObject object = new JSONObject(info);
            mTextStation.setText(object.getString(ResourceActivity.TAG_STATION));
            mTextRack.setText(object.getString(ResourceActivity.TAG_RACK));
            mTextFrame.setText(object.getString(ResourceActivity.TAG_FRAME));
            mTextDisk.setText(object.getString(ResourceActivity.TAG_DISK));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
