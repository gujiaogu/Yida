package com.yida.handset;

import android.bluetooth.BluetoothDevice;

/**
 * Created by gujiao on 15-9-23.
 */
public class BluetoothDeviceWrapper {

    private BluetoothDevice device;
    private String address;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
