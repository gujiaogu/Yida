package com.yida.handset.sqlite;

import android.provider.BaseColumns;

/**
 * Created by gujiao on 2015/10/20.
 */
public interface TableFrame extends BaseColumns {
    String TABLE_NAME = "frame";

    String FRAME_ID = "frameId";
    String CODE = "code";
    String TYPE = "type";
    String MODEL = "model";
    String EFUID = "efUid";
    String ERRINFO = "errInfo";
    String FP = "fp";
    String POSITION = "position";
    String MANUFACTURER = "manufacturer";
    String REMARK = "remark";
    String NETUNITID = "netunitId";
    String WORKSTATUS = "workStatus";
    String CREATETIME = "createTime";
    String CREATEBY = "createBy";
    String UPDATETIME = "updateTime";
    String UPDATEBY = "updateBy";
}
