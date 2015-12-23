package com.yida.handset.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yida.handset.entity.InspectResultEntity;

/**
 * Created by gujiao on 2015/11/10.
 */
public class InspectDao {

    private Context context;
    private DatabaseHelper helper;

    public InspectDao(Context context) {
        this.context = context;
        this.helper = DatabaseHelper.getInstance(this.context);
    }

    public InspectResultEntity query(int workId) {
        InspectResultEntity entity = null;
        helper.lock();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TableInspectResult.TABLE_NAME, null, TableInspectResult.WORKID + "=" + workId, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            entity = new InspectResultEntity();
            entity.setWorkId(cursor.getInt(cursor.getColumnIndex(TableInspectResult.WORKID)));
            entity.setData(cursor.getString(cursor.getColumnIndex(TableInspectResult.DATA)));
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
        return entity;
    }

    public void insert(InspectResultEntity entity) {
        ContentValues values = new ContentValues();
        values.put(TableInspectResult.DATA, entity.getData());
        values.put(TableInspectResult.WORKID, entity.getWorkId());
        helper.lock();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(TableInspectResult.TABLE_NAME, null, TableInspectResult.WORKID + "=" + entity.getWorkId(), null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues value = new ContentValues();
            value.put(TableInspectResult.DATA, entity.getData());
            db.update(TableInspectResult.TABLE_NAME, value, TableInspectResult.WORKID + "=" + entity.getWorkId(), null);
        } else {
            db.insert(TableInspectResult.TABLE_NAME, null, values);
        }
        db.close();
        helper.unlock();
    }

}
