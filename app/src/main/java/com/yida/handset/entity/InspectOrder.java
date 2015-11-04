package com.yida.handset.entity;

import java.util.List;

/**
 * Created by gujiao on 2015/10/29.
 */
public class InspectOrder {
    private int workOrderId;
    private String inspectTime;
    private List<InspectItem> devices;

    public int getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getInspectTime() {
        return inspectTime;
    }

    public void setInspectTime(String inspectTime) {
        this.inspectTime = inspectTime;
    }

    public List<InspectItem> getDevices() {
        return devices;
    }

    public void setDevices(List<InspectItem> devices) {
        this.devices = devices;
    }
}
