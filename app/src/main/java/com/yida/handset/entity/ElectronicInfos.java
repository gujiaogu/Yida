package com.yida.handset.entity;

import java.util.List;

/**
 * Created by gujiao on 2015/10/17.
 */
public class ElectronicInfos {
    private String vendorID;
    private String deviceID;
    private List<ElectronicInfo> electronicInfo;

    public String getVendorID() {
        return vendorID;
    }

    public void setVendorID(String vendorID) {
        this.vendorID = vendorID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public List<ElectronicInfo> getElectronicInfo() {
        return electronicInfo;
    }

    public void setElectronicInfo(List<ElectronicInfo> electronicInfo) {
        this.electronicInfo = electronicInfo;
    }
}
