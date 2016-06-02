package com.yida.handset.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.LogWrapper;
import com.yida.handset.LoginActivity;
import com.yida.handset.entity.LogEntity;
import com.yida.handset.entity.User;
import com.yida.handset.entity.WorkOrder;

import java.util.List;

/**
 * Created by gujiao on 2016/5/28.
 */
public class WorkOrderDetailDao {

    private Context context;
    private DatabaseHelper helper;
    private String username;

    public WorkOrderDetailDao(Context context) {
        this.context = context;
        if (helper == null) {
            helper = DatabaseHelper.getInstance(this.context);
        }
//        SharedPreferences preferences = this.context.getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
//        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
//        Gson gson = new Gson();
//        User user = gson.fromJson(userStr, new TypeToken<User>(){}.getType());
//        this.username = user.getUsername();
    }

    public void insert(int workId, String str) {
        helper.lock();
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableWorkOrderDetail.WORK_ORDER_ID, workId);
        values.put(TableWorkOrderDetail.DETAIL_JSON_STR, str);
        Cursor cursor = db.query(TableWorkOrderDetail.TABLE_NAME, null, TableWorkOrderDetail.WORK_ORDER_ID + "=?", new String[]{String.valueOf(workId)}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            db.update(TableWorkOrderDetail.TABLE_NAME, values, TableWorkOrderDetail.WORK_ORDER_ID + "=?", new String[]{String.valueOf(workId)});
        } else {
            long id = db.insert(TableWorkOrderDetail.TABLE_NAME, null, values);
            LogWrapper.d(String.valueOf(id));
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        helper.unlock();
    }

    public String query(int workId) {
        String result = "";
        helper.lock();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(TableWorkOrderDetail.TABLE_NAME, null, TableWorkOrderDetail.WORK_ORDER_ID + "=?", new String[]{String.valueOf(workId)}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(TableWorkOrderDetail.DETAIL_JSON_STR));
            }
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.close();
        helper.unlock();
        return result;
    }
}
