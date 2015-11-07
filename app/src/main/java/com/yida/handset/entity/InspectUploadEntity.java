package com.yida.handset.entity;

import java.util.List;

/**
 * Created by gujiao on 2015/11/5.
 */
public class InspectUploadEntity {
    private String result;
    private String token;
    private int workOrderId;
    private String inspectTime;
    private List<InspectItem> devices;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

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
