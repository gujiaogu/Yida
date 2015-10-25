package com.yida.handset.sqlite;

import android.provider.BaseColumns;

/**
 * Created by gujiao on 2015/10/20.
 */
public interface TableNetUnit extends BaseColumns {

    String TABLE_NAME = "net_unit";

    String NETUNITID = "netunitId";
    String NAME = "name";
    String TYPE = "type";
    String PROVINCE = "province";
    String CITY = "city";
    String ADDRESS = "address";
    String USERID = "userId";
    String IP = "ip";
    String REMARK = "remark";
    String STATUS = "status";
    String CREATE_TIME = "createTime";
    String CREATEBY = "createBy";
    String UPDATE_TIME = "updateTime";
    String UPDATEBY = "updateBy";
    String URL = "url";
}
