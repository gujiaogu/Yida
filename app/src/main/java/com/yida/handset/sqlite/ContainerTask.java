package com.yida.handset.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.yida.handset.ResourceActivity;
import com.yida.handset.entity.ContainerVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujiao on 2015/10/22.
 */
public class ContainerTask extends AsyncTask<String, Void, List<ContainerVo>> {

    private DatabaseHelper helper;
    private TaskManager listener;

    public ContainerTask(DatabaseHelper helper, TaskManager listener) {
        this.helper = helper;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<ContainerVo> containerVos) {
        super.onPostExecute(containerVos);
        this.listener.notifyFiberboxTask();
    }

    @Override
    protected List<ContainerVo> doInBackground(String... params) {
        List<ContainerVo> containers = new ArrayList<>();
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(TableContainer.TABLE_NAME, null, TableContainer.FRAMEID + "=?", new String[]{params[0]}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                ContainerVo container;
                while (cursor.moveToNext()) {
                    container = new ContainerVo();
                    container.setCreateTime(cursor.getString(cursor.getColumnIndex(TableContainer.CREATETIME)));
                    container.setRemark(cursor.getString(cursor.getColumnIndex(TableContainer.REMARK)));
                    container.setType(cursor.getString(cursor.getColumnIndex(TableContainer.TYPE)));
                    container.setCode(cursor.getString(cursor.getColumnIndex(TableContainer.CODE)));
                    container.setContainerId(cursor.getInt(cursor.getColumnIndex(TableContainer.CONTAINERID)));
                    container.setCreateBy(cursor.getInt(cursor.getColumnIndex(TableContainer.CREATEBY)));
                    container.setFrameId(cursor.getInt(cursor.getColumnIndex(TableContainer.FRAMEID)));
                    container.setUpdateBy(cursor.getInt(cursor.getColumnIndex(TableContainer.UPDATEBY)));
                    container.setUpdateTime(cursor.getString(cursor.getColumnIndex(TableContainer.UPDATETIME)));
                    containers.add(container);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (containers.size() > 0) {
            ResourceActivity.containers = containers;
            ResourceActivity.containersSpinner.clear();
            for (ContainerVo containerVo : ResourceActivity.containers) {
                ResourceActivity.containersSpinner.add(String.valueOf(containerVo.getContainerId()));
            }
        }
        return containers;
    }
}
