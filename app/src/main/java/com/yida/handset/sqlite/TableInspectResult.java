package com.yida.handset.sqlite;

import android.provider.BaseColumns;

/**
 * Created by gujiao on 2015/11/10.
 */
public interface TableInspectResult extends BaseColumns {
    String TABLE_NAME = "table_inspect_result";
    String WORKID = "workId";
    String DATA = "data";
}
