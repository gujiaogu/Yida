package com.yida.handset;

/**
 * Created by Administrator on 2015/10/12.
 */
public class Constants {
    public static final String NO_USE = "http://120.26.126.205:9080/l10000/index";
    public static final String NO_USE1 = "http://120.26.126.205:9080/l10000/swagger/index.html";
    public static final String SUPER_USER = "super";
    public static final String SUPER_PASSWORD = "1";
    public static String IP = "";
    public static String PORT = "";
    public static String SYSTEM_NAME = "/l10000";
    public static String HTTP_HEAD = "https://";
    public static String LOGIN = "/client/user/login.json";
    public static String CHECK_UPDATE = "/appversion/getCurrentVersion.json";
    public static String DOWNLOAD_APP = "";
    public static String MODIFY_PASSWORD = "/client/user/modifyPassword.json";
    public static String FORGET_PASSWORD = "/client/user/backPassword.json";
    public static String GET_RESOURCES = "/client/resource/getResourcesData.json";
    public static String GET_WORKORDER = "/client/workOrder/getWorkList.json";
    public static String GET_CONSTRUCT_ORDER = "/client/workOrder/getConstrucationOrder.json";
    public static String GET_INSPECT_ORDER = "/client/workOrder/getInspectOrder.json";
    public static String GET_ETAG_WRITE_ORDER = "/client/workOrder/getWriteLabelOrder.json";
    public static String GET_CONFIGURATION_ORDER = "/client/workOrder/downloadConfigAssignment.json";
    public static String GET_COLLECT_ORDER = "/client/workOrder/downloadDataGettingAssignment.json";
    public static String ACCEPT_ORDER = "/client/workOrder/changeStatus.json";
    public static String REJECT_ORDER = "/client/workOrder/rejectOrder.json";
    public static String COMPLETE_CONSTRUCT_ORDER = "/client/workOrder/backConstructionOrder.json";
    public static String COMPLETE_INSPECT_ORDER = "/client/workOrder/backInspectOrder.json";
    public static String COMPLETE_ETAG_WRITE_ORDER = "/client/workOrder/backWriteLabelOrder.json";
}
