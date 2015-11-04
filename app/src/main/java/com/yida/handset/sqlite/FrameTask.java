package com.yida.handset.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.yida.handset.ResourceActivity;
import com.yida.handset.entity.FrameVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujiao on 2015/10/22.
 */
public class FrameTask extends AsyncTask<String, Void, List<FrameVo>> {

    private DatabaseHelper helper;
    private TaskManager listener;

    public FrameTask(DatabaseHelper helper, TaskManager listener) {
        this.helper = helper;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<FrameVo> frames) {
        super.onPostExecute(frames);
        this.listener.notifyContainerTask();
    }

    @Override
    protected List<FrameVo> doInBackground(String... params) {
        List<FrameVo> frames = new ArrayList<>();
        try {
            helper.lock();
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(TableFrame.TABLE_NAME, null, TableFrame.NETUNITID + "=?", new String[]{params[0]}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                FrameVo frame;
                while (cursor.moveToNext()) {
                    frame = new FrameVo();
                    frame.setCreateTime(cursor.getString(cursor.getColumnIndex(TableFrame.CREATETIME)));
                    frame.setUpdateTime(cursor.getString(cursor.getColumnIndex(TableFrame.UPDATETIME)));
                    frame.setUpdateBy(cursor.getInt(cursor.getColumnIndex(TableFrame.UPDATEBY)));
                    frame.setCode(cursor.getString(cursor.getColumnIndex(TableFrame.CODE)));
                    frame.setEfUid(cursor.getString(cursor.getColumnIndex(TableFrame.EFUID)));
                    frame.setCreateBy(cursor.getInt(cursor.getColumnIndex(TableFrame.CREATEBY)));
                    frame.setErrInfo(cursor.getString(cursor.getColumnIndex(TableFrame.ERRINFO)));
                    frame.setFp(cursor.getInt(cursor.getColumnIndex(TableFrame.FP)));
                    frame.setFrameId(cursor.getInt(cursor.getColumnIndex(TableFrame.FRAME_ID)));
                    frame.setManufacturer(cursor.getString(cursor.getColumnIndex(TableFrame.MANUFACTURER)));
                    frame.setModel(cursor.getString(cursor.getColumnIndex(TableFrame.MODEL)));
                    frame.setNetunitId(cursor.getInt(cursor.getColumnIndex(TableFrame.NETUNITID)));
                    frame.setPosition(cursor.getString(cursor.getColumnIndex(TableFrame.POSITION)));
                    frame.setWorkStatus(cursor.getString(cursor.getColumnIndex(TableFrame.WORKSTATUS)));
                    frame.setRemark(cursor.getString(cursor.getColumnIndex(TableFrame.REMARK)));
                    frame.setType(cursor.getString(cursor.getColumnIndex(TableFrame.TYPE)));
                    frames.add(frame);
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
        if (frames.size() > 0) {
            ResourceActivity.frames = frames;
            ResourceActivity.framesSpinner.clear();
            for (FrameVo frameVo : ResourceActivity.frames) {
                ResourceActivity.framesSpinner.add(String.valueOf(frameVo.getCode()));
            }
        } else {
            ResourceActivity.framesSpinner.clear();
            ResourceActivity.frames.clear();
            ResourceActivity.containers.clear();
            ResourceActivity.containersSpinner.clear();
            ResourceActivity.fiberboxes.clear();
            ResourceActivity.fiberboxesSpinner.clear();
            ResourceActivity.ports.clear();
            ResourceActivity.portsSpinner.clear();
        }
        return frames;
    }
}
