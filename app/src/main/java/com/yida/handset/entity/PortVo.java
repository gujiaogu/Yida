package com.yida.handset.entity;

import java.util.Date;

/**
 * Created by hhb on 2015/10/18.
 */
public class PortVo {
    private int portId;
    private String etag;
    private String indicator;
    private int sequence;
    private String remark;
    private int fiberboxId;
    private String createTime;
    private int createBy;
    private String updateTime;
    private int updateBy;

    public int getPortId() {
        return portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getFiberboxId() {
        return fiberboxId;
    }

    public void setFiberboxId(int fiberboxId) {
        this.fiberboxId = fiberboxId;
    }

    public int getCreateBy() {
        return createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public int getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(int updateBy) {
        this.updateBy = updateBy;
    }
}
