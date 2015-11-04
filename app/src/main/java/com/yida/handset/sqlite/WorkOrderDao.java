package com.yida.handset.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.LoginActivity;
import com.yida.handset.entity.User;
import com.yida.handset.entity.WorkOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujiao on 2015/10/24.
 */
public class WorkOrderDao {

    private Context context;
    private DatabaseHelper helper;
    private String username;

    public WorkOrderDao(Context context) {
        this.context = context;
        if (helper == null) {
            helper = DatabaseHelper.getInstance(this.context);
        }
        SharedPreferences preferences = this.context.getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();
        User user = gson.fromJson(userStr, new TypeToken<User>(){}.getType());
        this.username = user.getUsername();
    }

    public List<WorkOrder> queryAll(String selection, String[] selections, String groupBy, String having, String orderBy) {
        List<WorkOrder> list = new ArrayList<>();
        helper.lock();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query(TableWorkOrder.TABLE_NAME, null, selection, selections, groupBy, having,orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            WorkOrder order;
            while (cursor.moveToNext()) {
                order = new WorkOrder();
                order.setWorkId(cursor.getInt(cursor.getColumnIndex(TableWorkOrder.WORKID)));
                order.setOrderType(cursor.getString(cursor.getColumnIndex(TableWorkOrder.ORDER_TYPE)));
                order.setSiteName(cursor.getString(cursor.getColumnIndex(TableWorkOrder.SITE_NAME)));
                order.setDateCompleted(cursor.getString(cursor.getColumnIndex(TableWorkOrder.DATE_COMPLETED)));
                order.setStatus(cursor.getString(cursor.getColumnIndex(TableWorkOrder.ORDER_STATUS)));
                order.setRemark(cursor.getString(cursor.getColumnIndex(TableWorkOrder.REMARK)));
                order.setUsername(cursor.getString(cursor.getColumnIndex(TableWorkOrder.USERNAME)));
                order.setAssignerName(cursor.getString(cursor.getColumnIndex(TableWorkOrder.ASSIGNERNAME)));
                list.add(order);
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

    public void insert(List<WorkOrder> data) {
        helper.lock();
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values;
        for (WorkOrder workOrder : data) {
            values = new ContentValues();
            values.put(TableWorkOrder.WORKID, workOrder.getWorkId());
            values.put(TableWorkOrder.ORDER_TYPE, workOrder.getOrderType());
            values.put(TableWorkOrder.SITE_NAME, workOrder.getSiteName());
            values.put(TableWorkOrder.DATE_COMPLETED, workOrder.getDateCompleted());
            values.put(TableWorkOrder.ORDER_STATUS, workOrder.getStatus());
            values.put(TableWorkOrder.REMARK, workOrder.getRemark());
            values.put(TableWorkOrder.USERNAME, username);
            values.put(TableWorkOrder.ASSIGNERNAME, workOrder.getAssignerName());
            Cursor cursor = db.query(TableWorkOrder.TABLE_NAME, null, TableWorkOrder.WORKID + "=?", new String[]{String.valueOf(workOrder.getWorkId())}, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                db.update(TableWorkOrder.TABLE_NAME, values, TableWorkOrder.WORKID + "=?", new String[]{String.valueOf(workOrder.getWorkId())});
                continue;
            }
            db.insert(TableWorkOrder.TABLE_NAME, null, values);
        }
        db.close();
        helper.unlock();
    }

    public int update(ContentValues values, String where) {
        helper.lock();
        SQLiteDatabase db = helper.getWritableDatabase();
        int reslut = db.update(TableWorkOrder.TABLE_NAME, values, where, null);
        db.close();
        helper.unlock();
        return reslut;
    }
}
