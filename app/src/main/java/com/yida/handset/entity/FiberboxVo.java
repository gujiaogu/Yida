package com.yida.handset.entity;

/**
 * Created by hhb on 2015/10/18.
 */
public class FiberboxVo {
    private int fiberboxId;
    private String code;
    private String type;
    private int msId;
    private String manufacture;
    private String productDate;
    private int holeNum;
    private String errInfo;
    private String remark;
    private int containerId;
    private String workStatus;
    private String createTime;
    private int createBy;
    private String updateTime;
    private int updateBy;
//    private List<PortVo> ports;

    public int getFiberboxId() {
        return fiberboxId;
    }

    public void setFiberboxId(int fiberboxId) {
        this.fiberboxId = fiberboxId;
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

    public int getMsId() {
        return msId;
    }

    public void setMsId(int msId) {
        this.msId = msId;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public int getHoleNum() {
        return holeNum;
    }

    public void setHoleNum(int holeNum) {
        this.holeNum = holeNum;
    }

    public String getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getContainerId() {
        return containerId;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
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
