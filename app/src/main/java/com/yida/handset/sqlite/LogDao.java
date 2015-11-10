package com.yida.handset.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.LoginActivity;
import com.yida.handset.entity.LogEntity;
import com.yida.handset.entity.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by gujiao on 2015/11/10.
 */
public class LogDao {

    private Context context;
    private DatabaseHelper helper;

    public LogDao(Context context) {
        this.context = context;
        helper = DatabaseHelper.getInstance(this.context);
    }

    public List<LogEntity> queryAll() {
        List<LogEntity> list = new ArrayList<>();
        helper.lock();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query(TableLog.TABLE_NAME, null, null, null, null, null,null);
        if (cursor != null && cursor.getCount() > 0) {
            LogEntity log;
            while (cursor.moveToNext()) {
                log = new LogEntity();
                log.setTime(cursor.getString(cursor.getColumnIndex(TableLog.TIME)));
                log.setType(cursor.getString(cursor.getColumnIndex(TableLog.TYPE)));
                log.setUsername(cursor.getString(cursor.getColumnIndex(TableWorkOrder.USERNAME)));
                list.add(log);
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
        return list;
    }

    public void insert(LogEntity entity) {
        helper.lock();
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableLog.TIME, entity.getTime());
        values.put(TableLog.USERNAME, entity.getUsername());
        values.put(TableLog.TYPE, entity.getType());
        db.insert(TableLog.TABLE_NAME, null, values);
        db.close();
        helper.unlock();
    }

    public void clear() {
        helper.lock();
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TableLog.TABLE_NAME, null, null);
        helper.unlock();
    }
}
