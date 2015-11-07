package com.yida.handset.entity;

/**
 * Created by gujiao on 2015/11/6.
 */
public class ConfigurationEntity {
    private String deviceName;
    private String deviceID;
    private String deviceType;
    private String deviceIPAddr;
    private String deviceIPAddrMask;
    private String deviceIPGateway;
    private String NMSIPAddr;
    private String NMSTrapPort;
    private boolean NMSTrapEnable;
    private String NMSTrapSecurityName;
    private String SNMPGroupName;
    private String SNMPAuthority;
    private boolean SNMPViewEnable;
    private String SNMPViewName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceIPAddrMask() {
        return deviceIPAddrMask;
    }

    public void setDeviceIPAddrMask(String deviceIPAddrMask) {
        this.deviceIPAddrMask = deviceIPAddrMask;
    }

    public String getNMSTrapPort() {
        return NMSTrapPort;
    }

    public void setNMSTrapPort(String NMSTrapPort) {
        this.NMSTrapPort = NMSTrapPort;
    }

    public String getSNMPGroupName() {
        return SNMPGroupName;
    }

    public void setSNMPGroupName(String SNMPGroupName) {
        this.SNMPGroupName = SNMPGroupName;
    }

    public String getSNMPViewName() {
        return SNMPViewName;
    }

    public void setSNMPViewName(String SNMPViewName) {
        this.SNMPViewName = SNMPViewName;
    }

    public String getSNMPAuthority() {
        return SNMPAuthority;
    }

    public void setSNMPAuthority(String SNMPAuthority) {
        this.SNMPAuthority = SNMPAuthority;
    }

    public String getNMSTrapSecurityName() {
        return NMSTrapSecurityName;
    }

    public void setNMSTrapSecurityName(String NMSTrapSecurityName) {
        this.NMSTrapSecurityName = NMSTrapSecurityName;
    }

    public boolean isNMSTrapEnable() {
        return NMSTrapEnable;
    }

    public void setNMSTrapEnable(boolean NMSTrapEnable) {
        this.NMSTrapEnable = NMSTrapEnable;
    }

    public boolean isSNMPViewEnable() {
        return SNMPViewEnable;
    }

    public void setSNMPViewEnable(boolean SNMPViewEnable) {
        this.SNMPViewEnable = SNMPViewEnable;
    }

    public String getNMSIPAddr() {
        return NMSIPAddr;
    }

    public void setNMSIPAddr(String NMSIPAddr) {
        this.NMSIPAddr = NMSIPAddr;
    }

    public String getDeviceIPGateway() {
        return deviceIPGateway;
    }

    public void setDeviceIPGateway(String deviceIPGateway) {
        this.deviceIPGateway = deviceIPGateway;
    }

    public String getDeviceIPAddr() {
        return deviceIPAddr;
    }

    public void setDeviceIPAddr(String deviceIPAddr) {
        this.deviceIPAddr = deviceIPAddr;
    }
}
