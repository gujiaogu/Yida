package com.yida.handset.entity;

import com.yida.handset.workorder.WorkOrderFragment;

/**
 * Created by gujiao on 2015/10/24.
 */
public class WorkOrder {

    private int workId;
    private String orderType;
    private String siteName;
    private String status = WorkOrderFragment.orderTypes.get(WorkOrderFragment.STATUS_NO_ACCEPT);
    private String dateCompleted;
    private String remark;
    private String username;
    private String assignerName;

    public String getAssignerName() {
        return assignerName;
    }

    public void setAssignerName(String assignerName) {
        this.assignerName = assignerName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
