package com.yida.handset.sqlite;

import android.provider.BaseColumns;

/**
 * Created by gujiao on 2015/10/24.
 */
public interface TableWorkOrder extends BaseColumns{
    String TABLE_NAME = "workorder";
    String WORKID = "workId";
    String ORDER_TYPE = "orderType";
    String SITE_NAME = "siteName";
    String ORDER_STATUS = "orderStatus";
    String DATE_COMPLETED = "dateCompleted";
    String REMARK = "remark";
    String USERNAME = "username";
}
