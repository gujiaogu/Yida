package com.yida.handset.entity;

/**
 * Created by Administrator on 2015/10/15.
 */
public class OpticalRoute implements OpticalItem {

    public static final String[] OPERATE = {"新装", "拆除", "更改"};
    public static final String[] ROUTE_TYPE = {"双端跳接", "单端跳接", "尾纤型光分路由器跳接"};

    private static final int OPERATE_NEW = 0; //新装
    private static final int OPERATE_DISMANTLE = 1; //拆除
    private static final int OPERATE_MODIFY = 2; //更改
    private static final int ROUTE_TWO = 0; //双端跳接
    private static final int ROUTE_ONE = 1; //单端跳接
    private static final int ROUTE_TAIL = 2; //尾纤型光分路由器跳接

    private String aDeviceName;
    private String aDeviceId;
    private String aFrameNo;
    private String aBoardNo;
    private int aPortNo;
    private String zDeviceName;
    private String zDeviceId;
    private String zFrameNo;
    private String zBoardNo;
    private int zPortNo;
    private int operateType;
    private String splittingRatio;
    private int routeType;
    private int passageId;
    private int id;

    public String getaDeviceName() {
        return aDeviceName;
    }

    public void setaDeviceName(String aDeviceName) {
        this.aDeviceName = aDeviceName;
    }

    public String getaDeviceId() {
        return aDeviceId;
    }

    public void setaDeviceId(String aDeviceId) {
        this.aDeviceId = aDeviceId;
    }

    public String getaFrameNo() {
        return aFrameNo;
    }

    public void setaFrameNo(String aFrameNo) {
        this.aFrameNo = aFrameNo;
    }

    public String getaBoardNo() {
        return aBoardNo;
    }

    public void setaBoardNo(String aBoardNo) {
        this.aBoardNo = aBoardNo;
    }

    public String getzFrameNo() {
        return zFrameNo;
    }

    public void setzFrameNo(String zFrameNo) {
        this.zFrameNo = zFrameNo;
    }

    public String getzBoardNo() {
        return zBoardNo;
    }

    public void setzBoardNo(String zBoardNo) {
        this.zBoardNo = zBoardNo;
    }

    public int getaPortNo() {
        return aPortNo;
    }

    public void setaPortNo(int aPortNo) {
        this.aPortNo = aPortNo;
    }

    public String getzDeviceName() {
        return zDeviceName;
    }

    public void setzDeviceName(String zDeviceName) {
        this.zDeviceName = zDeviceName;
    }

    public String getzDeviceId() {
        return zDeviceId;
    }

    public void setzDeviceId(String zDeviceId) {
        this.zDeviceId = zDeviceId;
    }

    public int getzPortNo() {
        return zPortNo;
    }

    public void setzPortNo(int zPortNo) {
        this.zPortNo = zPortNo;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public String getSplittingRatio() {
        return splittingRatio;
    }

    public void setSplittingRatio(String splittingRatio) {
        this.splittingRatio = splittingRatio;
    }

    public int getPassageId() {
        return passageId;
    }

    public void setPassageId(int passageId) {
        this.passageId = passageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }
}
