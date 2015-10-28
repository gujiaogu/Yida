package com.yida.handset.entity;

import java.util.List;

/**
 * Created by gujiao on 2015/10/26.
 */
public class ConstructOrderRoute implements OpticalItem{
    private int id;
    private String name;
    private String type;
    private List<OpticalRoute> opticalRoute;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<OpticalRoute> getOpticalRoute() {
        return opticalRoute;
    }

    public void setOpticalRoute(List<OpticalRoute> opticalRoute) {
        this.opticalRoute = opticalRoute;
    }
}
