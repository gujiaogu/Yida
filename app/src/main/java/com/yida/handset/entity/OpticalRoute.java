package com.yida.handset.entity;

/**
 * Created by Administrator on 2015/10/15.
 */
public class OpticalRoute {

    private static final int OPERATE_NEW = 0; //新装
    private static final int OPERATE_DISMANTLE = 1; //拆除
    private static final int OPERATE_MODIFY = 2; //更改
    private static final int OPERATE_TWO = 3; //双端跳接
    private static final int OPERATE_ONE = 4; //单端跳接
    private static final int OPERATE_TAIL = 5; //尾纤型光分路由器跳接

    private String aDeviceName;
    private String aDeviceId;
    private int aFrameNo;
    private int aBoardNo;
    private int aPortNo;
    private String zDeviceName;
    private String zDeviceId;
    private int zFrameNo;
    private int zBoardNo;
    private int zPortNo;
    private int operateType;
    private String SplittingRatio;
    private int routeType;

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

    public int getaFrameNo() {
        return aFrameNo;
    }

    public void setaFrameNo(int aFrameNo) {
        this.aFrameNo = aFrameNo;
    }

    public int getaBoardNo() {
        return aBoardNo;
    }

    public void setaBoardNo(int aBoardNo) {
        this.aBoardNo = aBoardNo;
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

    public int getzFrameNo() {
        return zFrameNo;
    }

    public void setzFrameNo(int zFrameNo) {
        this.zFrameNo = zFrameNo;
    }

    public int getzBoardNo() {
        return zBoardNo;
    }

    public void setzBoardNo(int zBoardNo) {
        this.zBoardNo = zBoardNo;
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
        return SplittingRatio;
    }

    public void setSplittingRatio(String splittingRatio) {
        SplittingRatio = splittingRatio;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }
}
