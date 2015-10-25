package com.yida.handset.entity;

import java.util.List;

/**
 * Created by hhb on 2015/10/18.
 */
public class ResourceVo extends ResultVo {
    private List<NetUnitVo> netUnits;
    private List<FrameVo> frames;
    private List<ContainerVo> containers;
    private List<FiberboxVo> fiberboxes;
    private List<PortVo> ports;

    public List<NetUnitVo> getNetUnits() {
        return netUnits;
    }

    public void setNetUnits(List<NetUnitVo> netUnits) {
        this.netUnits = netUnits;
    }

    public List<FrameVo> getFrames() {
        return frames;
    }

    public void setFrames(List<FrameVo> frames) {
        this.frames = frames;
    }

    public List<ContainerVo> getContainers() {
        return containers;
    }

    public void setContainers(List<ContainerVo> containers) {
        this.containers = containers;
    }

    public List<FiberboxVo> getFiberboxes() {
        return fiberboxes;
    }

    public void setFiberboxes(List<FiberboxVo> fiberboxes) {
        this.fiberboxes = fiberboxes;
    }

    public List<PortVo> getPorts() {
        return ports;
    }

    public void setPorts(List<PortVo> ports) {
        this.ports = ports;
    }
}
