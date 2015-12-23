package com.yida.handset.entity;

/**
 * Created by gujiao on 2015/12/22.
 */
public class CollectionOrderEntity {
    private int assignmentId;
    private String type;
    private int assigner;
    private int assignee;
    private String startDate;
    private String finishDate;
    private String status;
    private String code;
    private int updateBy;
    private int netunitId;

    public int getAssignmentId() {
        return assignmentId;
    }

    public String getType() {
        return type;
    }

    public int getAssigner() {
        return assigner;
    }

    public int getAssignee() {
        return assignee;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public int getUpdateBy() {
        return updateBy;
    }

    public int getNetunitId() {
        return netunitId;
    }
}
