package com.yida.handset.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.yida.handset.ResourceActivity;
import com.yida.handset.entity.FiberboxVo;

import java.util.ArrayList;
import java.util.List;

class FiberboxTask extends AsyncTask<String, Void, List<FiberboxVo>> {

    private DatabaseHelper helper;
    private TaskManager listener;

    public FiberboxTask(DatabaseHelper helper, TaskManager listener) {
        this.helper = helper;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<FiberboxVo> fiberboxVos) {
        super.onPostExecute(fiberboxVos);
        listener.notifyPortTask();
    }

    @Override
    protected List<FiberboxVo> doInBackground(String... params) {
        List<FiberboxVo> fiberboxes = new ArrayList<>();
        try {
            helper.lock();
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(Fiberbox.TABLE_NAME, null, Fiberbox.CONTAINERID + "=?" ,new String[]{params[0]} ,null ,null ,null);
            if (cursor != null && cursor.getCount() > 0) {
                FiberboxVo fiberbox;
                while (cursor.moveToNext()) {
                    fiberbox = new FiberboxVo();
                    fiberbox.setCreateTime(cursor.getString(cursor.getColumnIndex(Fiberbox.CREATETIME)));
                    fiberbox.setUpdateTime(cursor.getString(cursor.getColumnIndex(Fiberbox.UPDATETIME)));
                    fiberbox.setCreateBy(cursor.getInt(cursor.getColumnIndex(Fiberbox.CREATEBY)));
                    fiberbox.setUpdateBy(cursor.getInt(cursor.getColumnIndex(Fiberbox.UPDATEBY)));
                    fiberbox.setContainerId(cursor.getInt(cursor.getColumnIndex(Fiberbox.CONTAINERID)));
                    fiberbox.setCode(cursor.getString(cursor.getColumnIndex(Fiberbox.CODE)));
                    fiberbox.setErrInfo(cursor.getString(cursor.getColumnIndex(Fiberbox.ERRINFO)));
                    fiberbox.setFiberboxId(cursor.getInt(cursor.getColumnIndex(Fiberbox.FIBERBOXID)));
                    fiberbox.setHoleNum(cursor.getInt(cursor.getColumnIndex(Fiberbox.HOLENUM)));
                    fiberbox.setManufacture(cursor.getString(cursor.getColumnIndex(Fiberbox.MANUFACTURE)));
                    fiberbox.setMsId(cursor.getInt(cursor.getColumnIndex(Fiberbox.MSID)));
                    fiberbox.setProductDate(cursor.getString(cursor.getColumnIndex(Fiberbox.PRODUCTDATE)));
                    fiberbox.setRemark(cursor.getString(cursor.getColumnIndex(Fiberbox.REMARK)));
                    fiberbox.setType(cursor.getString(cursor.getColumnIndex(Fiberbox.TYPE)));
                    fiberbox.setWorkStatus(cursor.getString(cursor.getColumnIndex(Fiberbox.WORKSTATUS)));
                    fiberboxes.add(fiberbox);
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
        if(fiberboxes.size() > 0) {
            ResourceActivity.fiberboxes = fiberboxes;
            ResourceActivity.fiberboxesSpinner.clear();
            for (FiberboxVo fiberboxVo : ResourceActivity.fiberboxes) {
                ResourceActivity.fiberboxesSpinner.add(String.valueOf(fiberboxVo.getCode()));
            }
        } else {
            ResourceActivity.fiberboxes.clear();
            ResourceActivity.fiberboxesSpinner.clear();
            ResourceActivity.ports.clear();
            ResourceActivity.portsSpinner.clear();
        }
        return fiberboxes;
    }
}
