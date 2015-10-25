package com.yida.handset.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yida.handset.entity.FrameVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujiao on 2015/10/23.
 */
public class FrameDao {

    private DatabaseHelper helper;

    public FrameDao(DatabaseHelper helper) {
        this.helper = helper;
    }

    public List<FrameVo> getFrames(String netUnitId) {
        List<FrameVo> frames = new ArrayList<>();
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(TableFrame.TABLE_NAME, null, TableFrame.NETUNITID + "=?", new String[]{netUnitId}, null, null, null);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frames;
    }
}
