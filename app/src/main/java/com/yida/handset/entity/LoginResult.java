package com.yida.handset.entity;

/**
 * Created by gujiao on 15-10-13.
 */
public class LoginResult {

    private String code;
    private String message;
    private User object;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getObject() {
        return object;
    }

    public void setObject(User object) {
        this.object = object;
    }
}
