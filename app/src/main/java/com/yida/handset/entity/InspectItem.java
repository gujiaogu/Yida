package com.yida.handset.entity;

import java.util.List;

/**
 * Created by gujiao on 2015/10/29.
 */
public class InspectItem {
    private String deviceName;
    private int deviceId;
    private String deviceType;
    private String deviceSoftwareVersion;
    private String deviceHardwareVersion;
    private List<ResourceData> resourceData;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceSoftwareVersion() {
        return deviceSoftwareVersion;
    }

    public void setDeviceSoftwareVersion(String deviceSoftwareVersion) {
        this.deviceSoftwareVersion = deviceSoftwareVersion;
    }

    public String getDeviceHardwareVersion() {
        return deviceHardwareVersion;
    }

    public void setDeviceHardwareVersion(String deviceHardwareVersion) {
        this.deviceHardwareVersion = deviceHardwareVersion;
    }

    public List<ResourceData> getResourceData() {
        return resourceData;
    }

    public void setResourceData(List<ResourceData> resourceData) {
        this.resourceData = resourceData;
    }
}
