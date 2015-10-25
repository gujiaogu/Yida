package com.yida.handset.sqlite;

import android.provider.BaseColumns;

/**
 * Created by gujiao on 2015/10/20.
 */
public interface Fiberbox extends BaseColumns{
    String TABLE_NAME = "firberbox";
    String FIBERBOXID = "fiberboxId";
    String CODE = "code";
    String TYPE = "type";
    String MSID = "msId";
    String MANUFACTURE = "manufacture";
    String PRODUCTDATE = "productDate";
    String HOLENUM = "holeNum";
    String ERRINFO = "errInfo";
    String REMARK = "remark";
    String CONTAINERID = "containerId";
    String WORKSTATUS = "workStatus";
    String CREATETIME = "createTime";
    String CREATEBY = "createBy";
    String UPDATETIME = "updateTime";
    String UPDATEBY = "updateBy";

}
