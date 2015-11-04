package com.yida.handset.entity;

/**
 * Created by gujiao on 2015/10/29.
 */
public class InspectResult extends ResultVo {
    private InspectOrder workOrder;

    public InspectOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(InspectOrder workOrder) {
        this.workOrder = workOrder;
    }
}
