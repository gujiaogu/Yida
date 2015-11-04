package com.yida.handset.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gujiao on 2015/10/20.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "yida.db";
    private static final int DB_VERSION = 1;
    private static final Lock lock = new ReentrantLock();

    private static DatabaseHelper helper;

    public static DatabaseHelper getInstance(Context context) {
        if (helper == null) {
            helper = new DatabaseHelper(context.getApplicationContext());
        }
        return helper;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql_net_unit = "create table if not exists " + TableNetUnit.TABLE_NAME
                + "(" + TableNetUnit._ID + " integer primary key autoincrement, "
                + TableNetUnit.NETUNITID + " integer, "
                + TableNetUnit.NAME + " text, "
                + TableNetUnit.TYPE + " text, "
                + TableNetUnit.PROVINCE + " text, "
                + TableNetUnit.CITY + " text, "
                + TableNetUnit.ADDRESS + " text, "
                + TableNetUnit.USERID + " integer, "
                + TableNetUnit.IP + " text, "
                + TableNetUnit.REMARK + " text, "
                + TableNetUnit.STATUS + " text, "
                + TableNetUnit.CREATE_TIME + " text, "
                + TableNetUnit.CREATEBY + " integer, "
                + TableNetUnit.UPDATE_TIME + " text, "
                + TableNetUnit.UPDATEBY + " integer, "
                + TableNetUnit.URL + " text"
                +")";
        String sql_frame = "create table if not exists " + TableFrame.TABLE_NAME
                + "(" + TableFrame._ID + " integer primary key autoincrement, "
                + TableFrame.FRAME_ID + " integer, "
                + TableFrame.CODE + " text, "
                + TableFrame.TYPE + " text, "
                + TableFrame.MODEL + " text, "
                + TableFrame.EFUID + " text, "
                + TableFrame.ERRINFO + " text, "
                + TableFrame.FP + " integer, "
                + TableFrame.POSITION + " text, "
                + TableFrame.MANUFACTURER + " text, "
                + TableFrame.REMARK + " text, "
                + TableFrame.NETUNITID + " integer, "
                + TableFrame.WORKSTATUS + " text, "
                + TableFrame.CREATETIME + " text, "
                + TableFrame.CREATEBY + " integer, "
                + TableFrame.UPDATETIME + " text, "
                + TableFrame.UPDATEBY + " integer "
                +")";
        String sql_container = "create table if not exists " + TableContainer.TABLE_NAME
                + "(" + TableContainer._ID + " integer primary key autoincrement, "
                + TableContainer.CONTAINERID + " integer,"
                + TableContainer.CODE + " text,"
                + TableContainer.TYPE + " text,"
                + TableContainer.FRAMEID + " integer,"
                + TableContainer.REMARK + " text,"
                + TableContainer.CREATETIME + " text,"
                + TableContainer.CREATEBY + " integer, "
                + TableContainer.UPDATETIME + " text, "
                + TableContainer.UPDATEBY + " integer "
                +")";
        String sql_fiberbox = "create table if not exists " + Fiberbox.TABLE_NAME
                + "(" + Fiberbox._ID + " integer primary key autoincrement, "
                + Fiberbox.FIBERBOXID + " integer,"
                + Fiberbox.CODE + " text,"
                + Fiberbox.TYPE + " text,"
                + Fiberbox.MSID + " integer,"
                + Fiberbox.MANUFACTURE + " text,"
                + Fiberbox.HOLENUM + " integer,"
                + Fiberbox.PRODUCTDATE + " text,"
                + Fiberbox.ERRINFO + " text,"
                + Fiberbox.REMARK + " text,"
                + Fiberbox.CONTAINERID + " integer,"
                + Fiberbox.WORKSTATUS + " text,"
                + Fiberbox.CREATETIME + " text,"
                + Fiberbox.CREATEBY + " integer, "
                + Fiberbox.UPDATETIME + " text, "
                + Fiberbox.UPDATEBY + " integer "
                +")";
        String sql_port = "create table if not exists " + TablePort.TABLE_NAME
                + "(" + TablePort._ID + " integer primary key autoincrement, "
                + TablePort.PORTID + " integer,"
                + TablePort.ETAG + " text,"
                + TablePort.INDICATOR + " text,"
                + TablePort.SEQUENCE + " integer,"
                + TablePort.REMARK + " text,"
                + TablePort.FIBERBOXID + " integer,"
                + TablePort.CREATETIME + " text,"
                + TablePort.CREATEBY + " integer, "
                + TablePort.UPDATETIME + " text, "
                + TablePort.UPDATEBY + " integer "
                +")";
        String sql_workorder = "create table if not exists " + TableWorkOrder.TABLE_NAME
                + "(" + TableWorkOrder.WORKID + " integer primary key, "
                + TableWorkOrder.SITE_NAME + " text,"
                + TableWorkOrder.ORDER_TYPE + " text,"
                + TableWorkOrder.DATE_COMPLETED + " text,"
                + TableWorkOrder.USERNAME + " text,"
                + TableWorkOrder.ORDER_STATUS + " text,"
                + TableWorkOrder.ASSIGNERNAME + " text,"
                + TableWorkOrder.REMARK + " text"
                +")";
        sqLiteDatabase.execSQL(sql_net_unit);
        sqLiteDatabase.execSQL(sql_frame);
        sqLiteDatabase.execSQL(sql_container);
        sqLiteDatabase.execSQL(sql_fiberbox);
        sqLiteDatabase.execSQL(sql_port);
        sqLiteDatabase.execSQL(sql_workorder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
