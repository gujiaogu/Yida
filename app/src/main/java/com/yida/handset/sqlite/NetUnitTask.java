package com.yida.handset.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.yida.handset.ResourceActivity;
import com.yida.handset.entity.NetUnitVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujiao on 2015/10/21.
 */
public class NetUnitTask extends AsyncTask<Void, Void, List<NetUnitVo>> {

    private DatabaseHelper helper;
    private TaskManager listener;

    public NetUnitTask(DatabaseHelper helper, TaskManager listener) {
        this.helper = helper;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<NetUnitVo> netUnits) {
        super.onPostExecute(netUnits);
        this.listener.notifyFrameTask();
    }

    @Override
    protected List<NetUnitVo> doInBackground(Void... voids) {
        List<NetUnitVo> netUnits = new ArrayList<>();
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(TableNetUnit.TABLE_NAME, null, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                NetUnitVo netUnit;
                while (cursor.moveToNext()) {
                    netUnit = new NetUnitVo();
                    netUnit.setAddress(cursor.getString(cursor.getColumnIndex(TableNetUnit.ADDRESS)));
                    netUnit.setCity(cursor.getString(cursor.getColumnIndex(TableNetUnit.CITY)));
                    netUnit.setCreateBy(cursor.getInt(cursor.getColumnIndex(TableNetUnit.CREATEBY)));
                    netUnit.setCreateTime(cursor.getString(cursor.getColumnIndex(TableNetUnit.CREATE_TIME)));
                    netUnit.setIp(cursor.getString(cursor.getColumnIndex(TableNetUnit.IP)));
                    netUnit.setName(cursor.getString(cursor.getColumnIndex(TableNetUnit.NAME)));
                    netUnit.setNetunitId(cursor.getInt(cursor.getColumnIndex(TableNetUnit.NETUNITID)));
                    netUnit.setProvince(cursor.getString(cursor.getColumnIndex(TableNetUnit.PROVINCE)));
                    netUnit.setRemark(cursor.getString(cursor.getColumnIndex(TableNetUnit.REMARK)));
                    netUnit.setStatus(cursor.getString(cursor.getColumnIndex(TableNetUnit.STATUS)));
                    netUnit.setType(cursor.getString(cursor.getColumnIndex(TableNetUnit.TYPE)));
                    netUnit.setUpdateBy(cursor.getInt(cursor.getColumnIndex(TableNetUnit.UPDATEBY)));
                    netUnit.setUpdateTime(cursor.getString(cursor.getColumnIndex(TableNetUnit.UPDATE_TIME)));
                    netUnit.setUrl(cursor.getString(cursor.getColumnIndex(TableNetUnit.URL)));
                    netUnit.setUserId(cursor.getShort(cursor.getColumnIndex(TableNetUnit.USERID)));
                    netUnits.add(netUnit);
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
        if (netUnits.size() > 0) {
            ResourceActivity.netUnits = netUnits;
            ResourceActivity.netUnitsSpinner.clear();
            for (NetUnitVo netUnitVo : ResourceActivity.netUnits) {
                ResourceActivity.netUnitsSpinner.add(netUnitVo.getName());
            }
        }
        return netUnits;
    }
}
