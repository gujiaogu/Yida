package com.yida.handset.sqlite;

import android.provider.BaseColumns;

/**
 * Created by gujiao on 2015/10/20.
 */
public interface TablePort extends BaseColumns {
    String TABLE_NAME = "port";
    String PORTID = "portId";
    String ETAG = "etag";
    String INDICATOR = "indicator";
    String SEQUENCE = "sequence";
    String REMARK = "remark";
    String FIBERBOXID = "fiberboxId";
    String CREATETIME = "createTime";
    String CREATEBY = "createBy";
    String UPDATETIME = "updateTime";
    String UPDATEBY = "updateBy";
}
