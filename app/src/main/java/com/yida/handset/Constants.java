package com.yida.handset;

/**
 * Created by Administrator on 2015/10/12.
 */
public class Constants {
    private static final String MRUL = "http://120.26.126.205:9080/l10000";
    public static final String LOGIN = MRUL + "/client/user/login.json";
    public static final String MODIFY_PASSWORD = MRUL + "/client/user/modifyPassword.json";
    public static final String FORGET_PASSWORD = MRUL + "/client/user/backPassword.json";
    public static final String GET_RESOURCES = MRUL + "/client/resource/getResourcesData.json";
    public static final String GET_WORKORDER = MRUL + "/client/workOrder/getWorkList.json";
}
