package com.yida.handset.entity;

/**
 * Created by gujiao on 2015/12/22.
 */
public class CollectionOrderEntity {
    private int assignmentId;
    private String type;
    private int assigner;
    private int assignee;
    private String startDate;
    private String finishDate;
    private String status;
    private String code;
    private int updateBy;
    private int netunitId;
    private int frameId;
    private String deviceType;
    private String ip;
    private String deviceName;

    public int getAssignmentId() {
        return assignmentId;
    }

    public String getType() {
        return type;
    }

    public int getAssigner() {
        return assigner;
    }

    public int getAssignee() {
        return assignee;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public int getUpdateBy() {
        return updateBy;
    }

    public int getNetunitId() {
        return netunitId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getFrameId() {
        return frameId;
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }
}
