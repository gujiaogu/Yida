package com.yida.handset.entity;

/**
 * Created by gujiao on 2015/11/10.
 */
public class LogEntity {

    public static final String TYPE_LOGIN = "登录";
    public static final String TYPE_LOGOUT= "退出";

    private String username;
    private String time;
    private String type;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
