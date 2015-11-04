package com.yida.handset.sqlite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.yida.handset.ResourceActivity;
import com.yida.handset.entity.PortVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujiao on 2015/10/22.
 */
public class PortTask extends AsyncTask<String, Void, List<PortVo>> {

    public static final String TASK_FINISHED = "com.yida.handset.RESOURCE_TASK_FINISHED";

    private DatabaseHelper helper;
    private TaskManager listener;
    private Context context;

    public PortTask(Context context, DatabaseHelper helper, TaskManager listener) {
        this.helper = helper;
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<PortVo> portVos) {
        super.onPostExecute(portVos);
        Intent intent = new Intent();
        intent.setAction(TASK_FINISHED);
        this.context.sendBroadcast(intent);
    }

    @Override
    protected List<PortVo> doInBackground(String... params) {
        List<PortVo> ports = new ArrayList<>();
        try {
            helper.lock();
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(TablePort.TABLE_NAME, null, TablePort.FIBERBOXID + "=?", new String[]{params[0]}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                PortVo port;
                while (cursor.moveToNext()) {
                    port = new PortVo();
                    port.setCreateTime(cursor.getString(cursor.getColumnIndex(TablePort.CREATETIME)));
                    port.setCreateBy(cursor.getInt(cursor.getColumnIndex(TablePort.CREATEBY)));
                    port.setUpdateBy(cursor.getInt(cursor.getColumnIndex(TablePort.UPDATEBY)));
                    port.setUpdateTime(cursor.getString(cursor.getColumnIndex(TablePort.UPDATETIME)));
                    port.setRemark(cursor.getString(cursor.getColumnIndex(TablePort.REMARK)));
                    port.setEtag(cursor.getString(cursor.getColumnIndex(TablePort.ETAG)));
                    port.setFiberboxId(cursor.getInt(cursor.getColumnIndex(TablePort.FIBERBOXID)));
                    port.setIndicator(cursor.getString(cursor.getColumnIndex(TablePort.INDICATOR)));
                    port.setPortId(cursor.getInt(cursor.getColumnIndex(TablePort.PORTID)));
                    port.setSequence(cursor.getShort(cursor.getColumnIndex(TablePort.SEQUENCE)));
                    ports.add(port);
                }
            }
            if (cursor != null) {
                try{
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            db.close();
            helper.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ports.size() > 0) {
            ResourceActivity.ports = ports;
            ResourceActivity.portsSpinner.clear();
            for (PortVo portVo : ResourceActivity.ports) {
                ResourceActivity.portsSpinner.add(String.valueOf(portVo.getPortId()));
            }
        } else {
            ResourceActivity.ports.clear();
            ResourceActivity.portsSpinner.clear();
        }
        return ports;
    }
}