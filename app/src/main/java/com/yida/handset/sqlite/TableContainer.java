package com.yida.handset.sqlite;

import android.provider.BaseColumns;

/**
 * Created by gujiao on 2015/10/20.
 */
public interface TableContainer extends BaseColumns {

    String TABLE_NAME = "container";

    String CONTAINERID = "containerId";
    String CODE = "code";
    String TYPE = "type";
    String FRAMEID = "frameId";
    String REMARK = "remark";
    String CREATETIME = "createTime";
    String CREATEBY = "createBy";
    String UPDATETIME = "updateTime";
    String UPDATEBY = "updateBy";

}
