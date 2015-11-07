package com.yida.handset.entity;

import java.util.List;

/**
 * Created by gujiao on 2015/11/6.
 */
public class ConfigurationResult extends ResultVo {
    private List<ConfigurationEntity> devices;

    public List<ConfigurationEntity> getDevices() {
        return devices;
    }

    public void setDevices(List<ConfigurationEntity> devices) {
        this.devices = devices;
    }
}
