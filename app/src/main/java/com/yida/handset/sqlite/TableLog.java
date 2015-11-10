package com.yida.handset.sqlite;

import android.provider.BaseColumns;

/**
 * Created by gujiao on 2015/11/10.
 */
public interface TableLog extends BaseColumns {
    String TABLE_NAME = "login_log";
    String TIME = "time";
    String TYPE = "type";
    String USERNAME = "username";
}
