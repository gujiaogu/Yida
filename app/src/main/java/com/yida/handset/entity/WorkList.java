package com.yida.handset.entity;

import java.util.List;

/**
 * Created by gujiao on 2015/10/24.
 */
public class WorkList extends ResultVo {
    private List<WorkOrder> workList;

    public List<WorkOrder> getWorkList() {
        return workList;
    }

    public void setWorkList(List<WorkOrder> workList) {
        this.workList = workList;
    }
}
