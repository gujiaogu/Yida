package com.yida.handset.sqlite;

import android.content.Context;
import android.content.Intent;

import com.yida.handset.ResourceActivity;

/**
 * Created by gujiao on 2015/10/22.
 */
public class TaskManager {

    private DatabaseHelper helper;
    private static TaskManager taskManager;
    private boolean isRunning = false;
    private boolean isFirst = false;

    private Context context;

    private TaskManager(Context context) {
        if (helper == null) {
            helper = DatabaseHelper.getInstance(context);
        }
        this.context = context;
    }

    public static TaskManager getInstance(Context context) {
        if (taskManager == null) {
            taskManager = new TaskManager(context);
        }
        return taskManager;
    }

    public void notifyFrameTask() {
        startFrameTask(helper, 0);
    }

    public void notifyContainerTask() {
        startContainer(helper, 0);
    }

    public void notifyFiberboxTask() {
        startFiberbox(helper, 0);
    }

    public void notifyPortTask() {
        startPortTask(helper, 0);
    }

    public void startNetUnitTask(DatabaseHelper helper) {
        new NetUnitTask(helper, this).execute();
    }

    public void startFrameTask(DatabaseHelper helper, int netUnitIndex) {
        if (netUnitIndex >= 0 && ResourceActivity.netUnits.size() > 0) {
            new FrameTask(helper, this).execute(String.valueOf(ResourceActivity.netUnits.get(netUnitIndex).getNetunitId()));
        } else {
            Intent intent = new Intent();
            intent.setAction(PortTask.TASK_FINISHED);
            this.context.sendBroadcast(intent);
        }
    }

    public void startContainer(DatabaseHelper helper, int frameIndex) {
        if (frameIndex >= 0 && ResourceActivity.frames.size() > 0) {
            new ContainerTask(helper, this).execute(String.valueOf(ResourceActivity.frames.get(frameIndex).getFrameId()));
        } else {
            Intent intent = new Intent();
            intent.setAction(PortTask.TASK_FINISHED);
            this.context.sendBroadcast(intent);
        }
    }

    public void startFiberbox(DatabaseHelper helper, int containerIndex) {
        if (containerIndex >= 0 && ResourceActivity.containers.size() > 0) {
            new FiberboxTask(helper, this).execute(String.valueOf(ResourceActivity.containers.get(containerIndex).getContainerId()));
        } else {
            Intent intent = new Intent();
            intent.setAction(PortTask.TASK_FINISHED);
            this.context.sendBroadcast(intent);
        }
    }

    public void startPortTask(DatabaseHelper helper, int fiberbox) {
        if (fiberbox >= 0 && ResourceActivity.fiberboxes.size() > 0) {
            new PortTask(context, helper, this).execute(String.valueOf(ResourceActivity.fiberboxes.get(fiberbox).getFiberboxId()));
        } else {
            Intent intent = new Intent();
            intent.setAction(PortTask.TASK_FINISHED);
            this.context.sendBroadcast(intent);
        }
    }

    public void setTaskStatus(boolean status) {
        isRunning = status;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isFirst() {
        return isFirst;
    }

}
