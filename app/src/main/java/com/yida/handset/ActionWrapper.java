package com.yida.handset;

public class ActionWrapper {

    private int index;
    private DrawerAction action;
    private String name;

    public ActionWrapper(int index, String name, DrawerAction action) {
        this.index = index;
        this.action = action;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public DrawerAction getAction() {
        return action;
    }

    public void setAction(DrawerAction action) {
        this.action = action;
    }
}
