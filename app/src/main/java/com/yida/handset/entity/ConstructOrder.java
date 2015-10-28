package com.yida.handset.entity;

import java.util.List;

/**
 * Created by gujiao on 2015/10/26.
 */
public class ConstructOrder {

    private int id;
    List<ConstructOrderRoute> routeList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ConstructOrderRoute> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<ConstructOrderRoute> routeList) {
        this.routeList = routeList;
    }
}
