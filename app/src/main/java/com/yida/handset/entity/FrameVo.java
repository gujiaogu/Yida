package com.yida.handset.entity;

import java.util.Date;

/**
 * Created by hhb on 2015/10/18.
 */
public class FrameVo {

    private int frameId;
    private String code;
    private String type;
    private String model;
    private String efUid;
    private String errInfo;
    private int fp;
    private String position;
    private String manufacturer;
    private String remark;
    private int netunitId;
    private String workStatus;
    private String createTime;
    private int createBy;
    private String updateTime;
    private int updateBy;
//    private List<ContainerVo> containers;

    public int getFrameId() {
        return frameId;
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEfUid() {
        return efUid;
    }

    public void setEfUid(String efUid) {
        this.efUid = efUid;
    }

    public String getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }

    public int getFp() {
        return fp;
    }

    public void setFp(int fp) {
        this.fp = fp;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getNetunitId() {
        return netunitId;
    }

    public void setNetunitId(int netunitId) {
        this.netunitId = netunitId;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(int updateBy) {
        this.updateBy = updateBy;
    }
}
